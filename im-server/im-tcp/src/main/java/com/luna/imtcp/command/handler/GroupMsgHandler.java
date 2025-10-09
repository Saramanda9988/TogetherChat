package com.luna.imtcp.command.handler;

import com.luna.imtcp.api.enums.CommandType;
import com.luna.imtcp.api.vo.WebMessage;
import com.luna.imtcp.command.CommandHandler;
import org.springframework.stereotype.Component;

@Component
public class GroupMsgHandler implements CommandHandler {
    @Override
    public void handle(WebMessage message) {

    }

    @Override
    public int commandType() {
        return CommandType.GROUP_MESSAGE.getType();
    }
}
