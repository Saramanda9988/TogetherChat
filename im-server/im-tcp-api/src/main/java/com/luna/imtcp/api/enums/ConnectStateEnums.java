package com.luna.imtcp.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ConnectStateEnums {
    /**
     * 连接状态枚举
     */
    CONNECTED(1, "已连接"),
    DISCONNECTED(0, "未连接");

    private final Integer code;
    private final String description;
}
