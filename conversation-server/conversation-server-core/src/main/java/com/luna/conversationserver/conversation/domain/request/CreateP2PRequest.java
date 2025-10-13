package com.luna.conversationserver.conversation.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建单聊会话请求
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "创建单聊会话请求")
public class CreateP2PRequest {

    @NotNull(message = "对方用户ID不能为空")
    @Schema(description = "对方用户ID")
    private Long targetUserId;
}