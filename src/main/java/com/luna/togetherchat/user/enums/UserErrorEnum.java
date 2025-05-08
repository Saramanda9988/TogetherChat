package com.luna.togetherchat.user.enums;

import com.luna.togetherchat.common.enums.ErrorEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author Kkuil
 * @Date 2023/10/24 15:50
 * @Description 群异常码
 */
@AllArgsConstructor
@Getter
public enum UserErrorEnum implements ErrorEnum {
    /**
     *
     */
    USER_NOT_EXIST(10001, "用户不存在"),
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
