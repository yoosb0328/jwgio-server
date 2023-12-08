package com.ysb.jwgio.global.auth.oauth2;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;

/**
 * OAuth2UserToken의 Principal
 * attributes :
 * userAttributes.put("id", member_id);
 *             userAttributes.put("email", email);
 *             userAttributes.put("username", username);
 *             userAttributes.put("nickname", nickname);
 *             userAttributes.put("kakao_info_id", kakaoInfoId);
 */
public class CustomOAuth2User extends DefaultOAuth2User {

    private String kakaoAccessToken;
    private Map<String, Object> attributes;
    private String nameAttributeKey;

    public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities,
                            Map<String, Object> attributes,
                            String nameAttributeKey,
                            String kakaoAccessToken) {
        super(authorities, attributes, nameAttributeKey);
        this.kakaoAccessToken = kakaoAccessToken;
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
    }

    public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities,
                            Map<String, Object> attributes, String nameAttributeKey
                            ) {
        super(authorities, attributes, nameAttributeKey);
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
    }

    public String getKakaoAccessToken() {
        return kakaoAccessToken;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public String getNameAttributeKey() {
        return nameAttributeKey;
    }

    public void changeUsernameAttribute(String username) {
        this.attributes.put("username", username);
    }

}
