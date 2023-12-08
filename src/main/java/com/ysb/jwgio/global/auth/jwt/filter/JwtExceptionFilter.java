package com.ysb.jwgio.global.auth.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
//@ControllerAdvice
//@Component
public class JwtExceptionFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        log.info("JwtExceptionFilter = {}", request);
        try {
            chain.doFilter(request, response);
        } catch (JwtException e) {
            //JwtException 발생 시 필터 체인을 타지 않고 catch에서 멈춘다.
            log.info("JwtExceptionFilter e.getMessage = {}", e.getMessage());
            setErrorResponse(request, response, e);
        }
    }

    public void setErrorResponse(HttpServletRequest req, HttpServletResponse res, Throwable ex) throws IOException {
        log.info("JwtExceptionFilter - setErrorResponse");

        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        final Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Unauthorized");
        body.put("message", ex.getMessage());
        body.put("path", req.getServletPath());
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(res.getOutputStream(), body);
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage());

//        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage());
//        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED, "@@@@@@");
    }
}
