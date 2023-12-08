package com.ysb.jwgio.global.common.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private int code;
    private String status;
    private String message;

    public CustomException(int code, String status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }
}
