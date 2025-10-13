package com.luna.conversationserver.conversation.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ConversationRoleEnum {
    SINGLE(0, "单聊"),
    MEMBER(1, "成员"),
    ADMIN(2, "管理员"),
    OWNER(3, "群主"),;

    private final Integer type;
    private final String description;
}
