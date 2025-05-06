package com.luna.togetherchat.group.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Description: 成员角色枚举
 */
@AllArgsConstructor
@Getter
public enum MemberTypeEnum {
    OWNER(1, "群主"),
    ADMINISTRATOR(2, "管理员"),
    MEMBER(3, "群成员"),
    BOT(4, "AI")
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
