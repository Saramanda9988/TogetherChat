package com.luna.togetherchat.chat.domain.request.message;

import com.luna.togetherchat.common.domain.vo.request.CursorPageBaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


/**
 * 翻页类的拓展，多了一个 roomID
* */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "聊天分页请求")
public class ChatMessagePageRequest extends CursorPageBaseRequest {
    @NotNull
    @Schema(description = "会话id")
    private Long roomId;
}
