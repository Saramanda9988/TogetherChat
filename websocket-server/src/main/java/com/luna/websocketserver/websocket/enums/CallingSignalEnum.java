package com.luna.websocketserver.websocket.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum CallingSignalEnum {
    OFFER(1, "请求通信"),
    ACCEPT(2, "接受通信"),
    REJECT(3, "拒绝通信"),
    CANCEL(4, "取消通信"),
    ;

    private final Integer type;
    private final String description;

    private static final Map<Integer, CallingSignalEnum> cache;

    static {
        cache = Arrays
                .stream(CallingSignalEnum.values())
                .collect(Collectors.toMap(CallingSignalEnum::getType, Function.identity()));
    }

    public static CallingSignalEnum of(Integer type) {
        return cache.get(type);
    }
}
