package com.luna.messageserver.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息类型枚举
 */
@AllArgsConstructor
@Getter
public enum MessageTypeEnum {
    ACK(0, "收到消息"),
    TEXT(1, "文本消息"),
    IMAGE(2, "图片消息"),
    FILE(3, "文件消息"),;

    private final Integer type;
    private final String description;
}