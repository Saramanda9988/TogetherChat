package com.luna.roomserver.room.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum MemberTypeEnum {
    LEADER(1, "群主"),
    MANAGER(2, "群成员"),
    MEMBER(3, "管理员"),
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
