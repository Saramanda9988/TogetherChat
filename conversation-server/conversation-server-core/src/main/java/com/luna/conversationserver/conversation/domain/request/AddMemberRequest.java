package com.luna.conversationserver.conversation.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 添加单个成员请求
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "添加成员请求")
public class AddMemberRequest {

    @NotNull(message = "会话ID不能为空")
    @Schema(description = "会话ID")
    private Long conversationId;

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "要添加的用户ID")
    private Long userId;

    @Schema(description = "成员昵称")
    private String nickname;
}