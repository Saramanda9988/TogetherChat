package com.luna.togetherchat.group.domain.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 
 * </p>
 *
 * @author LunaRain_079
 * @since 2025-05-05
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("chat_group_member")
@Schema(name = "GroupMember", description = "")
public class GroupMember implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "成员关系ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "群组ID")
    @TableField("group_id")
    private Long groupId;

    @Schema(description = "用户ID")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "成员角色")
    @TableField("role")
    private Integer role;

    @Schema(description = "加入时间")
    @TableField("joined_at")
    private LocalDateTime joinedAt;
}
