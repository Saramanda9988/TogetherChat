package com.luna.togetherchat.chat.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description: 消息标记请求
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageMarkRequest {
    @NotNull
    @Schema(description = "消息id")
    private Long messageId;

    @NotNull
    @Schema(description = "房间id")
    private Long roomId;

    @NotNull
    @Schema(description = "标记类型 1角色反应")
    private Integer markType;

    @NotNull
    @Schema(description = "动作类型 1确认 2取消")
    private Integer actType;

    @NotNull
    @Schema(description = "反应的头像")
    private Long avatarId;

}
