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
public enum SessionErrorEnum implements ErrorEnum {

    NO_SUCH_SESSION(6000, "通话不存在~"),
    SESSION_ALREADY_ENDED(6001, "通话已结束"),
    REPEAT_JOIN(6002, "你已经加入通话~"),
    USER_ALREADY_LEAVE(6003, "你已经退出通话~"),
    OPERATION_TOO_FREQUENT(6004, "操作太频繁了，请稍后再试哦~~"),
    USER_ALREADY_IN_SESSION(6005, "你已经在其他通话中了~"),
    NO_VALID_USER(6006, "没有有效的用户可以加入通话~");
    private final Integer code;
    private final String msg;

    @Override
    public Integer getErrorCode() {
        return this.code;
    }

    @Override
    public String getErrorMsg() {
        return this.msg;
    }
}
