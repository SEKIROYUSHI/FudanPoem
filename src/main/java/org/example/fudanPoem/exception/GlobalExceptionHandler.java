package org.example.fudanPoem.exception;

import org.example.fudanPoem.common.Result;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return new Result<>(400,"参数校验失败",errors );
    }
}