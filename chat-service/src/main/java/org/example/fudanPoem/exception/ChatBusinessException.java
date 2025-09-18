package org.example.fudanPoem.exception;

import lombok.Getter;

@Getter
public class ChatBusinessException extends RuntimeException{

    private final int code ;

    public ChatBusinessException(int code,String message) {
        super(message);
        this.code = code;
    }
}
