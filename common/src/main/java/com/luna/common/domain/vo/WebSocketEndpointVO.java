package com.luna.common.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "WebSocket服务端点信息")
public class WebSocketEndpointVO {

    @Schema(description = "服务主机地址")
    private String host;

    @Schema(description = "服务端口")
    private Integer port;

    @Schema(description = "WebSocket连接URL")
    private String wsUrl;

    @Schema(description = "服务ID")
    private String serviceId;

    @Schema(description = "实例ID")
    private String instanceId;
}
