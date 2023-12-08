package com.ysb.jwgio.global.config;

import com.ysb.jwgio.global.auth.jwt.filter.JwtExceptionFilter;
import com.ysb.jwgio.global.auth.jwt.refreshToken.repository.RefreshTokenRepository;
import com.ysb.jwgio.global.auth.jwt.util.JwtUtils;
import com.ysb.jwgio.global.auth.jwt.util.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final JwtUtils jwtUtils;
//    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
//    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final RefreshTokenRepository refreshTokenRepository;
//    private final JwtExceptionFilter jwtExceptionFilter;

    @Value("${aws.cloudfront.url}")
    private String cloudfront;
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        /*
        csrf().disable()
        토큰 사용시 disable 설정해야 함. 세션사용하지 않고
        stateless 서버를 구현하므로 서버에 인증정보를 저장하지 않음.
        -> 상태변환 요청 받아도 인증정보는 안전
         */
        http
                .csrf().disable()
                .exceptionHandling()
//                .authenticationEntryPoint(jwtAuthenticationEntryPoint) //자격 증명 없을 시
//                .accessDeniedHandler(jwtAccessDeniedHandler) //권한 없을 시
                //H2-console 위한 설정
                .and()
                .headers()
                .frameOptions()
                .sameOrigin()
                //세션 사용하지 않으므로 세션 설정을 stateless로 설정함.
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**/*").permitAll() //Preflight 요청 OPTIONS 메서드 허용
                .antMatchers("/api/hello", "/api/authenticate", "/api/signup", "/", "/oauth2/**",
                        "/login", "/api/kakaoLogout", "/api/deleteToken",
                        "/api/callTest", "/login/*", "/api/kakaoToken", "/Prod/*",
                        "/api/login/*", "/api/logout", "/api/ping", "/api/logoutHandler",
                        "/api/securityLogout", "/api/match/currentMatch", "/api/match/read/**", "/api/match/read/first", "/api/member/read/all", "/api/member/profile/read", "/api/member/read/top/**", "/api/auth/reissue", "/api/test/**").permitAll()
                .antMatchers("/api/current/", "/loginSuccess", "/mypage", "/api/profileTest", "/api/member/**", "/api/auth/**", "/api/match/**", "/api/push/**").hasAnyRole("ADMIN", "USER", "MANAGER")
                .antMatchers("/api/user/**").hasRole("ADMIN")
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("/favicon.ico").permitAll()
                .anyRequest().authenticated()

                // JwtFilter를 addFilterBefore로 등록한 클래스를 체인에 적용.
                .and()
                .apply(new JwtSecurityConfig(tokenProvider, jwtUtils, refreshTokenRepository));

//        http
//                .oauth2Login() //OAuth2LoginAuthenticationFilter가 등록됨
//                .userInfoEndpoint() //Oauth 2.0 로그인 성공 후 사용자 정보를 가져오는 엔드포인트를 설정
//                .userService(kakaoOauth2UserService)
//                .and()
//                .successHandler(customAuthenticationSuccessHandler)
//                .failureHandler(customSimpleUrlAuthenticationFailureHandler);

        http
                .logout() // 로그아웃 설정 시작
                .logoutUrl("/api/logout") // 로그아웃 URL 설정
                .logoutSuccessUrl(cloudfront) // 로그아웃 성공 시 리다이렉트할 URL 설정
                .invalidateHttpSession(true) // 세션 무효화 여부 설정 (기본값: true)
                .deleteCookies("JSESSIONID", "refreshToken", "accessToken") // 삭제할 쿠키 이름 설정
                .permitAll(); // 로그아웃은 모든 사용자에게 허용

        http
                .cors()
                .and()
                .csrf()
                .disable();

        return http.build();
    }
}
