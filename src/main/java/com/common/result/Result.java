package com.common.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import com.charge.entity.ChargeOrder;

/**
 * 统一响应结果类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 状态码 */
    private Integer code;

    /** 消息 */
    private String message;

    /** 数据 */
    private T data;

    /** 时间戳 */
    private Long timestamp;

    /**
     * 成功响应
     */
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null, System.currentTimeMillis());
    }

    /**
     * 成功响应带数据
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data, System.currentTimeMillis());
    }

    /**
     * 成功响应带消息
     */
    public static Result<Void> success(String message) {
        return new Result<>(200, message, null, System.currentTimeMillis());
    }

    /**
     * 成功响应带消息和数据
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data, System.currentTimeMillis());
    }

    /**
     * 失败响应
     */
    public static <T> Result<T> error() {
        return new Result<>(500, "操作失败", null, System.currentTimeMillis());
    }

    /**
     * 失败响应带消息
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null, System.currentTimeMillis());
    }

    /**
     * 失败响应带状态码和消息
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null, System.currentTimeMillis());
    }

    /**
     * 自定义响应
     */
    public static <T> Result<T> build(Integer code, String message, T data) {
        return new Result<>(code, message, data, System.currentTimeMillis());
    }

    public static Result<PageResult<ChargeOrder>> fail(String string) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'fail'");
    }
}
