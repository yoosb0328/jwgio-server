package com.ysb.jwgio.global.auth.jwt.util;

import com.ysb.jwgio.global.auth.oauth2.CustomOAuth2User;
import com.ysb.jwgio.global.auth.oauth2.OAuth2UserToken;
import com.ysb.jwgio.global.auth.oauth2.UserAttributes;
import com.ysb.jwgio.global.common.errorcode.ErrorCode;
import com.ysb.jwgio.global.common.exception.CustomException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import javax.servlet.http.Cookie;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TokenProvider implements InitializingBean {

    private static final String AUTHORITIES_KEY = "auth";
    private static final String USER_ATTRIBUTES_KEY = "userAttributes";
    private final String secret;
    private final long tokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;
    private final long accessTokenValidityInMilliseconds;

    private Key key;

    /*
    @Component로 등록된 TokenProvider Bean이 생성되며 yml 파일의 설정 값이 주입됨.
     */
    public TokenProvider(@Value("${jwt.secret}") String secret,
                         @Value("${jwt.token-validity-in-seconds}") long tokenValidityInMilliseconds,
                         @Value("${jwt.refresh-validity-in-seconds}") long refreshTokenValidityInMilliseconds,
                         @Value("${jwt.access-validity-in-seconds}") long accessTokenValidityInMilliseconds) {
        this.secret = secret;
        this.tokenValidityInMilliseconds = tokenValidityInMilliseconds;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInMilliseconds;
        this.accessTokenValidityInMilliseconds = accessTokenValidityInMilliseconds;
    }
    /*
    주입받은 secret 값을 base64 디코딩 한 다음 key 변수에 할당
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /*
    Authentication 객체의 권한 정보를 활용하여 jwt 토큰을 생성하는 메서드
    TODO : Access Token, Refresh Token  만드는 메서드 구현해야 함.
     */
    public String createToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        long now = (new Date()).getTime();
        Date validity = new Date(now + this.tokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject((String) authentication.getDetails())
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    /*
    Refresh 토큰 생성
    authentication.getName() = kakaoInfoId
     */
    public String createRefreshToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        Map<String, Object> userAttributes = customOAuth2User.getAttributes();
        Long member_id = (Long) userAttributes.get("id");

//        long refreshPeriod = 1000L * 60L * 60L * 24L *14; // 2주
        long now = (new Date()).getTime();
        Date validity = new Date(now + refreshTokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(member_id.toString())
                .claim(AUTHORITIES_KEY, authorities)
                .claim(USER_ATTRIBUTES_KEY, userAttributes)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    /*
    Access 토큰 생성
     */
    public String createAccessToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        Map<String, Object> userAttributes = customOAuth2User.getAttributes();
        Long member_id = (Long) userAttributes.get("id");

        long now = (new Date()).getTime();
        Date validity = new Date(now + accessTokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(member_id.toString())
                .claim(AUTHORITIES_KEY, authorities)
                .claim(USER_ATTRIBUTES_KEY, userAttributes)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }
    public String reissueAccessToken(Collection<? extends GrantedAuthority> collection,
                                     Long member_id,
                                     Map<String, Object> userAttributes) {
        String authorities = collection.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        long now = (new Date()).getTime();
        Date validity = new Date(now + accessTokenValidityInMilliseconds);
        return Jwts.builder()
                .setSubject(member_id.toString())
                .claim(AUTHORITIES_KEY, authorities)
                .claim(USER_ATTRIBUTES_KEY, userAttributes)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    /*
    jwt 토큰의 정보로 authentication 객체를 리턴하는 메서드
    OAuth2UserToken는 authentication을 implements한 인증 객체임.
     */
    public Authentication getAuthentication(String token) {
        //토큰에서 클레임을 꺼냄
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
        Map<String, Object> userAttributes = (Map<String, Object>) claims.get(USER_ATTRIBUTES_KEY);
        String username = (String) userAttributes.get("username");

        CustomOAuth2User principal = new CustomOAuth2User(authorities, userAttributes, "id");
        OAuth2UserToken oAuth2UserToken = new OAuth2UserToken(principal, true, username);


        return oAuth2UserToken;
    }

    /*
    jwt 토큰을 받아 유효성 검사.
    TODO : 토큰의 만료기한 체크하여 만료되면 false를 리턴해야함.
     */
    public boolean validateToken(String token, String tokenName) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            throw new CustomException(ErrorCode.JWT_TOKEN_MALFORMED.getCode(), ErrorCode.JWT_TOKEN_MALFORMED.getStatus(), ErrorCode.JWT_TOKEN_MALFORMED.getMessage());

        } catch (ExpiredJwtException e) {
            if(tokenName.equals("refreshToken")){
                throw new CustomException(ErrorCode.REFRESH_TOKEN_EXPIRED.getCode(), ErrorCode.REFRESH_TOKEN_EXPIRED.getStatus(), ErrorCode.REFRESH_TOKEN_EXPIRED.getMessage());
            } else if(tokenName.equals("accessToken")) {
                throw new CustomException(ErrorCode.ACCESS_TOKEN_EXPIRED.getCode(), ErrorCode.ACCESS_TOKEN_EXPIRED.getStatus(), ErrorCode.ACCESS_TOKEN_EXPIRED.getMessage());
            }
        } catch (UnsupportedJwtException e) {
            throw new CustomException(ErrorCode.JJWTTOKEN_UNSUPPORTED.getCode(), ErrorCode.JJWTTOKEN_UNSUPPORTED.getStatus(), ErrorCode.JJWTTOKEN_UNSUPPORTED.getMessage());

        } catch (IllegalArgumentException e) {
            if(tokenName.equals("refreshToken")){
                throw new CustomException(ErrorCode.REFRESH_TOKEN_EXPIRED.getCode(), ErrorCode.REFRESH_TOKEN_EXPIRED.getStatus(), ErrorCode.REFRESH_TOKEN_EXPIRED.getMessage());
            } else if(tokenName.equals("accessToken")) {
                throw new CustomException(ErrorCode.ACCESS_TOKEN_EXPIRED.getCode(), ErrorCode.ACCESS_TOKEN_EXPIRED.getStatus(), ErrorCode.ACCESS_TOKEN_EXPIRED.getMessage());
            }
        }
        return false;
    }
    /*
    토큰의 유효성 검사 및 만료 시간 체크
     */
    public boolean verifyToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(key) // 비밀키를 설정하여 파싱한다.
                    .parseClaimsJws(token);  // 주어진 토큰을 파싱하여 Claims 객체를 얻는다.
            // 비밀키로 파싱하는 과정에서 유효하지 않은 토큰이면 exception 발생.
            // 토큰의 만료 시간과 현재 시간비교
            return claims.getBody()
                    .getExpiration()
                    .after(new Date());  // 만료 시간이 현재 시간 이후인지 확인하여 유효성 검사 결과를 반환
        } catch (Exception e) {
            return false;
        }
    }

    /*
    쿠키에 담아 jwt 전송 시 쿠키 생성 메서드
    HTTP-Only 속성을 추가하여 JavaScript로 접근할 수 없도록 설정하여 XSS 공격 방지
     */
    public Cookie createJwtCookie(String tokenName, String token) {
        Cookie cookie = new Cookie(tokenName, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
//        if(tokenName.equals("accessToken")) {
//            cookie.setMaxAge((int) (accessTokenValidityInMilliseconds / 1000)); // in seconds
//            log.info("accessToken setMaxAge = {}", (int) (accessTokenValidityInMilliseconds / 1000));
//        } else if(tokenName.equals("refreshToken")) {
//            cookie.setMaxAge((int) (refreshTokenValidityInMilliseconds / 1000)); // in seconds
//            log.info("refreshToken setMaxAge = {}", (int) (refreshTokenValidityInMilliseconds / 1000));
//        }
        cookie.setPath("/");
//        cookie.setDomain(".https://d3fdct0ii12edy.cloudfront.net");
        return cookie;
    }
}
