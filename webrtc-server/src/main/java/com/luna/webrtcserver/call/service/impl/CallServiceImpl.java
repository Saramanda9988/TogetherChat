package com.luna.webrtcserver.call.service.impl;

import com.luna.webrtcserver.call.cache.CallSessionCacheManager;
import com.luna.webrtcserver.call.dao.ParticipantDao;
import com.luna.webrtcserver.call.dao.SessionDao;
import com.luna.webrtcserver.call.domain.entity.Participant;
import com.luna.webrtcserver.call.domain.entity.Session;
import com.luna.webrtcserver.call.domain.request.CallingCancelRequest;
import com.luna.webrtcserver.call.domain.request.CallingRequest;
import com.luna.webrtcserver.call.domain.response.SessionInfoResponse;
import com.luna.webrtcserver.call.domain.response.SessionResponse;
import com.luna.webrtcserver.call.enums.SessionErrorEnum;
import com.luna.webrtcserver.call.enums.SessionStatusEnum;
import com.luna.webrtcserver.call.service.CallService;
import com.luna.webrtcserver.common.exception.BusinessException;
import com.luna.webrtcserver.common.utils.DistributedLockUtils;
import com.luna.webrtcserver.websocket.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CallServiceImpl implements CallService {

    private final SessionDao sessionDao;

    private final ParticipantDao participantDao;

    private final WebSocketService webSocketService;

    private final CallSessionCacheManager callSessionCacheManager;

    private final DistributedLockUtils distributedLockUtils;

    @Override
    @Transactional
    public SessionInfoResponse initiateCall(CallingRequest request, Long callerId) {
        // 获取分布式锁，确保同一用户只能有一个端发起会话
        DistributedLockUtils.LockInfo lockInfo = distributedLockUtils.tryLockCallSession(0L, callerId);
        if (!lockInfo.isLocked()) {
            throw new BusinessException(SessionErrorEnum.OPERATION_TOO_FREQUENT);
        }
        try {
            // 1. 创建通话会话
            Session session = Session.builder()
                    .creatorId(request.getCallerId())
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
            participantDao.saveBatch(participants);
            webSocketService.addSession(session, callerId);
            return SessionInfoResponse
                    .builder()
                    .sessionId(session.getSessionId())
                    .key(UUID.randomUUID().toString().replace("-", "").substring(0, 8))
                    .build();
        } finally {
            distributedLockUtils.releaseLock(lockInfo);
        }
    }

    @Override
    @Transactional
    public void endCall(CallingCancelRequest request, Long receiverId) {
        // 获取分布式锁
        DistributedLockUtils.LockInfo lockInfo = distributedLockUtils.tryLockCallSession(request.getSessionId(), receiverId);
        if (!lockInfo.isLocked()) {
            throw new BusinessException(SessionErrorEnum.OPERATION_TOO_FREQUENT);
        }

        try {
            // 1. 查询会话
            Session session = sessionDao.getById(request.getSessionId());
            session.setEndTime(LocalDateTime.now());
            sessionDao.updateById(session);
            webSocketService.endSession(session.getSessionId(), receiverId);
        } finally {
            // 释放锁
            distributedLockUtils.releaseLock(lockInfo);
        }
    }
    @Override
    public List<SessionResponse> getCallHistory(Long userId) {
        List<Long> sessionIds = participantDao.getByUserId(userId);
        if (sessionIds.isEmpty()) {
            return new ArrayList<>();
        }

        ArrayList<SessionResponse> sessionResponses = new ArrayList<>();
        for(Long sessionId : sessionIds) {
            Session session = sessionDao.getById(sessionId);
            SessionResponse response = SessionResponse.builder()
                    .callType(session.getCallType())
                    .sessionId(session.getSessionId())
                    .subject(session.getSubject())
                    .status(session.getStatus())
                    .createTime(session.getCreateTime())
                    .endTime(session.getEndTime())
                    .build();
            sessionResponses.add(response);
        }

        return sessionResponses;
    }

}