package com.luna.messageserver.message.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 消息响应
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "消息响应")
public class MessageResponse {

    @Schema(description = "消息ID，全局唯一")
    private Long messageId;

    @Schema(description = "服务器消息顺序")
    private Integer syncId;

    @Schema(description = "消息属于的群组/会话ID")
    private Long groupId;

    @Schema(description = "发送者用户ID")
    private Long userId;

    @Schema(description = "发送者用户名")
    private String username;

    @Schema(description = "消息状态：0=正常，1=撤回，2=编辑")
    private Integer status;

    @Schema(description = "消息类型：1=文本，2=图片，3=文件")
    private Integer messageType;

    @Schema(description = "回复的消息内容")
    private String replyMessage;

    @Schema(description = "消息内容")
    private String content;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}