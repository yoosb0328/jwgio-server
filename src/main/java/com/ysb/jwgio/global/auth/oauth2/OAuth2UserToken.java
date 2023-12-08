package com.ysb.jwgio.global.auth.oauth2;

import com.ysb.jwgio.global.auth.oauth2.CustomOAuth2User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.HashSet;

/**
 * 커스텀 Authentication Object
 */
public class OAuth2UserToken implements Authentication {
    private CustomOAuth2User principal;
    private boolean authenticated;
    private String details;
    public OAuth2UserToken(CustomOAuth2User principal, boolean authenticated, String details) {
        this.principal = principal;
        this.authenticated = authenticated;
        this.details = details;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return principal == null ? new HashSet<>() : principal.getAuthorities();
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public String getDetails() {
        return this.details;
    }

    @Override
    public CustomOAuth2User getPrincipal() {
        return this.principal;
    }

    @Override
    public boolean isAuthenticated() {
        return this.authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return principal.getName();
    }
}
