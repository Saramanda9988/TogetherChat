package com.luna.imtcp.api.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessagePack<T> implements Serializable {
    /**
     * 发送者ID
     */
    private String userId;

    /**
     * 应用ID，标识是哪个租户应用
     */
    private Integer appId;

    /**
     * 接收方
     */
    private String toId;

    /**
     * 客户端标识
     */
    private int clientType;

    /**
     * 消息ID
     */
    private String messageId;

    /**
     * 客户端设备唯一标识
     */
    private String imei;

    /**
     * 命令字段
     */
    private Integer command;

    /**
     * 冗余字段，标识消息类型，和聊天业务相关
     */
    private Integer type;

    /**
     * 消息体
     */
    private T data;
}
