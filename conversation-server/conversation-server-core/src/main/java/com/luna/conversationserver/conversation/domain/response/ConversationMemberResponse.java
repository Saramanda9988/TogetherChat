package com.luna.conversationserver.conversation.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 会话成员信息响应
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "会话成员信息响应")
public class ConversationMemberResponse {

    @Schema(description = "成员关系ID")
    private Long memberId;

    @Schema(description = "会话ID")
    private Long conversationId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "成员角色：0=单聊，1=成员，2=管理员，3=群主")
    private Integer role;

    @Schema(description = "加入时间")
    private LocalDateTime joinTime;

    @Schema(description = "成员昵称")
    private String nickname;

    @Schema(description = "状态：1=正常，2=禁言，3=退出")
    private Byte state;
}