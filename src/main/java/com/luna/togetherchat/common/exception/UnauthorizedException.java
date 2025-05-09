package com.luna.togetherchat.common.exception;

import com.luna.togetherchat.common.enums.ErrorEnum;
import lombok.Data;

/**
 * @author 苍镜月
 * @version 1.0
 * @implNote
 */

@Data
public class UnauthorizedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    protected Integer errorCode;

    /**
     * 错误信息
     */
    protected String errorMsg;

    public UnauthorizedException() {
        super();
    }

    public UnauthorizedException(String errorMsg) {
        super(errorMsg);
        this.errorMsg = errorMsg;
    }

    public UnauthorizedException(Integer errorCode, String errorMsg) {
        super(errorMsg);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public UnauthorizedException(Integer errorCode, String errorMsg, Throwable cause) {
        super(errorMsg, cause);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public UnauthorizedException(ErrorEnum error) {
        super(error.getErrorMsg());
        this.errorCode = error.getErrorCode();
        this.errorMsg = error.getErrorMsg();
    }

    @Override
    public String getMessage() {
        return errorMsg;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
