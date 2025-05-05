package com.luna.togetherchat.websocket.domain.enums;

import com.luna.togetherchat.websocket.domain.vo.request.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Description: ws前端请求类型枚举
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-19
 */
@AllArgsConstructor
@Getter
public enum WSRespTypeEnum {
    MESSAGE(4, "新消息", WSMessage.class),
    INVALIDATE_TOKEN(6, "使前端的token失效，意味着前端需要重新登录", null),
    MARK(8, "消息标记", WSMsgMark.class),
    RECALL(9, "消息撤回", WSMsgRecall.class),
    MEMBER_CHANGE(11, "成员变动", WSMemberChange.class),
    ROLE_CHANGE(12, "角色变动", WSRoleChange.class),
    BACKGROUND_CHANGE(13, "群背景变动", WSBackgroundChange.class),
    ROOM_DISSOLVE(14,"房间解散", WSRoomDissolve.class);

    private final Integer type;
    private final String desc;
    private final Class dataClass;

    private static final Map<Integer, WSRespTypeEnum> cache;

    static {
        cache = Arrays.stream(WSRespTypeEnum.values()).collect(Collectors.toMap(WSRespTypeEnum::getType, Function.identity()));
    }

    public static WSRespTypeEnum of(Integer type) {
        return cache.get(type);
    }
}
