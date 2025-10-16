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
public class P2PMessageBody implements Serializable {
    private String tempMessageId;

    private Long fromId;

    private Long conversationId;

    private Long toId;

    private Integer appId;

    private String content;

    private String replyMessage;

    private Integer messageType;
}
