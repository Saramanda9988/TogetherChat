package com.luna.chatserver.chat.service.impl;

import com.luna.chatserver.chat.domain.entity.Message;
import com.luna.chatserver.chat.event.producer.MQProducer;
import com.luna.chatserver.chat.service.MessageService;
import com.luna.chatserver.common.constant.MQConstant;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl implements MessageService {
    @Resource
    private MQProducer mqProducer;

    public void savePushMsg(Message msg) {
        mqProducer.sendMsg(MQConstant.MESSAGE_SAVE_EXCHANGE, msg);
    }

    public void changePushMsg(Message msg) {
        mqProducer.sendDbChange(MQConstant.MESSAGE_CHANGE_EXCHANGE, msg);
    }
}
