package com.luna.webrtcserver.call.enums;

import com.luna.webrtcserver.common.enums.ErrorEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Description: 通用异常码
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-26
 */
@AllArgsConstructor
@Getter
public enum SessionTypeEnum {

    ONE_TO_ONE(1, "一对一通话"),
    GROUP(2, "群聊");
    private final Integer type;
    private final String msg;
}
