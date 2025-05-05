package com.luna.togetherchat.chat.domain.entity.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Description: 消息扩展属性
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageExtra implements Serializable {
    private static final long serialVersionUID = 1L;

    // 文件消息
    private FileMessage fileMessage;
    // 图片消息
    private ImageMessage imageMessage;
    // 转发消息
    private ForwardMessage forwardMessage;

    /**
     * @param body 和 handler 绑定的 消息类型实例
     * @param <T> 和 handler 绑定的 消息类型
     */
    public <T> MessageExtra(T body) {
        switch (body) {
            case ImageMessage i -> this.imageMessage = i;
            case FileMessage f -> this.fileMessage = f;
            case ForwardMessage fm -> this.forwardMessage = fm;
            default -> {}
        }
    }
}
