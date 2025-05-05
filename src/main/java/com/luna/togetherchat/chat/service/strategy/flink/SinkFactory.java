package com.luna.togetherchat.chat.service.strategy.flink;

import com.luna.togetherchat.chat.service.strategy.flink.sink.AbstractSink;
import com.luna.togetherchat.common.enums.CommonErrorEnum;
import com.luna.togetherchat.common.utils.AssertUtil;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SinkFactory {

    private static final Map<String, AbstractSink> STRATEGY_MAP = new HashMap<>();

    public static void register(String code, AbstractSink strategy) {
        STRATEGY_MAP.put(code, strategy);
    }

    public static AbstractSink getStrategyNoNull(String code) {
        AbstractSink strategy = STRATEGY_MAP.get(code);
        AssertUtil.isNotEmpty(strategy, CommonErrorEnum.PARAM_VALID);
        return strategy;
    }
}
