package com.luna.chatserver.chat.service.strategy.message;

import com.luna.chatserver.chat.enums.MessageTypeEnum;
import org.springframework.stereotype.Component;


@Component
public class TextMessageHandler extends AbstractMessageHandler<String> {

    @Override
    MessageTypeEnum getMessageTypeEnum() { return MessageTypeEnum.TEXT; }
}