package com.ysb.jwgio.global.auth.jwt.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
@Slf4j
@Component
public class JwtUtils {

    /*
    Request Header에서 토큰 정보를 꺼내오는 메서드
     */
    public String resolveToken(HttpServletRequest request, String AUTHORIZATION_HEADER) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    /*
    Cookie에서 JWT를 꺼내오는 메서드
     */
    public String resolveTokenFromHttpRequest(String tokenName, HttpServletRequest request) {
        String jwt = "";

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (tokenName.equals(cookie.getName())) {
                    jwt = cookie.getValue();
                    break;
                }
            }
        }
        return jwt;
    }
    public String resolveTokenFromCookie(String tokenName, Cookie cookie) {
        String jwt = "";

        if (tokenName.equals(cookie.getName())) {
            jwt = cookie.getValue();
        }
        return jwt;
    }
}
