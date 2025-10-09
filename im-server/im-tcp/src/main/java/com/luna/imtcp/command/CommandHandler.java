package com.luna.imtcp.command;

import com.luna.imtcp.api.enums.CommandType;
import com.luna.imtcp.api.vo.WebMessage;

public interface CommandHandler {
    void handle(WebMessage message);

    int commandType();
}
