package com.luna.togetherchat.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Description: 通用异常码
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-26
 */
@AllArgsConstructor
@Getter
public enum CommonErrorEnum implements ErrorEnum {

    SYSTEM_ERROR(-1, "系统出小差了，请稍后再试哦~~"),
    PARAM_VALID(-2, "参数校验失败{0}"),
    FREQUENCY_LIMIT(-3, "请求太频繁了，请稍后再试哦~~"),
    LOCK_LIMIT(-4, "请求太频繁了，请稍后再试哦~~"),
    DATABASE_ERROR(-5, "数据库问题"),
    PARAMS_ERROR(102, "参数有误"),
    ACCESS_TOKEN_INVALID(401, "access token无效"),
    REFRESH_TOKEN_INVALID(400, "refresh token无效"),;
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
