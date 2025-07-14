package com.luna.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum WsDiscoveryErrorEnum implements ErrorEnum{

    NO_SERVER_FOUND(40001, "暂无可用的WebSocket服务"),
    SERVER_UNAVAILABLE(40002, "获取WebSocket服务失败"),
    FAIL_TO_GET_HEALTH(40003, "获取WebSocket服务健康状态失败"),
    FAIL_TO_GET_INSTANCE_LIST(40004, "获取实例列表失败")
    ;

    private final Integer code;
    private final String msg;

    @Override
    public Integer getErrorCode() {
        return this.code;
    }

    @Override
    public String getErrorMsg() {
        return this.msg;
    }
}
