package com.luna.conversationserver.conversation.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 禁言/解除禁言成员请求
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "禁言/解除禁言成员请求")
public class MuteMemberRequest {

    @NotNull(message = "禁言状态不能为空")
    @Schema(description = "禁言状态：true=禁言，false=解除禁言")
    private Boolean muted;
}