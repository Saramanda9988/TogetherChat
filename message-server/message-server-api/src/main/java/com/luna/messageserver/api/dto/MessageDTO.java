package com.luna.messageserver.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消息DTO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long messageId;
    private Integer syncId;
    private Long conversationId;
    private Long userId;
    private Integer status;
    private Integer messageType;
    private String replyMessage;
    private String content;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}