package org.example.fudanPoem.exception;

import org.example.fudanPoem.common.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ChatExceptionHandler {

    @ExceptionHandler(ChatBusinessException.class)
    public Result<?> handleChatBusinessException(ChatBusinessException e) {
        return new Result<>(e.getCode(), e.getMessage(), null);
    }

}
