package com.luna.conversationserver.conversation.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 修改成员角色请求
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "修改成员角色请求")
public class UpdateMemberRoleRequest {

    @NotNull(message = "角色不能为空")
    @Schema(description = "新角色：1=成员，2=管理员，3=群主")
    private Integer role;
}