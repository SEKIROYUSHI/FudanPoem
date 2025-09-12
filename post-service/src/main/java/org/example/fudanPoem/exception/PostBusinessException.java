package org.example.fudanPoem.exception;


import lombok.Getter;

@Getter
public class PostBusinessException extends RuntimeException {
    private final int code ;

    public  PostBusinessException(int code,String message) {
        super(message);
        this.code = code;
    }
}
