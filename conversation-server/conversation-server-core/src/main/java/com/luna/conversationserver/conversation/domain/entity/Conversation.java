package com.luna.conversationserver.conversation.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * <p>
 * 
 * </p>
 *
 * @author LunaRain_079
 * @since 2025-10-13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("conversation")
@Schema(name = "Conversation", description = "")
public class Conversation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "群组ID")
    @TableId(value = "conversation_id", type = IdType.AUTO)
    private Long conversationId;

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
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "最后更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @Schema(description = "群组类型 1单聊 2群聊")
    @TableField("type")
    private Integer type;

    @Schema(description = "房间状态 0正常 1删除")
    @TableField("status")
    private Integer status;
}
