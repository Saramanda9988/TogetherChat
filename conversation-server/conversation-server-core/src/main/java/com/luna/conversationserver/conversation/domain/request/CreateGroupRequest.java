package com.luna.conversationserver.conversation.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 创建群聊请求
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "创建群聊请求")
public class CreateGroupRequest {

    @NotBlank(message = "群聊名称不能为空")
    @Size(max = 50, message = "群聊名称长度不能超过50个字符")
    @Schema(description = "群聊名称")
    private String name;

    @Schema(description = "群聊简介")
    @Size(max = 200, message = "群聊简介长度不能超过200个字符")
    private String description;

    @Schema(description = "群聊头像URL")
    private String avatar;

    @Schema(description = "初始成员用户ID列表（不包含创建者）")
    private List<Long> memberIds;
}