package com.luna.togetherchat.group.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 群组信息响应
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "群组信息响应")
public class GroupResponse {
    
    @Schema(description = "群组ID")
    private Long id;
    
    @Schema(description = "群组名称")
    private String name;
    
    @Schema(description = "创建者用户ID")
    private Long creatorId;
    
    @Schema(description = "群组简介")
    private String description;
    
    @Schema(description = "群组头像URL")
    private String avatar;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
    
    @Schema(description = "最后更新时间")
    private LocalDateTime updatedAt;
    
    @Schema(description = "成员数量")
    private Integer memberCount;
}
