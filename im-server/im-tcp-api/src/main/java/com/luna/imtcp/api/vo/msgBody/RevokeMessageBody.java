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
public class RevokeMessageBody implements Serializable {
    private Long messageId;

    private Long operatingUserId;

    private Long conversationId;

    private Integer appId;
}
