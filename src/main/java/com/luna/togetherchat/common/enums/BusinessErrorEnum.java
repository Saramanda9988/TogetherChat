package com.luna.togetherchat.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Description: 业务校验异常码
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-26
 */
@AllArgsConstructor
@Getter
public enum BusinessErrorEnum implements ErrorEnum {
    //==================================common==================================
    BUSINESS_ERROR(101, "{0}"),
    //==================================user==================================
    LOGIN_USER_ERROR(10001, "用户不存在"),
    LOGIN_PASSWORD_ERROR(10002, "用户密码错误"),
    // 在枚举中添加以下错误码
    USERNAME_ALREADY_EXISTS(10001, "用户名已存在"),
    EMAIL_ALREADY_EXISTS(10002, "邮箱已被注册"),
    PHONE_ALREADY_EXISTS(10003, "手机号已被注册"),
    //==================================chat==================================
    SYSTEM_ERROR(1001, "系统出小差了，请稍后再试哦~~"),
    MESSAGE_UPDATE_ERROR(1002, "无法修改消息，可能是权限不足，也可能是不存在");
    private final Integer code;
    private final String msg;

    @Override
    public Integer getErrorCode() {
        return code;
    }

    @Override
    public String getErrorMsg() {
        return msg;
    }
}
