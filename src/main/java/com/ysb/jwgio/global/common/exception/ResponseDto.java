package com.ysb.jwgio.global.common.exception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseDto {
    private int code;
    private String status;
    private String message;

    public ResponseDto setCode(int code) {
        this.code = code;
        return this;
    }

    public ResponseDto setStatus(String status) {
        this.status = status;
        return this;
    }

    public ResponseDto setMessage(String message) {
        this.message = message;
        return this;
    }
}
