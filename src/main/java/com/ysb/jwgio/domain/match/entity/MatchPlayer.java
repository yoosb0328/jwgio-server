package com.ysb.jwgio.domain.match.entity;

import com.ysb.jwgio.domain.position.Positions;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import com.ysb.jwgio.domain.member.entity.Member;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class MatchPlayer {
    /**
     * Match와 Member의 중간 테이블
     * 해당 Match에 참여한 선수
     */
    @Id
    @GeneratedValue
    @Column(name = "MATCH_PLAYER_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id")
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "matchPlayer", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @Column(name = "quarter_stats")
    private List<QuarterStats> quarterStats = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Positions main_position; //해당 매치에 주로 플레이한 포지션

    private float rate;
    private float rateSum;
    private float managerRate;

    @OneToMany(mappedBy = "matchPlayer", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<MatchRateCheck> receivers = new ArrayList<>();

    public static MatchPlayer createMatchPlayer(Member member, Match match) {
        MatchPlayer matchPlayer =  new MatchPlayer();
        matchPlayer.setMember(member);
        matchPlayer.setManagerRate(-1.0f);//초기값은 -1.0f;
        member.getMatches().add(matchPlayer);
        match.getPlayers().add(matchPlayer);

        matchPlayer.setMatch(match);
        for(int i=1; i<=6; i++) {
            QuarterStats qs = QuarterStats.createQuarterStats(i);
            qs.setMatchPlayer(matchPlayer);
            matchPlayer.quarterStats.add(qs);
        }
        matchPlayer.rateSum = -1.0f;
        return matchPlayer;
    }
    public void setRate(float rate) {
        this.rate = rate;
    }

    public void setManagerRate(float managerRate) { this.managerRate = managerRate; }

    public void setMember(Member member) {
        this.member = member;
    }

    public void setMatch(Match match) {
        this.match = match;
    }
    public void addRateSum(float rate) {
        if(this.rateSum == -1.0f) this.rateSum = 0.0f;
        this.rateSum += rate;
    }
    public float calcAvgRate() {
        float num = (float) this.match.getPlayers().size() - 1.0f;
        this.rate = this.rateSum / num;
        return rate;
    }
    public void addQuarterStats(QuarterStats quarterStats) {
        this.quarterStats.add(quarterStats);
    }

    public void setMainPosition(int mainPositionIndex) {
        this.main_position = Positions.findPosition(mainPositionIndex);
    }
}
