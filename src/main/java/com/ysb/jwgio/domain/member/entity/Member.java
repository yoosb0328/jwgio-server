package com.ysb.jwgio.domain.member.entity;

import com.ysb.jwgio.domain.match.entity.MatchPlayer;
import com.ysb.jwgio.domain.position.Positions;
import com.ysb.jwgio.global.common.authority.Authority;
import com.ysb.jwgio.global.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Member extends BaseEntity {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String username;

    private String nickname;

    private String email;

    private int jerseyNumber;

    @Column(name = "kakao_info_id")
    private Long kakaoInfoId;

    private String profileImg;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private Set<Authority> authorities = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private Positions position;

    @OneToMany(mappedBy = "member")
    private List<MatchPlayer> matches = new ArrayList<>();
    private int totalWin; //승리한 쿼터 수
    private int totalLose; //패배한 쿼터 수
    private int totalDraw; //비긴 쿼터 수
    private int totalGoal; //통산 득점
    private int totalAssist; //통산 어시
    //포지션 별 득점/어시
    private int left_ala_goal;
    private int left_ala_assist;
    private int right_ala_goal;
    private int right_ala_assist;
    private int pivot_goal;
    private int pivot_assist;
    private int fixo_goal;
    private int fixo_assist;
    private int goleiro_goal;
    private int goleiro_assist;
    private int goleiro_cs;
    //포지션 별 승/무/패
    private int left_ala_win;
    private int left_ala_draw;
    private int left_ala_lose;
    private int right_ala_win;
    private int right_ala_draw;
    private int right_ala_lose;
    private int pivot_win;
    private int pivot_draw;
    private int pivot_lose;
    private int fixo_win;
    private int fixo_draw;
    private int fixo_lose;
    private int goleiro_win;
    private int goleiro_draw;
    private int goleiro_lose;

    private float rate;
    private float managerRate;

    public void addAuthority(Authority authority) {
        this.authorities.add(authority);
        authority.setMember(this);
    }

    /*
     */
    public static Member createMember(String nickname, String email, Long kakaoInfoId, Authority... authorities) {
        Member member = new Member();
        member.nickname = nickname;
        member.username = nickname; //처음 가입할 때에는 nickname (카카오 닉네임)을 username으로 설정한다.
        member.email = email;
        for (Authority authority: authorities) {
            member.addAuthority(authority);
        }
        member.kakaoInfoId = kakaoInfoId;
        return member;
    }

    public void updateProfile(String imgUrl, String username, int jerseyNumber, Positions position) {
        this.profileImg = imgUrl;
        this.username = username;
        this.jerseyNumber = jerseyNumber;
        this.position = position;
    }

    public void addStats(int goal, int assist) {
        this.totalGoal += goal;
        this.totalAssist += assist;
    }
    public long getMatchCount() {
        return this.matches.stream().filter(m -> m.getMatch().getStatus() == 4).count();
    }
    public void calcRate() {
        float matchCount = (float) this.getMatchCount();
        float rateSum = 0f; //각 매치의 평점을 가져옵니다.
        float managerRateSum = 0f; // 각 매치의 감독 평점을 가져옵니다.
        float managerRateCount = 0f; //감독 평점이 존재하는 경기의 숫자.
        for(MatchPlayer match : this.matches) {
            rateSum += match.getRate();
            if(match.getManagerRate() != -1.0f) {
                managerRateSum += match.getManagerRate();
                managerRateCount++;
            }
        }
        this.rate = rateSum / matchCount;
        this.managerRate = managerRateSum / managerRateCount;
    }

    public void addTotalQuarterResult(int result) {
        if(result == 1) this.totalWin++;
        if(result == 0) this.totalDraw++;
        if(result == -1) this.totalLose++;
    }

    public void addPositionQuarterResult(Positions position, int result, int ga) {
        switch (position) {
            case RIGHT_ALA: {
                if(result == 1) this.right_ala_win++;
                else if(result == 0) this.right_ala_draw++;
                else if(result == -1) this.right_ala_lose++;
                break;
            }
            case LEFT_ALA: {
                if(result == 1) this.left_ala_win++;
                else if(result == 0) this.left_ala_draw++;
                else if(result == -1) this.left_ala_lose++;
                break;
            }
            case FIXO: {
                if(result == 1) this.fixo_win++;
                else if(result == 0) this.fixo_draw++;
                else if(result == -1) this.fixo_lose++;
                break;
            }
            case PIVOT: {
                if(result == 1) this.pivot_win++;
                else if(result == 0) this.pivot_draw++;
                else if(result == -1) this.pivot_lose++;
                break;
            }
            case GOLEIRO: {
                if(result == 1) this.goleiro_win++;
                else if(result == 0) this.goleiro_draw++;
                else if(result == -1) this.goleiro_lose++;
                if(ga == 0) this.goleiro_cs++;
                break;
            }
            default:
                break;
        }

    }

    public void addLeftAlaStats(int goal, int assist) {
        this.left_ala_goal += goal;
        this.left_ala_assist += assist;
    }
    public void addRightAlaStats(int goal, int assist) {
        this.right_ala_goal += goal;
        this.right_ala_assist += assist;
    }
    public void addPivotStats(int goal, int assist) {
        this.pivot_goal += goal;
        this.pivot_assist += assist;
    }
    public void addFixoStats(int goal, int assist) {
        this.fixo_goal += goal;
        this.fixo_assist += assist;
    }
    public void addGoleiroStats(int goal, int assist) {
        this.goleiro_goal += goal;
        this.goleiro_assist += assist;
    }
}
