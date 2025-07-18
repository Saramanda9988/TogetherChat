package com.luna.chatserver.chat.event.producer;

import com.luna.chatserver.chat.domain.entity.Message;
import com.luna.common.constant.MQConstant;
import com.luna.common.utils.MQProducer;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
    @Resource
    private MQProducer mqProducer;

    public void savePushMsg(Message msg) {
        mqProducer.sendMsg(MQConstant.MESSAGE_SAVE_EXCHANGE, msg);
    }

    public void changePushMsg(Message msg) {
        mqProducer.sendDbChange(MQConstant.MESSAGE_CHANGE_EXCHANGE, msg);
    }
}
