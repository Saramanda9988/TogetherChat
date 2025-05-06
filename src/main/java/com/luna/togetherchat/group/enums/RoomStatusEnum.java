package com.luna.togetherchat.group.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Description: 房间状态枚举
 */
@AllArgsConstructor
@Getter
public enum RoomStatusEnum {
    ACTIVE(0, "正常"),
    DELETED(1, "删除"),
    ;

    private final Integer type;
    private final String desc;

    private static final Map<Integer, MemberTypeEnum> cache;

    static {
        cache = Arrays.stream(MemberTypeEnum.values()).collect(Collectors.toMap(MemberTypeEnum::getType, Function.identity()));
    }

    public static MemberTypeEnum of(Integer type) {
        return cache.get(type);
    }
}
