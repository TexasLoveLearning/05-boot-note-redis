package com.fyc._4bootnote.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /** 1. 参数校验失败（@Valid） */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValid(MethodArgumentNotValidException e) {
        String msg = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
        log.warn("参数校验失败: {}", msg);
        return new Result<>(400, msg, null);
    }

    /** 2. 业务异常（自定义） */
    @ExceptionHandler(ServiceException.class)
    public Result<Void> handleService(ServiceException e) {
        log.warn("业务异常: {}", e.getMessage());
        return new Result<>(e.getCode(), e.getMessage(), null);
    }

    /** 3. 数据库唯一键冲突 */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result<Void> handleSQL(SQLIntegrityConstraintViolationException e) {
        log.warn("数据库冲突: {}", e.getMessage());
        return new Result<>(400, "数据已存在，请检查重复字段", null);
    }

    /** 4. 兜底——所有没被逮到的 */
    @ExceptionHandler(Throwable.class)
    public Result<Void> handleAll(Throwable e) {
        log.error("系统异常", e);
        return new Result<>(500, "服务器繁忙，请稍后重试", null);
    }
}