package com.luna.imtcp.command.handler;

import com.luna.common.utils.JsonUtils;
import com.luna.common.utils.RedisUtils;
import com.luna.imtcp.api.enums.CommandType;
import com.luna.imtcp.api.vo.WebMessage;
import com.luna.imtcp.api.vo.msgBody.MessageVOBody;
import com.luna.imtcp.command.CommandHandler;
import com.luna.imtcp.api.vo.msgBody.P2PMessageBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class P2PMsgHandler implements CommandHandler {
    @Override
    public void handle(WebMessage message) {
        log.info("P2PMsgHandler收到消息: {}", message);
        P2PMessageBody singleMsg = JsonUtils.toObj(message.getMessagePack(), P2PMessageBody.class);
        RedisUtils.
        MessageVOBody messageVOBody = MessageVOBody.builder().build();
    }

    @Override
    public int commandType() {
        return CommandType.SINGLE_MESSAGE.getType();
    }
}
