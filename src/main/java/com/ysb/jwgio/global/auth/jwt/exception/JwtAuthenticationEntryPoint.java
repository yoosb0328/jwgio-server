package com.ysb.jwgio.global.auth.jwt.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    /*
    유효한 자격증명을 제공하지 않고 접근하려 할 때 401 Unauthorized 에러 리턴.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.info("JwtAuthenticationEntryPoint = {}", request.getRequestURI());
        log.info("JwtAuthenticationEntryPoint = {}", request.getHeader("ERROR_MSG"));
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, request.getHeader("ERROR_MSG"));
    }
}
