package com.luna.conversationserver.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 会话DTO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConversationDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long conversationId;
    private String name;
    private Long creatorId;
    private String description;
    private String avatar;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer type;
    private Integer status;
    private Integer memberCount;
}