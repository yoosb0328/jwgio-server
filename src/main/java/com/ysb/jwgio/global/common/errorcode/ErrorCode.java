package com.ysb.jwgio.global.common.errorcode;


public enum ErrorCode implements EnumModel {

    ACCESS_TOKEN_EXPIRED(701, "ACCESS_TOKEN_EXPIRED", "access token이 만료되었습니다."),
    REFRESH_TOKEN_EXPIRED(702, "REFRESH_TOKEN_EXPIRED", "refresh token이 만료되었습니다."),
    JWT_TOKEN_MALFORMED(703, "JWT_TOKEN_MALFORMED", "잘못된 JWT 서명입니다."),
    JJWTTOKEN_UNSUPPORTED(703, "JWT_TOKEN_MALFORMED", "지원하지 않는 JWT 서명입니다."),
    JWT_TOKEN_ILLEGAL(701, "JWT_TOKEN_ILLEGAL", "잘못된 JWT 토큰입니다.");
    private int code;
    private String status;
    private String message;

    ErrorCode(int code, String status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getStatus() {
        return status;
    }

    public int getCode() {
        return code;
    }
}
