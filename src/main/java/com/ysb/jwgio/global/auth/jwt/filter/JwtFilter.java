package com.ysb.jwgio.global.auth.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ysb.jwgio.global.auth.jwt.refreshToken.repository.RefreshTokenRepository;
import com.ysb.jwgio.global.auth.jwt.util.JwtUtils;
import com.ysb.jwgio.global.auth.jwt.util.TokenProvider;
import com.ysb.jwgio.global.common.errorcode.ErrorCode;
import com.ysb.jwgio.global.common.exception.CustomException;
import com.ysb.jwgio.global.common.exception.ResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Stateless 방식의 JWT 인증 로직에서는
 * SecurityFiterChain의 실행이 끝난 시점 즉,
 * 클라이언트의 요청이 Controller를 거쳐서 처리를 다 끝낸 후에
 * 메서드 실행 흐름이 SecurityContextPersistenceFilter로 넘어오게 되면
 * SecurityContext를 비우게 됩니다.
 */
@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    // extends GenericFilterBean

    public final static String AUTHORIZATION_HEADER = "Authorization";
    private final TokenProvider tokenProvider;
    private final JwtUtils jwtUtils;
    private final RefreshTokenRepository refreshTokenRepository;
    @Value("${backend.url}")
    private String BACNEND_URL;
    private final static String[] whiteList = {
            "/", "/oauth/*", "/api/logout",
            "/login/*", "/css/*", "/api/hello",
            "/api/deleteToken", "/favicon.ico",
            "/oauth2/code/kakao", "/api/kakaoToken", "/api/auth/reissue",
            "/api/match/currentMatch", "/api/match/read/**",
            "/api/member/read/all", "/api/member/read/top/**", "/api/test/**", "/api/member/profile/read"
    };


    /*
    jwt 토큰의 인증정보를 security context에 저장하는 역할
    토큰을 받아 유효성 검증을 하고 정상이면 시큐리티 컨텍스트에 저장.
        TODO : -로그인하여 토큰 발급 시 refresh 토큰 redis에 저장하는 로직 추가.
           -access token 및 refresh token 검증 로직 추가.
           쿠키로부터 access token과 refresh 토큰을 꺼내와 유효성을 검사한다.
           1. access 토큰이 유효하다 -> 통과
           2. access 토큰이 만료되었다. -> refresh 토큰이 만료되지 않았다.
              redis의 refresh 토큰과 쿠키의 refresh 토큰을 비교한다.
              둘이 같다면 access 토큰을 재발급하여 cookie에 담아 클라이언트로 보내준다.
           3. access 토큰이 만료되었다. -> refresh 토큰이 만료되었다.
              로그인 페이지로 다시 보낸다. (다시 로그인 시켜야 함)

     */
    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            String requestURI = request.getRequestURI();
            if(requestURI.equals("/api/auth/reissue")){
                //accessToken 재발급 요청의 경우 refreshToken만 검사하여 유효하면 controller로 넘어가고 유효하지 않으면 catch문으로 이동한다.
                String refreshToken = jwtUtils.resolveTokenFromHttpRequest("refreshToken", request);
                tokenProvider.validateToken(refreshToken, "refreshToken");
            }
            if(isNotWhiteList(requestURI)) {
                String accessToken = request.getHeader("Authorization");
                //access 토큰 체크
                if(tokenProvider.validateToken(accessToken, "accessToken")){
                    //유효한 경우 인증객체 생성. 유효하지 않은 경우 Exception 발생 -> catch문으로 이동.
                    Authentication authentication = tokenProvider.getAuthentication(accessToken);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
            chain.doFilter(request, response);
        } catch (CustomException e) {
            if(e.getCode() == 701) {
                //access토큰 만료
//                ResponseDto responseDto = ResponseDto.builder()
//                        .status(ErrorCode.ACCESS_TOKEN_EXPIRED.getStatus())
//                        .message(ErrorCode.ACCESS_TOKEN_EXPIRED.getMessage())
//                        .code(ErrorCode.ACCESS_TOKEN_EXPIRED.getCode())
//                        .build();
//                response.setStatus(ErrorCode.ACCESS_TOKEN_EXPIRED.getCode());
//                response.getWriter().write(new ObjectMapper().writeValueAsString(responseDto));
//                response.getWriter().flush();
                //701과 같은 커스텀 코드로 보내면 api gateway에서 인식을 못하여 internal sever error로 다시 처리해서 보내는 문제 발생
                response.setHeader("JWT-CHECK", ErrorCode.ACCESS_TOKEN_EXPIRED.getStatus());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ErrorCode.ACCESS_TOKEN_EXPIRED.getStatus());

            } else if(e.getCode() == 702 || e.getCode() == 703) {
                //refresh토큰 만료 -> responseDto를 보내고 프론트에서는 다시 로그인 페이지로 이동합니다.
//                ResponseDto responseDto = ResponseDto.builder()
//                        .status(e.getStatus())
//                        .message(e.getMessage())
//                        .code(e.getCode())
//                        .build();
//                response.setStatus(e.getCode());
//                response.getWriter().write(new ObjectMapper().writeValueAsString(responseDto));
//                response.getWriter().flush();
                response.setHeader("JWT-CHECK", ErrorCode.REFRESH_TOKEN_EXPIRED.getStatus());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ErrorCode.REFRESH_TOKEN_EXPIRED.getStatus());
            }
        }
    }

    /**
     * 화이트 리스트의 경우 인증 체크 X
     */
    private boolean isNotWhiteList(String requestURI) {
        return !PatternMatchUtils.simpleMatch(whiteList, requestURI);
    }
}
