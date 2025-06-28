package com.luna.togetherchat.chat.service.impl;

import com.luna.togetherchat.chat.domain.entity.Message;
import com.luna.togetherchat.chat.event.MQProducer;
import com.luna.togetherchat.common.constant.MQConstant;
import com.luna.togetherchat.websocket.domain.dto.PushMessageDTO;
import com.luna.togetherchat.websocket.domain.vo.request.WSBaseResp;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class MessageSaveService {
    @Resource
    private MQProducer mqProducer;

    public void savePushMsg(Message msg) {
        mqProducer.sendMsg(MQConstant.MESSAGE_SAVE_EXCHANGE, msg);
    }
}
