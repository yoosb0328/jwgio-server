package com.ysb.jwgio.global.auth.oauth2.kakao;

import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
public class KakaoUserInfoDto {
    private Date connected_at;
    private Long id;
    private Map<String, Object> kakao_account;
    private Map<String, Object> properties;
    private String accessToken;
    private boolean isNewMember;
}
