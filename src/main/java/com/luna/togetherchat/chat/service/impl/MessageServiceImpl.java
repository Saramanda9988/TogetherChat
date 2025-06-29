package com.luna.togetherchat.chat.service.impl;

import com.luna.togetherchat.chat.domain.entity.Message;
import com.luna.togetherchat.chat.event.producer.MQProducer;
import com.luna.togetherchat.chat.service.MessageService;
import com.luna.togetherchat.common.constant.MQConstant;
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
