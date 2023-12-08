package com.ysb.jwgio.global.auth.jwt.accessToken;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccessToken {
    private String accessToken;

    public AccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
