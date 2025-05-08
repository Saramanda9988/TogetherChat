package com.luna.togetherchat.group.domain.entity;


import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
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
@TableName("group")
@Schema(name = "Group", description = "群组实体类")
public class Group implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "群组ID")
    @TableId(value = "group_id", type = IdType.AUTO)
    private Long groupId;

    @Schema(description = "群组名称")
    @TableField("name")
    private String name;

    @Schema(description = "创建者用户ID")
    @TableField("creator_id")
    private Long creatorId;

    @Schema(description = "群组简介")
    @TableField("description")
    private String description;

    @Schema(description = "群组头像URL")
    @TableField("avatar")
    private String avatar;

    @Schema(description = "群组类型")
    @TableField("type")
    private Integer type;

    @Schema(description = "房间状态 0正常 1删除")
    @TableField("status")
    private Integer status;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
