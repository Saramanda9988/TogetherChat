package com.luna.togetherchat.chat.domain.response;

import com.luna.togetherchat.chat.domain.entity.Message;
import com.luna.togetherchat.chat.domain.entity.MessageMark;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Description: 消息
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-23
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageResponse {

    @Schema(description = "消息详情")
    @NotNull
    private Message message;

    @Schema(description = "消息标记")
    private List<MessageMark> messageMark;

}
