package com.luna.togetherchat.chat.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.luna.togetherchat.chat.domain.entity.message.MessageExtra;
import com.luna.togetherchat.chat.domain.request.message.ChatMessageRequest;
import com.luna.togetherchat.chat.enums.MessageStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

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
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName(value = "message", autoResultMap = true)
@Schema(description = "Message")
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @TableId(value = "message_id")
    @Schema(description = "全局的ID，全局唯一，用于表示")
    private Long messageID;

    @NotNull
    @TableField("sync_id")
    @Schema(description = "用于确定用户发送的消息顺序，是万有一失的严格递增，session级别的Id")
    private Long syncId;

    @NotNull
    @Schema(description = "消息的房间号")
    @TableField("room_id")
    private Long roomId;

    @NotNull
    @Schema(description = "用户id")
    @TableField("user_id")
    private Long userId;

    @NotNull
    @Schema(description = "角色id")
    @TableField("role_id")
    private Long roleId;

    @NotNull
    @Schema(description = "内容")
    @TableField("content")
    private String content;

    @NotNull
    @Schema(description = "说话人的这个时候的立绘")
    @TableField("avatar_id")
    private Long avatarId;

    @Schema(description = "BA那种，比如说人旁边飘过去一个问号这样子")
    @TableField("animation")
    private Integer animation;

    @Schema(description = "比如说，立绘的抖动，这种")
    @TableField("special_effects")
    private Integer specialEffects;

    @Schema(description = "回复的消息id")
    @TableField("reply_message_id")
    private Long replyMessageId;

    @NotNull
    @Schema(description = "消息状态")
    @TableField("status")
    private Integer status;

    @NotNull
    @Schema(description = "消息类型")
    @TableField("message_type")
    private Integer messageType;

    @NotNull
    @Schema(description = "位置")
    @TableField("position")
    private Double position;

    @Schema(description = "不同类型消息持有的额外信息")
    @TableField(value = "extra", typeHandler = JacksonTypeHandler.class)
    private MessageExtra extra;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    public Message(ChatMessageRequest request, Long userId) {
        this.roomId = request.getRoomId();
        this.messageType = request.getMessageType();
        this.roleId = request.getRoleId();
        this.avatarId = request.getAvatarId();
        this.content = request.getContent();
        this.replyMessageId = request.getReplayMessageId();
        this.userId = userId;
        this.status = MessageStatusEnum.NORMAL.getStatus();

        // 设置默认值
//        this.messageID = null; // 由数据库生成
//        this.syncId = null; // 由业务层设置
//        this.userId = null; // 由业务层设置
//        this.animation = 0; // 默认无动画
//        this.specialEffects = 0; // 默认无特效
//        this.status = 0; // 默认状态
//        this.position = 0.0; // 默认位置
    }
}
