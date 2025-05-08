package com.luna.togetherchat.group.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新群组请求
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "更新群组请求")
public class GroupUpdateRequest {
    
    @NotNull(message = "群组ID不能为空")
    @Schema(description = "群组ID")
    private Long id;
    
    @Size(max = 50, message = "群组名称不能超过50个字符")
    @Schema(description = "群组名称")
    private String name;
    
    @Size(max = 200, message = "群组简介不能超过200个字符")
    @Schema(description = "群组简介")
    private String description;
    
    @Schema(description = "群组头像URL")
    private String avatar;
}
