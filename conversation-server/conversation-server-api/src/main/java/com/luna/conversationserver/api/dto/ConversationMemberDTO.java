package com.luna.conversationserver.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 会话成员DTO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConversationMemberDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long memberId;
    private Long conversationId;
    private Long userId;
    private String username;
    private Integer role;
    private LocalDateTime joinTime;
    private String nickname;
    private Byte state;
}