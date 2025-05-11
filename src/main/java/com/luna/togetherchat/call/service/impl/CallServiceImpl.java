package com.luna.togetherchat.call.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.luna.togetherchat.call.dao.ParticipantDao;
import com.luna.togetherchat.call.dao.SessionDao;
import com.luna.togetherchat.call.domain.entity.Session;
import com.luna.togetherchat.call.domain.entity.Participant;
import com.luna.togetherchat.call.domain.request.*;
import com.luna.togetherchat.call.domain.response.CallHistoryResponse;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class CallServiceImpl implements CallService {
    private final SessionDao sessionDao;

    private final ParticipantDao participantDao;

    private final WebSocketService webSocketService;
    
    @Override
    @Transactional
    public void initiateCall(CallingRequest request, Long callerId) {
        // 1. 创建通话会话
        Session session = Session.builder()
                .creatorId(request.getCallerId())
                .type(request.getCallType())
                .status(SessionStatusEnum.WAITING.getStatus())
                .subject(request.getSubject())
                .build();

        sessionDao.save(session);

        List<Long> onlineUserId = webSocketService.getOnlineUserId();
        List<Long> onlineParticipantUserId = request.getReceiverId().stream().filter(onlineUserId::contains).toList();
        List<Participant> participants = new ArrayList<>();
        // 2. 添加参与者记录
        onlineParticipantUserId.forEach(receiverId -> {
            Participant participant = Participant.builder()
                    .sessionId(session.getSessionId())
                    .userId(receiverId)
                    .build();
            participants.add(participant);
        });

        participantDao.saveBatch(participants);

        onlineParticipantUserId.forEach(participantUserId -> {
            WSBaseResp<WSCallSignalingAction> resp = new WSBaseResp<>();
            resp.setType(WSRespTypeEnum.CALL_SIGNAL.getType());
            resp.setData(WSCallSignalingAction
                    .builder()
                    .callerId(callerId)
                    .receiverId(participantUserId)
                    .type(CallingSignalEnum.OFFER.getType())
                    .callType(request.getCallType())
                    .expireTime(request.getExpireTime())
                    .build());
            // 4. 通过WebSocket服务发送OFFER信令
            webSocketService.sendToUid(resp, participantUserId);
        });
    }
    
    @Override
    @Transactional
    public void acceptCall(CallingAcceptRequest request, Long receiverId) {
        // 1. 查询会话信息
        
        // 2. 验证接收者身份
        
        // 3. 验证通话状态
        
        // 4. 更新会话状态
        
        // 5. 添加接收者参与记录
        
        // 6. 通过WebSocket发送接受通话信令
        
        // 7. 返回会话信息
    }
    
    @Override
    public void rejectCall(CallingRejectRequest request, Long receiverId) {
        // 1. 查询会话信息
        
        // 2. 验证接收者身份
        
        // 3. 验证通话状态
        
        // 4. 更新会话状态
        
        // 5. 通过WebSocket发送拒绝通话信令
    }
    
    @Override
    public void endCall(CallingCancelRequest request, Long userId) {
        // 1. 查询会话信息
        
        // 2. 验证用户身份
        
        // 3. 验证通话状态
        
        // 4. 更新会话状态
        
        // 5. 更新参与者记录
        
        // 6. 通过WebSocket发送取消通话信令

        // 向对方发送取消信号
    }
    
    @Override
    public CursorPageBaseResponse<CallHistoryResponse> getCallHistory(Long userId, Long cursor, Integer pageSize) {
        // 实现通话历史查询逻辑
        // 这里需要根据您的数据库设计实现分页查询
        // 为简化示例，这里省略具体实现
        return new CursorPageBaseResponse<>();
    }

}