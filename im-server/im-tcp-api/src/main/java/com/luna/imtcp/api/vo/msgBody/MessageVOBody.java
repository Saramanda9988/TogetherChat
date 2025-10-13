package com.luna.imtcp.api.vo.msgBody;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageVOBody {
    private Long messageId;

    private Integer syncId;

    private Long groupId;

    private Long userId;

    private Integer status;

    private Integer messageType;

    private MessageVOBody replyMessage;

    private String content;

    private LocalDateTime createTime;
}
