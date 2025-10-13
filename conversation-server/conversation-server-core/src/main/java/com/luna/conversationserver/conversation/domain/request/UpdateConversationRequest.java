package com.luna.conversationserver.conversation.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新会话信息请求
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "更新会话信息请求")
public class UpdateConversationRequest {

    @Size(max = 50, message = "会话名称长度不能超过50个字符")
    @Schema(description = "会话名称")
    private String name;

    @Size(max = 200, message = "会话简介长度不能超过200个字符")
    @Schema(description = "会话简介")
    private String description;

    @Schema(description = "会话头像URL")
    private String avatar;
}