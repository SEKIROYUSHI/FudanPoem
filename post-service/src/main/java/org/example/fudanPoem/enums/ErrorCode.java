package org.example.fudanPoem.enums;


import lombok.Getter;

@Getter
public enum ErrorCode {
    EMAIL_ALREADY_REGISTERED(1001, "邮箱已被注册"),
    USERNAME_ALREADY_REGISTERED(1002, "用户名已被注册"),
    PASSWORD_FORMAT_ERROR(1003, "密码必须至少8位，包含大写字母、小写字母和数字"),
    USER_NOT_FOUND(1004, "用户不存在"),
    PASSWORD_ERROR(1005, "密码错误"),
    UNKNOWN_ERROR(1006, "未知错误");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
