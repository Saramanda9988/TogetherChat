package com.luna.common.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserClientDto {
    private Integer appId;

    private String userId;

    private Integer clientType;

    private String imei;
}
