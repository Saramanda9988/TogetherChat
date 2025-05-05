package com.luna.togetherchat.chat.service.strategy.message;

import com.luna.togetherchat.chat.domain.entity.message.SoundMessage;
import com.luna.togetherchat.chat.enums.MessageTypeEnum;
import org.springframework.stereotype.Component;

/**
 * @author 苍镜月
 * @version 1.0
 * @implNote
 */

@Component
public class SoundMessageHandler extends AbstractMessageHandler<SoundMessage> {
    @Override
    MessageTypeEnum getMessageTypeEnum() {
        return MessageTypeEnum.IMG;
    }
}
