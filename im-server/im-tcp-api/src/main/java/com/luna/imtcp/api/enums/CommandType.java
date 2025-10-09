package com.luna.imtcp.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CommandType {
    GROUP_MESSAGE(1001),
    SINGLE_MESSAGE(1002),
    MESSAGE_ACK(1003),
    READ_MESSAGE(1004);

    private final Integer type;
}
