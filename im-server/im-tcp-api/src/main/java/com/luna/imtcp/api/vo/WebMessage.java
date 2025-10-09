package com.luna.imtcp.api.vo;

import lombok.Data;

@Data
public class WebMessage {
    private MessageHeader messageHeader;

    private String messagePack;

    @Override
    public String toString() {
        return "WebMessage {" +
                "messageHeader=" + messageHeader +
                ", messagePack=" + messagePack +
                '}';
    }

}
