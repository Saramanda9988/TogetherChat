package com.luna.imtcp.api.vo.msgBody;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupMessageBody implements Serializable {
    private String tempMessageId;

    private Long fromId;

    private Long conversationId;

    private Integer appId;

    private String content;

    private String replyMessage;

    private Integer messageType;
}