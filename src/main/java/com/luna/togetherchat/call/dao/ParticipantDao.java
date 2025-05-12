package com.luna.togetherchat.call.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luna.togetherchat.call.domain.entity.Participant;
import com.luna.togetherchat.call.mapper.ParticipantMapper;
import org.springframework.stereotype.Service;

@Service
public class ParticipantDao extends ServiceImpl<ParticipantMapper, Participant> {
    public Participant getBySessionIdAndUserId(Long sessionId, Long receiverId) {
        return lambdaQuery()
                .eq(Participant::getSessionId, sessionId)
                .eq(Participant::getUserId, receiverId)
                .one();
    }
}
