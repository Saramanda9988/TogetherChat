package com.luna.togetherchat.chat.domain.entity;


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
@TableName("chat_group")
@Schema(name = "Group", description = "")
public class Group implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "群组ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

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

    @Schema(description = "创建时间")
    @TableField("created_at")
    private LocalDateTime createdAt;

    @Schema(description = "最后更新时间")
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
