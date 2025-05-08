package com.luna.togetherchat.group.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 移除群组成员请求
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "移除群组成员请求")
public class GroupMemberRemoveRequest {
    
    @NotNull(message = "群组ID不能为空")
    @Schema(description = "群组ID")
    private Long groupId;
    
    @NotNull(message = "用户ID不能为空")
    @Schema(description = "要移除的用户ID")
    private Long userId;
}
