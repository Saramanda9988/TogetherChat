package com.luna.common.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketInstanceVO {
    private String instanceId;

    private String serviceId;

    private String host;

    private Integer port;

    private Boolean secure;

    private String uri;

    private Map<String, String> metadata;

    private Boolean healthy;
}
