package com.luna.conversationserver.conversation.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ConversationTypeEnum {
    P2P(1, "单聊"),
    GROUP(2, "群聊");

    private final Integer type;
    private final String description;
}
