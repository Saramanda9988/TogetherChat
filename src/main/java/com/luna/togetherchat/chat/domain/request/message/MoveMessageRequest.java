package com.luna.togetherchat.chat.domain.request.message;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description: 消息移动请求体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoveMessageRequest {
    @NotNull
    @Schema(description = "消息id")
    private Long messageId;

    @Schema(description = "移动到此消息之前")
    private Long beforeMessageId;

    @Schema(description = "移动到此消息之后")
    private Long afterMessageId;
}
