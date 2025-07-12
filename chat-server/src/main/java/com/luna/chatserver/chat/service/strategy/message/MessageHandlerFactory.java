package com.luna.chatserver.chat.service.strategy.message;

import com.luna.chatserver.common.enums.CommonErrorEnum;
import com.luna.chatserver.common.utils.AssertUtil;

import java.util.HashMap;
import java.util.Map;

public class MessageHandlerFactory {
    private static final Map<Integer, AbstractMessageHandler> STRATEGY_MAP = new HashMap<>();

    public static void register(Integer code, AbstractMessageHandler strategy) {
        STRATEGY_MAP.put(code, strategy);
    }

    public static AbstractMessageHandler getStrategyNoNull(Integer code) {
        AbstractMessageHandler strategy = STRATEGY_MAP.get(code);
        AssertUtil.isNotEmpty(strategy, CommonErrorEnum.PARAM_VALID);
        return strategy;
    }
}
