package com.luna.togetherchat.chat.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author starrybamboo
 * @since 2024-09-03
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("message_mark")
@Schema(description = "MessageMark")
public class MessageMark implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "message_mark_id", type = IdType.AUTO)
    private Long messageMarkId;

    @TableField("message_id")
    private Long messageId;

    @TableField("user_id")
    private Long userId;

    @TableField("role_id")
    private Long roleId;

    @Schema(description = "消息类型")
    @TableField("type")
    private Long type;

    @Schema(description = "回复的")
    @TableField("avatar_id")
    private Long avatarId;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
