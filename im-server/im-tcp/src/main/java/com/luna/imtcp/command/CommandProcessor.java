package com.luna.imtcp.command;

import com.luna.common.utils.AssertUtil;
import com.luna.imtcp.api.vo.WebMessage;
import com.luna.imtcp.utils.UserChannelUtils;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CommandProcessor {
    private final Map<Integer, CommandHandler> handlerMap = new HashMap<>();

    public CommandProcessor(List<CommandHandler> handlers) {
        for (CommandHandler handler : handlers) {
            handlerMap.put(handler.commandType(), handler);
        }
    }

    public void handleCommand(int commandType, WebMessage payload, ChannelHandlerContext ctx) {
        CommandHandler handler = handlerMap.get(commandType);
        AssertUtil.isNotNull(handler, "No handler found for command type: " + commandType);
        handler.handle(payload);
    }
}
