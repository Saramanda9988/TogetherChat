package com.luna.conversationserver.conversation.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ConversationMemberStateEnum {
    NORMAL(1, "正常"),
    MUTED(2, "禁言"),
    BLOCKED(3, "退出"),
    ;

    private final Integer type;
    private final String description;
}
