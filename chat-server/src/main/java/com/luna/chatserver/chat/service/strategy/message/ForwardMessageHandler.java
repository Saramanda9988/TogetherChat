package com.luna.chatserver.chat.service.strategy.message;

import com.luna.chatserver.chat.domain.entity.message.ForwardMessage;
import com.luna.chatserver.chat.enums.MessageTypeEnum;
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