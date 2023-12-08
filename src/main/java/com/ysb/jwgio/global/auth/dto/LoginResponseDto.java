package com.ysb.jwgio.global.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseDto {

    private Long member_id;
    private String username;
    private String accessToken;
    private String auth;
    private boolean isNewMember;
}
