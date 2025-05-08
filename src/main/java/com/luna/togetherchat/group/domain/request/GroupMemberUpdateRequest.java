package com.luna.togetherchat.group.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新群组成员角色请求
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "更新群组成员角色请求")
public class GroupMemberUpdateRequest {
    
    @NotNull(message = "群组ID不能为空")
    @Schema(description = "群组ID")
    private Long groupId;
    
    @NotNull(message = "用户ID不能为空")
    @Schema(description = "要更新的用户ID")
    private Long userId;
    
    @NotNull(message = "角色类型不能为空")
    @Schema(description = "新的角色类型")
    private Integer role;
}
