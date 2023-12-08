package com.ysb.jwgio.global.common.authority;

import com.ysb.jwgio.domain.member.entity.Member;
import com.ysb.jwgio.global.common.entity.UserRole;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Authority implements GrantedAuthority {
    @Id @GeneratedValue
    @Column(name = "authority_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    private String authority;
    public Authority setAuthority(String role) {
        this.authority = role;
        return this;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public static Authority createUserRole() {
        Authority authority = new Authority();
        authority.authority = UserRole.ROLE_USER;

        return authority;
    }

    public static Authority createManagerRole() {
        Authority authority = new Authority();
        authority.authority = UserRole.ROLE_MANAGER;

        return authority;
    }

    public static Authority createAdminRole() {
        Authority authority = new Authority();
        authority.authority = UserRole.ROLE_ADMIN;

        return authority;
    }
}
