package com.ysb.jwgio.global.auth.controller;

import com.ysb.jwgio.global.auth.dto.LoginResponseDto;
import com.ysb.jwgio.global.auth.jwt.accessToken.AccessToken;
import com.ysb.jwgio.global.auth.jwt.util.JwtUtils;
import com.ysb.jwgio.global.auth.jwt.util.TokenProvider;
import com.ysb.jwgio.global.auth.oauth2.kakao.KakaoTokenDto;
import com.ysb.jwgio.global.auth.oauth2.kakao.KakaoUserInfoDto;
import com.ysb.jwgio.global.auth.oauth2.CustomOAuth2User;
import com.ysb.jwgio.global.auth.service.AuthService;
import com.ysb.jwgio.global.common.errorcode.ErrorCode;
import com.ysb.jwgio.global.common.exception.ResponseDto;
import com.ysb.jwgio.global.common.sns.SnsService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

    private final TokenProvider tokenProvider;
    private final JwtUtils jwtUtils;
    private final AuthService authService;
    private final SnsService snsService;
    @Value("${jwt.refresh-validity-in-seconds}")
    private long refreshTokenValidityInMilliseconds;
    @Value("${jwt.access-validity-in-seconds}")
    private long accessTokenValidityInMilliseconds;

    @Value("${backend.url}")
    private String BACNEND_URL;

    /**
     * 클라이언트에서 직접 카카오 로그아웃 요청 후 서버/api/deleteToken으로 리다이렉트하므로 해당 매서드는 사용하지 않음.
     */
//    @GetMapping("/kakaoLogout")
//    public void kakaoLogout(HttpServletResponse response, @Value("${spring.security.oauth2.client.registration.kakao.client-id}") String client_id) throws IOException {
////        String refreshToken = jwtUtils.resolveTokenFromCookie("refreshToken", request);
////
////        if(tokenProvider.validateToken(refreshToken) && refreshTokenRepository.existsById(refreshToken)){
////            refreshTokenRepository.deleteById(refreshToken);
////        }
//        log.info("AuthController - kakaoLogout!");
//        String kakaoLogoutUrl = "https://kauth.kakao.com/oauth/logout";
////        String logoutRedirectUrl = CLOUDFRONT_URL+"/api/deleteToken";
//        String logoutRedirectUrl = "http://localhost:8080/api/deleteToken";
//
//        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(kakaoLogoutUrl)
//                .queryParam("client_id", client_id)
//                .queryParam("logout_redirect_uri", logoutRedirectUrl);
//        String logoutUrl = uriBuilder.toUriString();
//        response.setHeader("Access-Control-Allow-Origin", "*");
//        response.sendRedirect(logoutUrl);
//    }

    @GetMapping("/deleteToken")
    public void deleteRedisToken(HttpServletRequest request, HttpServletResponse response) throws IOException, JwtException {
        String refreshToken = jwtUtils.resolveTokenFromHttpRequest("refreshToken", request);
        if(refreshToken.isEmpty()) {
            response.sendRedirect(BACNEND_URL+"/api/logout"); //스프링 시큐리티 logout url
        } else {
            if(authService.existsRefreshToken(refreshToken)) authService.deleteRefreshToken(refreshToken);
            response.sendRedirect(BACNEND_URL+"/api/logout");
        }
    }

    @GetMapping("/kakaoToken")
    public ResponseEntity<LoginResponseDto> getKakaoToken(HttpServletResponse response, @RequestParam String code) throws IOException {
        boolean isNewMember = false;

        KakaoTokenDto kakaoTokenDto = authService.getKakaoAccessToken(code);
        KakaoUserInfoDto kakaoUserInfoDto = authService.getKakaoUserInfo(kakaoTokenDto);
        List<Object> result = authService.loadOAuth2User(kakaoTokenDto.getAccess_token(), kakaoUserInfoDto);
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) result.get(0);
        Map<String, Object> userAttributes = customOAuth2User.getAttributes();
        Long member_id = (Long) userAttributes.get("id");
        String username = (String) userAttributes.get("username");
        isNewMember = (boolean) result.get(1);
        kakaoUserInfoDto.setNewMember(isNewMember);

        Map<String, Cookie> cookieMap = authService.onAuthenticationSuccess(response, customOAuth2User);
        Cookie refreshTokenCookie = cookieMap.get("refreshTokenCookie");
        Cookie accessTokenCookie = cookieMap.get("accessTokenCookie");
        String accessToken = jwtUtils.resolveTokenFromCookie("accessToken", accessTokenCookie);
        kakaoUserInfoDto.setAccessToken(accessToken);
        refreshTokenCookie.setMaxAge(86400); // 단위 : 초 //86400초 = 24시간.
        response.addCookie(refreshTokenCookie);
        GrantedAuthority grantedAuthority = customOAuth2User.getAuthorities().stream().findFirst().get();
        String authorityString = grantedAuthority.toString();

        return ResponseEntity.ok().body(
                LoginResponseDto.builder()
                        .username(username)
                        .member_id(member_id)
                        .accessToken(accessToken)
                        .isNewMember(isNewMember)
                        .auth(authorityString)
                        .build()
        );
    }

    @PostMapping("/auth/reissue")
    public ResponseEntity<AccessToken> reissueAccessToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtUtils.resolveTokenFromHttpRequest("refreshToken", request);
        Authentication oldAuthentication = tokenProvider.getAuthentication(refreshToken);
        CustomOAuth2User principal = (CustomOAuth2User) oldAuthentication.getPrincipal();
        int member_id_int = (int) principal.getAttributes().get("id");
        long member_id = Long.valueOf(member_id_int);
        String accessToken = tokenProvider.reissueAccessToken(
                principal.getAuthorities(),
                member_id,
                principal.getAttributes()
        );

        Authentication authentication = tokenProvider.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        AccessToken accessTokenDto = new AccessToken(accessToken);
        return ResponseEntity.ok()
                .body(accessTokenDto);
    }

    @GetMapping("/auth/accessExpired")
    public ResponseEntity<?> accessTokenExpired() {
        ResponseDto responseDto = ResponseDto.builder()
                .status(ErrorCode.ACCESS_TOKEN_EXPIRED.getStatus())
                .message(ErrorCode.ACCESS_TOKEN_EXPIRED.getMessage())
                .code(ErrorCode.ACCESS_TOKEN_EXPIRED.getCode())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
    }

    @GetMapping("/auth/deviceToken")
    public ResponseEntity<?> setDeviceToken(@RequestParam String deviceToken, @RequestParam Long member_id) {
        //로그인 시 deviceToken 처리
        boolean isExist = authService.deviceTokenExistCheck(deviceToken, member_id);
        if(!isExist) {
            String endpoint = snsService.createEndpoint(deviceToken);
            log.info("AuthController - setDeviceToken - createEndpoint : {}", endpoint);
            snsService.subCreateMatchTopic(endpoint);
            snsService.subCompleteMatchTopic(endpoint);
        }
        return ResponseEntity.ok(200);
    }
}
