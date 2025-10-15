package com.luna.messageserver.message.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息类型枚举
 */
@AllArgsConstructor
@Getter
public enum MessageTypeEnum {
    TEXT(1, "文本消息"),
    IMAGE(2, "图片消息"),
    FILE(3, "文件消息"),
    VOICE(4, "语音消息"),
    VIDEO(5, "视频消息");

    private final Integer type;
    private final String description;
}