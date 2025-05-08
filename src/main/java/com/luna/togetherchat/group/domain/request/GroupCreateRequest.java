package com.luna.togetherchat.group.domain.request;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 创建群组请求
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "创建群组请求")
public class GroupCreateRequest {
    
    @NotBlank(message = "群组名称不能为空")
    @Size(max = 50, message = "群组名称不能超过50个字符")
    @Schema(description = "群组名称")
    private String name;
    
    @Size(max = 200, message = "群组简介不能超过200个字符")
    @Schema(description = "群组简介")
    private String description;
    
    @Schema(description = "群组头像URL")
    private String avatar;

    @Schema(description = "群组类型")
    private Integer type;
    
    @Schema(description = "初始成员用户ID列表")
    private List<Long> memberIds;
}
