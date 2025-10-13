package com.luna.messageserver.message.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * @author LunaRain_079
 * @since 2025-10-12
 */
@Getter
@Setter
@TableName("message")
@Schema(name = "Message", description = "消息落库类")
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "消息Id，全局唯一")
    @TableId("message_id")
    private Long messageId;

    @Schema(description = "服务器消息顺序")
    @TableField("sync_id")
    private Integer syncId;

    @Schema(description = "消息属于的房间号")
    @TableField("group_id")
    private Long groupId;

    @Schema(description = "用户id")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "消息状态，是否被撤回，是否被编辑过")
    @TableField("status")
    private Integer status;

    @Schema(description = "消息类型，比如说是文本还是图片这种")
    @TableField("message_type")
    private Integer messageType;

    @Schema(description = "回复的消息")
    @TableField("reply_message")
    private String replyMessage;

    @Schema(description = "聊天消息的文字内容，图片消息也有文字内容（[图片]）")
    @TableField("content")
    private String content;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
