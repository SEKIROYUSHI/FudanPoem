package org.example.fudanPoem.exception;


import lombok.Getter;

@Getter
public class UserBusinessException extends RuntimeException {
    private final int code ;

    public UserBusinessException(int code,String message) {
        super(message);
        this.code = code;
    }
}
