package com.luna.common.enums;

import com.luna.common.domain.vo.chat.WSMemberChange;
import com.luna.common.domain.vo.chat.WSRoleChange;
import com.luna.common.domain.vo.chat.WSRoomDissolve;
import com.luna.common.domain.vo.response.WSMessageResp;
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
    // TODO:需要进行修改
    DIRECT_MESSAGE(1,"私聊新消息", WSMessageResp.class),
    MESSAGE(4, "群聊新消息", WSMessageResp.class),

    MEMBER_CHANGE(11, "成员变动", WSMemberChange.class),
    ROLE_CHANGE(12, "角色变动", WSRoleChange.class),
    ROOM_DISSOLVE(14,"房间解散", WSRoomDissolve.class),

    INVALIDATE_TOKEN(100, "使前端的token失效，意味着前端需要重新登录", null)
    ;

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