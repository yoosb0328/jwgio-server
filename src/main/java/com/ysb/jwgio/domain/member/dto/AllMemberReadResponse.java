package com.ysb.jwgio.domain.member.dto;

import com.ysb.jwgio.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AllMemberReadResponse {
    private Long member_id;
    private String username;
    private String img;
    private String authority;
    private int positionIndex;
    private int jerseyNumber;
    private boolean isEmpty;

    public AllMemberReadResponse(Member member) {
        this.member_id = member.getId();
        this.username = member.getUsername();
        this.img = member.getProfileImg();
        this.authority = member.getAuthorities().stream().findFirst().get().getAuthority().toString();
        this.positionIndex = member.getPosition().getIndex();
        this.jerseyNumber = member.getJerseyNumber();
    }
}
