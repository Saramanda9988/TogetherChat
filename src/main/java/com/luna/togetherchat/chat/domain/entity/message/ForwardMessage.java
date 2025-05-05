package com.luna.togetherchat.chat.domain.entity.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.luna.togetherchat.chat.domain.response.ChatMessageResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 苍镜月
 * @version 1.0
 * @implNote 转发消息
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)  // 添加此注解
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ForwardMessage {

    @Schema(description = "转发的消息列表，json写死的，不会随着原来的消息而改变")
    @Size(max = 500, message = "转发上限最多是500条")
    private List<ChatMessageResponse> messageList;
}