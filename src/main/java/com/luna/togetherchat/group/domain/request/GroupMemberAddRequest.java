package com.luna.togetherchat.group.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 添加群组成员请求
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "添加群组成员请求")
public class GroupMemberAddRequest {
    
    @NotNull(message = "群组ID不能为空")
    @Schema(description = "群组ID")
    private Long groupId;
    
    @NotNull(message = "用户ID列表不能为空")
    @Schema(description = "要添加的用户ID列表")
    private List<Long> userIds;
}
