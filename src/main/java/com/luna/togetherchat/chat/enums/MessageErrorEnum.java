package com.luna.togetherchat.chat.enums;

import com.luna.togetherchat.common.enums.ErrorEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Description: 通用异常码
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-26
 */
@AllArgsConstructor
@Getter
public enum MessageErrorEnum implements ErrorEnum {

    NO_SUCH_MESSAGE(1000, "消息不存在~"),
    UPDATE_DENY(1001, "无法修改他人的消息"),
    DELETE_DENY(1002, "你无法删除别人的信息哦~"),
    PERMISSION_DENY(1003, "状态异常")
    ;
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
