package com.example.intelligentbibackend.exception;

import com.example.intelligentbibackend.common.ErrorCode;

/**
 * 业务异常类(接口错误返回时抛一个异常)
 */

public class BesinessException extends RuntimeException{
    private final int code;
    private final String description;

//    构造函数
    public BesinessException(String message, int code, String description) {
        super(message);
        this.code = code;
        this.description = description;
    }

    public BesinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
    }
    public BesinessException(ErrorCode errorCode, String description) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
