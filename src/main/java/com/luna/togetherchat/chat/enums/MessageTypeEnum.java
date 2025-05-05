package com.luna.togetherchat.chat.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum MessageTypeEnum {
    TEXT(1, "正常消息"),
    IMG(2, "图片"),
    FILE(3, "文件"),
    SYSTEM(4, "系统消息"),
    FORWARD(5, "转发消息"),
    SOUND(7, "语音消息")
    ;

    private final Integer type;
    private final String description;

    private static final Map<Integer, MessageTypeEnum> cache;

    static {
        cache = Arrays
                .stream(MessageTypeEnum.values())
                .collect(Collectors.toMap(MessageTypeEnum::getType, Function.identity()));
    }

    public static MessageTypeEnum of(Integer type) {
        return cache.get(type);
    }
}
