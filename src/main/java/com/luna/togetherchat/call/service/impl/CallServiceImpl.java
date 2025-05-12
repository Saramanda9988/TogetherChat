package com.luna.togetherchat.call.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.luna.togetherchat.call.cache.CallSessionCacheManager;
import com.luna.togetherchat.call.dao.ParticipantDao;
import com.luna.togetherchat.call.dao.SessionDao;
import com.luna.togetherchat.call.domain.entity.Session;
import com.luna.togetherchat.call.domain.entity.Participant;
import com.luna.togetherchat.call.domain.request.*;
import com.luna.togetherchat.call.domain.response.CallHistoryResponse;
import com.luna.togetherchat.call.enums.SessionErrorEnum;
import com.luna.togetherchat.call.enums.SessionTypeEnum;
import com.luna.togetherchat.common.utils.DistributedLockUtils;
import com.luna.togetherchat.websocket.domain.enums.CallingSignalEnum;
import com.luna.togetherchat.call.enums.SessionStatusEnum;
import com.luna.togetherchat.call.service.CallService;
import com.luna.togetherchat.common.domain.vo.response.CursorPageBaseResponse;
import com.luna.togetherchat.common.exception.BusinessException;
import com.luna.togetherchat.websocket.domain.enums.WSRespTypeEnum;
import com.luna.togetherchat.websocket.domain.vo.request.WSBaseResp;
import com.luna.togetherchat.websocket.domain.vo.request.WSCallSignalingAction;
import com.luna.togetherchat.websocket.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class CallServiceImpl implements CallService {
    // TODO:需不需要维护一个map，表示谁在会议中
    private final SessionDao sessionDao;

    private final ParticipantDao participantDao;

    private final WebSocketService webSocketService;

    private final CallSessionCacheManager callSessionCacheManager;

    private final DistributedLockUtils distributedLockUtils;

    @Override
    @Transactional
    public void initiateCall(CallingRequest request, Long callerId) {
        // 获取分布式锁，确保同一用户只能有一个端发起会话
        DistributedLockUtils.LockInfo lockInfo = distributedLockUtils.tryLockCallSession(0L, callerId);
        if (!lockInfo.isLocked()) {
            throw new BusinessException(SessionErrorEnum.OPERATION_TOO_FREQUENT);
        }
        try {
            // 1. 创建通话会话
            Session session = Session.builder()
                    .creatorId(request.getCallerId())
                    .sessionType(request.getSessionType())
                    .callType(request.getCallType())
                    .status(SessionStatusEnum.WAITING.getStatus())
                    .subject(request.getSubject())
                    .build();

            sessionDao.save(session);

            List<Long> onlineUserId = webSocketService.getOnlineUserId();
            List<Long> onlineParticipantUserId = request.getReceiverId().stream().filter(onlineUserId::contains).toList();
            List<Participant> participants = new ArrayList<>();
            // 2. 添加参与者记录
            onlineParticipantUserId.forEach(receiverId -> {
                // 过滤掉已在其他会话中的用户
                Long receiverSessionId = callSessionCacheManager.getUserSessionId(receiverId);
                if (receiverSessionId != null) {
                    return;
                }
                Participant participant = Participant.builder()
                        .sessionId(session.getSessionId())
                        .userId(receiverId)
                        .build();
                participants.add(participant);
            });

            if (participants.isEmpty()) {
                throw new BusinessException(SessionErrorEnum.NO_VALID_USER);
            }
            participantDao.saveBatch(participants); // TODO:可以重构，添加一个方法，前端可以访问对应的表，让前端选择合适的用户，而不是后端过滤

            onlineParticipantUserId.forEach(participantUserId -> {
                WSBaseResp<WSCallSignalingAction> resp = new WSBaseResp<>();
                resp.setType(WSRespTypeEnum.CALL_SIGNAL.getType());
                resp.setData(WSCallSignalingAction
                        .builder()
                        .callerId(callerId)
                        .receiverId(participantUserId)
                        .type(CallingSignalEnum.OFFER.getType())
                        .callType(request.getCallType())
                        .sessionType(request.getSessionType())
                        .expireTime(request.getExpireTime())
                        .build());
                // 4. 通过WebSocket服务发送OFFER信令
                webSocketService.sendToUid(resp, participantUserId);
            });
        } finally {
            distributedLockUtils.releaseLock(lockInfo);
        }
    }
    
    @Override
    @Transactional
    public void acceptCall(CallingAcceptRequest request, Long receiverId) {
        // 获取分布式锁
        DistributedLockUtils.LockInfo lockInfo = distributedLockUtils.tryLockCallSession(request.getSessionId(), receiverId);
        if (!lockInfo.isLocked()) {
            throw new BusinessException(SessionErrorEnum.OPERATION_TOO_FREQUENT);
        }
        
        try {
            // 1. 查询会话信息
            Session session = sessionDao.getById(request.getSessionId());
            if (Objects.isNull(session)) {
                throw new BusinessException(SessionErrorEnum.NO_SUCH_SESSION);
            }
            
            // 2. 验证接收者身份和信令类型
            if (!Objects.equals(receiverId, request.getReceiverId())) {
                return;
            }
            
            // 3. 验证通话状态
            if (Objects.equals(session.getStatus(), SessionStatusEnum.ENDED.getStatus())) {
                throw new BusinessException(SessionErrorEnum.SESSION_ALREADY_ENDED);
            }
            
            // 3.1 检查用户是否已在其他会话中
            Long existingSessionId = callSessionCacheManager.getUserSessionId(receiverId);
            if (existingSessionId != null && !existingSessionId.equals(request.getSessionId())) {
                throw new BusinessException(SessionErrorEnum.USER_ALREADY_IN_SESSION);
            }
            
            // 4. 更新会话状态
            if (Objects.equals(session.getStatus(), SessionStatusEnum.WAITING.getStatus())) {
                session.setStatus(SessionStatusEnum.PERFORMING.getStatus());
                sessionDao.updateById(session);
            }
            
            // 5. 添加接收者参与记录
            Participant participant = participantDao.getBySessionIdAndUserId(request.getSessionId(), receiverId);
            if (Objects.nonNull(participant.getJoinTime())) {
                throw new BusinessException(SessionErrorEnum.REPEAT_JOIN);
            }
            
            participant.setJoinTime(LocalDateTime.now());
            participantDao.updateById(participant);
            
            // 5.1 更新缓存，记录用户已加入会话
            callSessionCacheManager.joinSession(receiverId, request.getSessionId());
            
            // 6. 通过WebSocket发送接受通话信令
            WSBaseResp<WSCallSignalingAction> resp = new WSBaseResp<>();
            resp.setType(WSRespTypeEnum.CALL_SIGNAL.getType());
            resp.setData(WSCallSignalingAction
                    .builder()
                    .callerId(receiverId)
                    .receiverId(request.getCallerId())
                    .type(CallingSignalEnum.ACCEPT.getType())
                    .callType(session.getCallType())
                    .sessionType(session.getSessionType())
                    .build());
            webSocketService.sendToUid(resp, request.getCallerId());
        } finally {
            // 释放锁
            distributedLockUtils.releaseLock(lockInfo);
        }
    }
    
    @Override
    public void endCall(CallingCancelRequest request, Long receiverId) {
        // 获取分布式锁
        DistributedLockUtils.LockInfo lockInfo = distributedLockUtils.tryLockCallSession(request.getSessionId(), receiverId);
        if (!lockInfo.isLocked()) {
            throw new BusinessException(SessionErrorEnum.OPERATION_TOO_FREQUENT);
        }
        
        try {
            // 1. 查询会话信息
            Session session = sessionDao.getById(request.getSessionId());
            if (Objects.isNull(session)) {
                throw new BusinessException(SessionErrorEnum.NO_SUCH_SESSION);
            }
            
            // 2. 验证接收者身份和信令类型
            if (!Objects.equals(receiverId, request.getReceiverId())) {
                return;
            }
            
            // 3. 验证通话状态
            if (Objects.equals(session.getStatus(), SessionStatusEnum.ENDED.getStatus())) {
                throw new BusinessException(SessionErrorEnum.SESSION_ALREADY_ENDED);
            }
            
            // 5. 添加接收者参与记录
            Participant participant = participantDao.getBySessionIdAndUserId(request.getSessionId(), receiverId);
            if (Objects.nonNull(participant.getLeaveTime())) {
                throw new BusinessException(SessionErrorEnum.USER_ALREADY_LEAVE);
            }
            
            participant.setLeaveTime(LocalDateTime.now());
            participantDao.updateById(participant);
            
            // 更新缓存
            callSessionCacheManager.leaveSession(receiverId, request.getSessionId());
            
            // 检查是否所有参与者都已离开
            Set<Object> remainingUsers = callSessionCacheManager.getSessionUsers(request.getSessionId());
            if (remainingUsers == null || remainingUsers.isEmpty()) {
                session.setStatus(SessionStatusEnum.ENDED.getStatus());
                sessionDao.updateById(session);
                callSessionCacheManager.endSession(request.getSessionId());
            }
            
            // 6. 通过WebSocket发送取消通话信令
            WSBaseResp<WSCallSignalingAction> resp = new WSBaseResp<>();
            resp.setType(WSRespTypeEnum.CALL_SIGNAL.getType());
            resp.setData(WSCallSignalingAction
                    .builder()
                    .callerId(receiverId)
                    .receiverId(request.getCallerId())
                    .type(CallingSignalEnum.CANCEL.getType())
                    .sessionType(request.getSessionType())
                    .callType(session.getCallType())
                    .cancelReason(request.getCancelReason())
                    .build());
            webSocketService.sendToUid(resp, request.getCallerId());
        } finally {
            // 释放锁
            distributedLockUtils.releaseLock(lockInfo);
        }
    }

    /**
     * 拒绝会话请求
     *
     * @param request 拒绝请求
     * @param receiverId 接收者ID
     */
    @Override
    public void rejectCall(CallingRejectRequest request, Long receiverId) {
        // 获取分布式锁
        DistributedLockUtils.LockInfo lockInfo = distributedLockUtils.tryLockCallSession(request.getSessionId(), receiverId);
        if (!lockInfo.isLocked()) {
            throw new BusinessException(SessionErrorEnum.OPERATION_TOO_FREQUENT);
        }
        try {
            // 1. 查询会话信息
            Session session = sessionDao.getById(request.getSessionId());
            if (Objects.isNull(session)) {
                throw new BusinessException(SessionErrorEnum.NO_SUCH_SESSION);
            }
            // 2. 验证接收者身份和信令类型
            if (!Objects.equals(receiverId, request.getReceiverId())) {
                return;
            }
            // 3. 验证通话状态
            if (Objects.equals(session.getStatus(), SessionStatusEnum.ENDED.getStatus())) {
                throw new BusinessException(SessionErrorEnum.SESSION_ALREADY_ENDED);
            }
            // 4. 查看说单聊还是群聊
            if (Objects.equals(session.getSessionType(), SessionTypeEnum.ONE_TO_ONE.getType())) {
                // 单聊直接结束会话
                session.setStatus(SessionStatusEnum.ENDED.getStatus());
                sessionDao.updateById(session);
                callSessionCacheManager.endSession(request.getSessionId());
            };

            // 6. 通过WebSocket发送取消通话信令
            WSBaseResp<WSCallSignalingAction> resp = new WSBaseResp<>();
            resp.setType(WSRespTypeEnum.CALL_SIGNAL.getType());
            resp.setData(WSCallSignalingAction
                    .builder()
                    .callerId(receiverId)
                    .receiverId(request.getCallerId())
                    .type(CallingSignalEnum.REJECT.getType())
                    .sessionType(request.getSessionType())
                    .callType(session.getCallType())
                    .rejectReason(request.getRejectReason())
                    .build());
            webSocketService.sendToUid(resp, request.getCallerId());
        } finally {
            // 释放锁
            distributedLockUtils.releaseLock(lockInfo);
        }
    }
    
    @Override
    public CursorPageBaseResponse<CallHistoryResponse> getCallHistory(Long userId, Long cursor, Integer pageSize) {
        // 实现通话历史查询逻辑
        // 这里需要根据您的数据库设计实现分页查询
        // 为简化示例，这里省略具体实现
        return new CursorPageBaseResponse<>();
    }

}