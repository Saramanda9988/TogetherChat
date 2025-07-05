package com.luna.togetherchat.call.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "会话信息响应体")
public class SessionInfoResponse {
    @Schema(description = "会话ID")
    private Long sessionId;

    @Schema(description = "会话密钥")
    private String key;
}
