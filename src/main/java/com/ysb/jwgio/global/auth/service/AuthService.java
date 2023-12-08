package com.ysb.jwgio.global.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ysb.jwgio.global.auth.jwt.refreshToken.dto.RefreshToken;
import com.ysb.jwgio.global.common.authority.Authority;
import com.ysb.jwgio.domain.member.entity.Member;
import com.ysb.jwgio.global.common.entity.UserRole;
import com.ysb.jwgio.domain.member.repository.MemberRepository;
import com.ysb.jwgio.global.auth.jwt.refreshToken.repository.RefreshTokenRepository;
import com.ysb.jwgio.global.auth.jwt.util.TokenProvider;
import com.ysb.jwgio.global.auth.oauth2.kakao.KakaoTokenDto;
import com.ysb.jwgio.global.auth.oauth2.kakao.KakaoUserInfoDto;
import com.ysb.jwgio.global.auth.oauth2.CustomOAuth2User;
import com.ysb.jwgio.global.auth.oauth2.OAuth2UserToken;
import com.ysb.jwgio.global.fcm.deviceToken.dto.DeviceToken;
import com.ysb.jwgio.global.fcm.deviceToken.repository.DeviceTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final DeviceTokenRepository deviceTokenRepository;
    private final String refreshTokenName = "refreshToken";
    private final String accessTokenName = "accessToken";

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String KAKAO_CLIENT_ID;
    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String KAKAO_REDIRECT_URI;
    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String KAKAO_CLIENT_SECRET;
    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String KAKAO_TOKEN_URI;
    @Value("https://kapi.kakao.com/v2/user/me")
    private String KAKAO_USER_INFO_URI;

    @Transactional
    public KakaoTokenDto getKakaoAccessToken(String code) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        headers.add("Access-Control-Allow-Credentials", String.valueOf(true));
        headers.add("Access-Control-Allow-Origin", "http://ysb-react-fe.s3-website.ap-northeast-2.amazonaws.com");
        // Http Response Body 객체 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code"); //카카오 공식문서 기준 authorization_code 로 고정
        params.add("client_id", KAKAO_CLIENT_ID); // 카카오 Dev 앱 REST API 키
        params.add("redirect_uri", KAKAO_REDIRECT_URI); // 카카오 Dev redirect uri
        params.add("code", code); // 프론트에서 인가 코드 요청시 받은 인가 코드값
        params.add("client_secret", KAKAO_CLIENT_SECRET); // 카카오 Dev 카카오 로그인 Client Secret

        // 헤더와 바디 합치기 위해 Http Entity 객체 생성
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        // 카카오로부터 Access token 받아오기
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> accessTokenResponse = rt.exchange(
                KAKAO_TOKEN_URI, // "https://kauth.kakao.com/oauth/token"
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // JSON Parsing (-> KakaoTokenDto)
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        KakaoTokenDto kakaoTokenDto = null;
        try {
            kakaoTokenDto = objectMapper.readValue(accessTokenResponse.getBody(), KakaoTokenDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return kakaoTokenDto;
    }

    @Transactional
    public KakaoUserInfoDto getKakaoUserInfo(KakaoTokenDto kakaoTokenDto) {
        String kakaoAccessToken = kakaoTokenDto.getAccess_token();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + kakaoAccessToken);
        headers.add("Content-type", "application/x-www.form.unlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> accountInfoRequest = new HttpEntity<>(headers);

        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> accountInfoResponse = rt.exchange(
                KAKAO_USER_INFO_URI,
                HttpMethod.POST,
                accountInfoRequest,
                String.class
        );

        // JSON Parsing (-> kakaoAccountDto)
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        KakaoUserInfoDto kakaoUserInfoDto = null;
        try {
            kakaoUserInfoDto = objectMapper.readValue(accountInfoResponse.getBody(), KakaoUserInfoDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return kakaoUserInfoDto;
    }

    /**
     * KakaoOauth2UserService의 loadUser 역할
     */
    public List<Object> loadOAuth2User(String kakaoAccessToken, KakaoUserInfoDto kakaoUserInfoDto) {
        boolean isNewMember = false;
        ArrayList<Object> result = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> kakaoUserAttributes =  objectMapper.convertValue(kakaoUserInfoDto, Map.class);
        Map<String, Object> kakao_account = kakaoUserInfoDto.getKakao_account();
        String email = (String) kakao_account.get("email");
        Map<String, Object> profile = (Map<String, Object>) kakao_account.get("profile");
        String nickname = (String) profile.get("nickname");
        Long kakaoInfoId = kakaoUserInfoDto.getId();
        Optional<Member> findMember = memberRepository.findMemberByEmail(email);

        if(findMember.isEmpty()) {
            isNewMember = true;
            Authority authority = Authority.createUserRole();
            Member member = Member.createMember(nickname, email, kakaoInfoId, authority);
            Member savedMember = memberRepository.save(member);
            Long member_id = savedMember.getId();
            String username = savedMember.getUsername();
            Map<String, Object> userAttributes = new HashMap<>();
            userAttributes.put("id", member_id);
            userAttributes.put("email", email);
            userAttributes.put("username", username);
            userAttributes.put("nickname", nickname);
            userAttributes.put("kakao_info_id", kakaoInfoId);

            CustomOAuth2User user = new CustomOAuth2User(
                    Collections.singleton(new SimpleGrantedAuthority(UserRole.ROLE_USER)),
                    userAttributes,
                    "id",
                    kakaoAccessToken
            );
            result.add(user);
            result.add(isNewMember);

            return result;
            // nameAttributeKey에 해당하는 id는 kakao userinfo 측에서 넘겨주는 kakao user info 고유 아이디.
            // nameAttributeKey가 authentication.getName()의 값이 됨. (getName()의 데이터 타입 = string)
        }
        else {
            //이미 가입된 사용자라면 해당 사용자의 권한을 가져와야 합니다.
            Member member = findMember.get();
            Long member_id = member.getId();
            String username = member.getUsername();
            Map<String, Object> userAttributes = new HashMap<>();
            userAttributes.put("id", member_id);
            userAttributes.put("email", email);
            userAttributes.put("username", username);
            userAttributes.put("nickname", nickname);
            userAttributes.put("kakao_info_id", kakaoInfoId);
            Set<Authority> authorities = member.getAuthorities();
            List<Authority> collect = authorities.stream().collect(Collectors.toList());
            Authority authority = collect.get(0);
            CustomOAuth2User user = new CustomOAuth2User(
                    Collections.singleton(new SimpleGrantedAuthority(authority.getAuthority().toString())),
                    userAttributes,
                    "id",
                    kakaoAccessToken
            );
            result.add(user);
            result.add(isNewMember);
            return result;
        }
    }
    /**
     * CustomAuthenticationSuccessHandler의 onAuthenticationSuccess 역할
     * userAttributes (kakaoInfoId) > kakao_account (email) > profile (nickname)
     */
    public Map<String, Cookie> onAuthenticationSuccess(HttpServletResponse response, CustomOAuth2User customOAuth2User) throws IOException {
        //유저 정보 추출
        Map<String, Object> userAttributes = customOAuth2User.getAttributes();
//        Map<String, Object> kakaoAccount =(Map<String, Object>) userAttributes.get("kakao_account");
//        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        //DynamoDB에 refreshToken과 함께 저장하기 위해서.
//        String nickname = (String) profile.get("nickname");
//        String email = (String) kakaoAccount.get("email");
        String username = (String) userAttributes.get("username");
        String email = (String) userAttributes.get("email");

        //authentication 설정
        SecurityContext context = SecurityContextHolder.getContext();
        OAuth2UserToken oAuth2UserToken = new OAuth2UserToken(customOAuth2User, true, username);
        context.setAuthentication(oAuth2UserToken);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomOAuth2User principal = (CustomOAuth2User) authentication.getPrincipal();

        //jwt토큰 생성
        String refreshToken = tokenProvider.createRefreshToken(authentication);
        String accessToken = tokenProvider.createAccessToken(authentication);

        //DynamoDB에 refreshToken 저장.
        RefreshToken refreshTokenObj = new RefreshToken();
        refreshTokenObj.setRefreshToken(refreshToken);
//        refreshTokenObj.setKakaoInfoId(kakaoInfoId);
//        refreshTokenObj.setAuthorities(authorities.stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.joining(",")));
//        refreshTokenObj.setKakaoNickname(nickname);
        refreshTokenObj.setEmail(email);

        refreshTokenRepository.save(refreshTokenObj);

        Map<String, Cookie> cookieMap = new HashMap<>();
        Cookie refreshTokenCookie = tokenProvider.createJwtCookie(refreshTokenName, refreshToken);
        Cookie accessTokenCookie = tokenProvider.createJwtCookie(accessTokenName, accessToken);
        cookieMap.put("refreshTokenCookie", refreshTokenCookie);
        cookieMap.put("accessTokenCookie", accessTokenCookie);
        //컨트롤러에 쿠키 반환.
        return cookieMap;
    }
    @Transactional
    public void deleteRefreshToken(String refreshToken) {
        refreshTokenRepository.deleteByPk(refreshToken);
    }

    public boolean existsRefreshToken(String refreshToken) {
        return refreshTokenRepository.existsByPk(refreshToken);
    }

    public boolean deviceTokenExistCheck(String deviceToken, Long member_id) {
        log.info("deviceTokenExistCheck  : {}", deviceToken );
        log.info("isExist?  : {}", deviceTokenRepository.existsByPk(deviceToken) );

        if(!deviceTokenRepository.existsByPk(deviceToken)) {
            DeviceToken deviceTokenObj = new DeviceToken();
            deviceTokenObj.setDeviceToken(deviceToken);
            deviceTokenObj.setMemberId(member_id);
            deviceTokenRepository.save(deviceTokenObj);
            return false;
        }
        return true;
    }
}
