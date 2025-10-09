package com.luna.imtcp.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MessageType {
    // 0x0. json、 0x1. protobuf、 0x2. xml
    DATA_TYPE_JSON(0x0),
    DATA_TYPE_PROTOBUF(0x1),
    DATA_TYPE_XML(0x2);

    private final Integer msgType;
    public Integer getCode() {
        return msgType;
    }

}
