package com.luna.chatserver.chat.service.strategy.message;

import com.luna.chatserver.chat.domain.entity.message.ImageMessage;
import com.luna.chatserver.chat.enums.MessageTypeEnum;
import org.springframework.stereotype.Component;

/**
 * @author 苍镜月
 * @version 1.0
 * @implNote
 */

@Component
public class ImageMessageHandler extends AbstractMessageHandler<ImageMessage> {
    @Override
    MessageTypeEnum getMessageTypeEnum() {
        return MessageTypeEnum.IMG;
    }
}
