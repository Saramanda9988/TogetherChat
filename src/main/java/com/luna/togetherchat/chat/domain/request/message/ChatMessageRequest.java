package com.luna.togetherchat.chat.domain.request.message;

import com.luna.togetherchat.chat.domain.entity.message.MessageExtra;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 单条消息的发送请求体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageRequest {

    @NotNull
    @Schema(description = "房间id")
    private Long groupId;

    @NotNull
    @Schema(description = "消息类型")
    private Integer messageType;

    @NotBlank(message = "内容不能为空")
    @Size(max = 1024, message = "消息内容过长。")
    @Schema(description = "消息内容")
    private String content;

    @Schema(description = "回复的消息id,如果没有别传就好")
    private Long replayMessageId;

    /**
     * @see MessageExtra
     */
    @NotNull
    @Schema(description = "消息内容，类型不同传值不同.")
    private Object body;
}
