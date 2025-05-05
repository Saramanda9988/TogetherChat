package com.luna.togetherchat.chat.service.strategy.message;

import com.luna.togetherchat.chat.domain.entity.message.ForwardMessage;
import com.luna.togetherchat.chat.enums.MessageTypeEnum;
import org.springframework.stereotype.Component;

/**
 * 用于转发消息的处理类
 */
@Component
public class ForwardMessageHandler extends AbstractMessageHandler<ForwardMessage> {
    @Override
    MessageTypeEnum getMessageTypeEnum() {
        return MessageTypeEnum.FORWARD;
    }
}