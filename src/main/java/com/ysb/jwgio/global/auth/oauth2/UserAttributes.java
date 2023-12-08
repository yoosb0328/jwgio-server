package com.ysb.jwgio.global.auth.oauth2;

import lombok.Data;

@Data
public class UserAttributes {

    private Long id;
    private String email;
    private String username;
    private String nickname;
    private Long kakao_info_id;
}
