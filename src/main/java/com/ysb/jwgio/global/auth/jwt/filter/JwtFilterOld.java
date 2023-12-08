//package com.ysb.jwgio.global.auth.jwt.filter;
//
//import com.ysb.jwgio.global.auth.jwt.accessToken.AccessToken;
//import com.ysb.jwgio.global.auth.jwt.refreshToken.repository.RefreshTokenRepository;
//import com.ysb.jwgio.global.auth.jwt.util.JwtUtils;
//import com.ysb.jwgio.global.auth.jwt.util.TokenProvider;
//import com.ysb.jwgio.global.auth.oauth2.CustomOAuth2User;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.util.PatternMatchUtils;
//import org.springframework.util.StringUtils;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
///**
// * Stateless 방식의 JWT 인증 로직에서는
// * SecurityFiterChain의 실행이 끝난 시점 즉,
// * 클라이언트의 요청이 Controller를 거쳐서 처리를 다 끝낸 후에
// * 메서드 실행 흐름이 SecurityContextPersistenceFilter로 넘어오게 되면
// * SecurityContext를 비우게 됩니다.
// */
//@Slf4j
//@RequiredArgsConstructor
//public class JwtFilterOld extends OncePerRequestFilter {
//    // extends GenericFilterBean
//
//    public final static String AUTHORIZATION_HEADER = "Authorization";
//    private final TokenProvider tokenProvider;
//    private final JwtUtils jwtUtils;
//    private final RefreshTokenRepository refreshTokenRepository;
//    private final static String[] whiteList = {"/", "/oauth/*", "/api/logout", "/login/*", "/css/*", "/api/hello", "/api/deleteToken", "/favicon.ico", "/oauth2/code/kakao", "/api/kakaoToken" };
//
//
//    /*
//    jwt 토큰의 인증정보를 security context에 저장하는 역할
//    토큰을 받아 유효성 검증을 하고 정상이면 시큐리티 컨텍스트에 저장.
//        TODO : -로그인하여 토큰 발급 시 refresh 토큰 redis에 저장하는 로직 추가.
//           -access token 및 refresh token 검증 로직 추가.
//           쿠키로부터 access token과 refresh 토큰을 꺼내와 유효성을 검사한다.
//           1. access 토큰이 유효하다 -> 통과
//           2. access 토큰이 만료되었다. -> refresh 토큰이 만료되지 않았다.
//              redis의 refresh 토큰과 쿠키의 refresh 토큰을 비교한다.
//              둘이 같다면 access 토큰을 재발급하여 cookie에 담아 클라이언트로 보내준다.
//           3. access 토큰이 만료되었다. -> refresh 토큰이 만료되었다.
//              로그인 페이지로 다시 보낸다. (다시 로그인 시켜야 함)
//
//     */
//    @Override
//    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
//        HttpServletRequest httpServletRequest = request;
//
////        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
////        String jwt = resolveTokenFromCookie(httpServletRequest);
////        String jwt = resolveToken(httpServletRequest);
//        String requestURI = httpServletRequest.getRequestURI();
//
//        log.info("JwtFilter.doFilter() - requestURI : {}", requestURI);
//
//        if(isLoginCheckPath(requestURI)) {
//            String refreshToken = jwtUtils.resolveTokenFromHttpRequest("refreshToken", httpServletRequest);
//            String accessToken = request.getHeader("Authorization");
////            String accessToken = jwtUtils.resolveTokenFromHttpRequest("accessToken", httpServletRequest);
//            log.info("JwtFilter.doFilter() - refreshToken : {}", refreshToken);
//            log.info("JwtFilter.doFilter() - accessToken : {}", accessToken);
//            if(StringUtils.hasText(accessToken) && tokenProvider.validateToken(accessToken, "accessToken")) {
//                //accessToken이 만료되지 않음 -> 인증 객체 생성.
//                Authentication authentication = tokenProvider.getAuthentication(accessToken);
//                log.info("@@@@@@@@@@ doFilter - authentication.getName : {}", authentication.getName());
//                log.info("@@@@@@@@@@ doFilter - authentication.getAuthorities : {}", authentication.getAuthorities());
//                log.info("@@@@@@@@@@ doFilter - authentication.getPrincipal : {}", authentication.getPrincipal());
//
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//                log.info("Security Context에 '{}' 인증 정보를 저장했습니다, uri : {}", authentication.getDetails(), requestURI);
//            } else {
//                log.info("acceessToken이 만료되었습니다.");
//                if(!tokenProvider.validateToken(refreshToken, "refreshToken")) {
//                    //refreshToken이 유효하지 않음. -> JwtException -> JwtExceptionFilter
//                    log.info("refreshToken도 만료되었습니다. 로그인 페이지로 이동합니다.");
//                    return;
//                } else if(StringUtils.hasText(refreshToken) && tokenProvider.validateToken(refreshToken, "refreshToken")) {
//                    log.info("refreshToken이 유효하여 accessToken을 재발급합니다.");
//                    //유효한 refreshToken으로 authentication 객체를 생성하여 이를 바탕으로 accessToken 재발급.
//                    Authentication oldAuthentication = tokenProvider.getAuthentication(refreshToken);
//                    CustomOAuth2User principal = (CustomOAuth2User) oldAuthentication.getPrincipal();
//
//                    accessToken = tokenProvider.reissueAccessToken(
//                            principal.getAuthorities(),
//                            (Long) principal.getAttributes().get("id"),
//                            principal.getAttributes()
//                    );
//                    //재발급한 accessToken을 응답에 저장 및 인증 객체 생성
////                    response.addCookie(tokenProvider.createJwtCookie("accessToken", accessToken));
////                    AccessToken accessTokenDto = new AccessToken();
////                    accessTokenDto.setAccessToken(accessToken);
////                    ResponseEntity.ok().body(accessTokenDto);
//                    response.getWriter().write(accessToken);
//                    response.getWriter().flush();
//                    Authentication authentication = tokenProvider.getAuthentication(accessToken);
//
//                    log.info("@@@@@@@@@@ doFilter - authentication.getName : {}", authentication.getDetails());
//                    log.info("@@@@@@@@@@ doFilter - authentication.getAuthorities : {}", authentication.getAuthorities());
//                    log.info("@@@@@@@@@@ doFilter - authentication.getPrincipal : {}", authentication.getPrincipal());
//
//                    SecurityContextHolder.getContext().setAuthentication(authentication);
//                    log.info("Security Context에 '{}' 인증 정보를 저장했습니다, uri : {}", authentication.getDetails(), requestURI);
//                }
//            }
//        }
//        chain.doFilter(request, response);
//    }
//
//    /**
//     * 화이트 리스트의 경우 인증 체크 X
//     */
//    private boolean isLoginCheckPath(String requestURI) {
//        return !PatternMatchUtils.simpleMatch(whiteList, requestURI);
//    }
//}
