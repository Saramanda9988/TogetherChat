package com.luna.messageserver.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息状态枚举
 */
@AllArgsConstructor
@Getter
public enum MessageStatusEnum {
    NORMAL(0, "正常"),
    RECALLED(1, "撤回"),
    EDITED(2, "编辑");

    private final Integer status;
    private final String description;
}