package com.luna.togetherchat.chat.domain.request.message;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDeleteRequest {
    @NotNull
    @Schema(description = "全局的ID，全局唯一，用于表示")
    private Long messageID;

    @NotNull
    @Schema(description = "消息的房间号")
    private Long groupId;

    @NotNull
    @Schema(description = "消息所属的用户id")
    private Long ownerId;
}
