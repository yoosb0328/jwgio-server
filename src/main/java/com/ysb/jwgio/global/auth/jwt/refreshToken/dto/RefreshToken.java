package com.ysb.jwgio.global.auth.jwt.refreshToken.dto;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

/**
 * current ttl : 1 hours
 * NoArgs 생성자 필수
 * getter, setter 방식 사용할 것
 */
@DynamoDbBean
public class RefreshToken {
    private String refreshToken;
//    private Long kakaoInfoId;
    private Long ttl = System.currentTimeMillis() + 1000 * 60 * 60 * 24; // ttl 24시간
//    private String authorities;
//    private String kakaoNickname;
    private String email;
//    private Map<String, AttributeValue> userAttributes;

    public RefreshToken() {
    }

//    public RefreshToken(String refreshToken, Long kakaoInfoId, Collection<? extends GrantedAuthority> authorities, String kakaoNickname, String email) {
//        this.refreshToken = refreshToken;
//        this.kakaoInfoId = kakaoInfoId;
//        this.authorities = authorities;
//        this.kakaoNickname = kakaoNickname;
//        this.email = email;
//        this.ttl = System.currentTimeMillis() + 1000 * 60 * 60;
//    }

    @DynamoDbPartitionKey
    @DynamoDbAttribute(value = "refreshToken")
    public String getRefreshToken() {
        return this.refreshToken;
    }
//    @DynamoDbAttribute(value = "kakaoInfoId")
//    public Long getKakaoInfoId() {
//        return this.kakaoInfoId;
//    }
//    @DynamoDbAttribute(value = "authorities")
//    public String getAuthorities() {
//        return this.authorities;
//    }
//    @DynamoDbAttribute(value = "kakaoNickname")
//    public String getKakaoNickname() {
//        return this.kakaoNickname;
//    }
    @DynamoDbAttribute(value = "email")
    public String getEmail() {
        return this.email;
    }
    @DynamoDbAttribute(value = "ttl")
    public Long getTtl() {
        return this.ttl;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

//    public void setKakaoInfoId(Long kakaoInfoId) {
//        this.kakaoInfoId = kakaoInfoId;
//    }

    public void setTtl(Long ttl) {
        this.ttl = ttl;
    }

//    public void setAuthorities(String authorities) {
//        this.authorities = authorities;
//    }
//
//    public void setKakaoNickname(String kakaoNickname) {
//        this.kakaoNickname = kakaoNickname;
//    }

    public void setEmail(String email) {
        this.email = email;
    }

}
