package com.common.exception;

/**
 * 业务异常类
 * 用于业务逻辑校验失败时抛出，由全局异常处理器统一捕获并返回友好提示
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = 400;  // 默认业务异常状态码
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
