package com.luna.gatewayserver;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
// 网关没有引用common包，这里单独进行了处理
public class UserClientDto {
    private Integer appId;

    private String userId;

    private Integer clientType;

    private String imei;
}
