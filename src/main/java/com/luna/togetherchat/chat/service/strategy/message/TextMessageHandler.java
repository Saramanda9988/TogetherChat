package com.luna.togetherchat.chat.service.strategy.message;

import com.luna.togetherchat.chat.enums.MessageTypeEnum;
import org.springframework.stereotype.Component;


@Component
public class TextMessageHandler extends AbstractMessageHandler<String> {

    @Override
    MessageTypeEnum getMessageTypeEnum() { return MessageTypeEnum.TEXT; }
}