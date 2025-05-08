package com.luna.togetherchat.group.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 群组成员信息响应
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "群组成员信息响应")
public class GroupMemberResponse {
    
    @Schema(description = "成员关系ID")
    private Long id;
    
    @Schema(description = "群组ID")
    private Long groupId;
    
    @Schema(description = "用户ID")
    private Long userId;
    
    @Schema(description = "用户昵称")
    private String nickname;
    
    @Schema(description = "用户头像URL")
    private String avatar;
    
    @Schema(description = "成员角色")
    private Integer role;
}
