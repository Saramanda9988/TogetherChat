package com.luna.conversationserver.conversation.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 会话信息响应
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "会话信息响应")
public class ConversationResponse {

    @Schema(description = "会话ID")
    private Long conversationId;

    @Schema(description = "会话名称")
    private String name;

    @Schema(description = "创建者用户ID")
    private Long creatorId;

    @Schema(description = "会话简介")
    private String description;

    @Schema(description = "会话头像URL")
    private String avatar;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "最后更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "会话类型：1=单聊，2=群聊")
    private Integer type;

    @Schema(description = "会话状态：0=正常，1=删除")
    private Integer status;

    @Schema(description = "成员数量")
    private Integer memberCount;
}