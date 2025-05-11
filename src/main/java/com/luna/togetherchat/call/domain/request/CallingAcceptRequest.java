package com.luna.togetherchat.call.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CallingAcceptRequest {
    @Schema(description = "发起信令的用户ID")
    private Long callerId;

    @Schema(description = "会话id")
    private Long sessionId;

    @Schema(description = "信令类型")
    private Integer type;
}
