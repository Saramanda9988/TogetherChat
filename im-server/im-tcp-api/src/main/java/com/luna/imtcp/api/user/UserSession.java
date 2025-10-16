package com.luna.imtcp.api.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSession implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;

    private Integer appId;

    /**
     * 端标识：web端、pc端、移动端
     */
    private Integer clientType;
    
    /**
     * SDK 版本号，对接前端传入的版本号做后端相应逻辑
     */
    private Integer version;
    
    /**
     * 连接状态【1.在线、2.离线】
     */
    private Integer connectState;

    private Integer brokerId;

    private String brokerIp;

    private String brokerPort;

    private String brokerRpcPort;

    private String imei;
}
