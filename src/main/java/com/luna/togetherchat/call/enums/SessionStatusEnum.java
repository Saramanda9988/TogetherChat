package com.luna.togetherchat.call.enums;

import com.luna.togetherchat.chat.enums.MessageStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum SessionStatusEnum {
    WAITING(0, "正常"),
    PERFORMING(1, "删除"),
    ENDED(2, "修改"),
    ;

    private final Integer status;
    private final String desc;

    private static final Map<Integer, MessageStatusEnum> cache;

    static {
        cache = Arrays.stream(MessageStatusEnum.values()).collect(Collectors.toMap(MessageStatusEnum::getStatus, Function.identity()));
    }

    public static MessageStatusEnum of(Integer type) {
        return cache.get(type);
    }
}
