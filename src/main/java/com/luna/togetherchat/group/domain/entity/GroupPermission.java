package com.luna.togetherchat.group.domain.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
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
@TableName("chat_group_permission")
@Schema(name = "GroupPermission", description = "")
public class GroupPermission implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "权限ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "群组ID")
    @TableField("group_id")
    private Long groupId;

    @Schema(description = "适用角色")
    @TableField("role")
    private String role;

    @Schema(description = "权限类型")
    @TableField("permission_type")
    private String permissionType;

    @Schema(description = "是否启用该权限")
    @TableField("enabled")
    private Boolean enabled;
}
