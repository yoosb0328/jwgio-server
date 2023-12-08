package com.ysb.jwgio.global.config;

import com.ysb.jwgio.global.auth.jwt.filter.JwtExceptionFilter;
import com.ysb.jwgio.global.auth.jwt.filter.JwtFilter;
import com.ysb.jwgio.global.auth.jwt.refreshToken.repository.RefreshTokenRepository;
import com.ysb.jwgio.global.auth.jwt.util.JwtUtils;
import com.ysb.jwgio.global.auth.jwt.util.TokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private TokenProvider tokenProvider;
    private JwtUtils jwtUtils;
    private RefreshTokenRepository refreshTokenRepository;
//    private JwtExceptionFilter jwtExceptionFilter;
    public JwtSecurityConfig(TokenProvider tokenProvider, JwtUtils jwtUtils, RefreshTokenRepository refreshTokenRepository) {
        this.tokenProvider = tokenProvider;
        this.jwtUtils = jwtUtils;
        this.refreshTokenRepository = refreshTokenRepository;
//        this.jwtExceptionFilter = jwtExceptionFilter;
    }

    /*
    JwtFilter를 시큐리티 로직에 등록.
    UsernamePasswordAuthenticationFilter 앞에 추가함.
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        JwtFilter customFilter = new JwtFilter(tokenProvider, jwtUtils, refreshTokenRepository);
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
//        http.addFilterBefore(jwtExceptionFilter, customFilter.getClass());
    }
}
