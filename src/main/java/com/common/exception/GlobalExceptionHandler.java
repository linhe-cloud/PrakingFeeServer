package com.common.exception;

import com.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import java.util.StringJoiner;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.error("业务异常：{}", e.getMessage(), e);
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数校验异常
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public Result<Void> handleValidationException(Exception e, HttpServletRequest request) {
        log.error("参数校验异常：{}", e.getMessage(), e);

        StringJoiner joiner = new StringJoiner("; ");
        if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException ex = (MethodArgumentNotValidException) e;
            ex.getBindingResult().getFieldErrors().forEach(error ->
                joiner.add(error.getField() + ": " + error.getDefaultMessage()));
        } else if (e instanceof BindException) {
            BindException ex = (BindException) e;
            ex.getBindingResult().getFieldErrors().forEach(error ->
                joiner.add(error.getField() + ": " + error.getDefaultMessage()));
        }

        return Result.error(400, "参数校验失败: " + joiner.toString());
    }

    /**
     * 处理权限异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public Result<Void> handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        log.error("权限异常：{}", e.getMessage(), e);
        return Result.error(403, "权限不足");
    }

    /**
     * 处理其他异常
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常：{}", e.getMessage(), e);
        return Result.error(500, "系统异常，请联系管理员");
    }
}
