package org.example.fudanPoem.exception;

import org.example.fudanPoem.common.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        return Result.error("服务器内部错误: " + e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public  Result<?> handleRuntimeException(RuntimeException e) {
        return Result.error("运行时错误: " + e.getMessage());
    }

    @ExceptionHandler(UserBusinessException.class)
    public Result<?> handleUserBusinessException(UserBusinessException e) {
        return new Result<>(e.getCode(), e.getMessage(), null);
    }
}