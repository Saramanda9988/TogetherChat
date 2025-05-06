package com.luna.togetherchat.chat.domain.request.message;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.luna.togetherchat.chat.domain.entity.message.MessageExtra;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageUpdateRequest {
    @NotNull
    @Schema(description = "全局的ID，全局唯一，用于表示")
    private Long messageID;

    @NotNull
    @Schema(description = "用于确定用户发送的消息顺序，是万有一失的严格递增，session级别的Id")
    private Long syncId;

    @NotNull
    @Schema(description = "消息的房间号")
    private Long groupId;

    @NotNull
    @Schema(description = "用户id")
    private Long userId;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "比如说，立绘的抖动，这种")
    private Integer specialEffects;

    @Schema(description = "回复的消息id")
    private Long replyMessageId;

    @Schema(description = "消息类型")
    private Integer messageType;

    @Schema(description = "不同类型消息持有的额外信息")
    private MessageExtra extra;
}
