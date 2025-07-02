package com.luna.togetherchat.websocket.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum WSReqTypeEnum {
    LOGIN(1, "登录"),
    HEARTBEAT(2, "心跳包"),
    MESSAGE(3, "消息"),
    ENTER(4, "进入房间"),
    OFFER(5, "沟通媒体设备信息"),
    ANSWER(6, "回复媒体设备信息"),
    CANDIDATE(7, "ICE 候选信息,交换网络信息"),
    LEAVE(8, "离开房间"),
    END(9, "结束房间"),
    JOIN(10, "加入房间"),
    REJECT(11, "拒绝加入房间"),
    CANCEL(12, "结束房间通话"),
    ;

    private final Integer type;
    private final String desc;

    private static Map<Integer, WSReqTypeEnum> cache;

    static {
        cache = Arrays.stream(WSReqTypeEnum.values()).collect(Collectors.toMap(WSReqTypeEnum::getType, Function.identity()));
    }

    public static WSReqTypeEnum of(Integer type) {
        return cache.get(type);
    }
}
