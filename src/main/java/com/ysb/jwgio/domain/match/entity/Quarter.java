package com.ysb.jwgio.domain.match.entity;

import com.ysb.jwgio.domain.member.entity.Member;
import com.ysb.jwgio.global.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Quarter extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "quarter_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id")
    private Match match;

    private int num; // 쿼터 번호

    private int gf; //팀 득점

    private int ga; //팀 실점

    private int result; // 승 : 1 / 무 : 0 / 패: -1   //입력안함 : -2

    public void setMatch(Match match) {
        this.match = match;
    }

    public void setQuarterResult(int gf, int ga) {
        this.gf = gf;
        this.ga = ga;
        this.result = (gf - ga) > 0 ? 1 : (gf-ga) < 0 ? -1 : 0;
        List<MatchPlayer> players = this.match.getPlayers();
        for(MatchPlayer player : players) {
            Member member = player.getMember();

        }
    }

    public static Quarter createQuarter(int num) {
        Quarter quarter = new Quarter();
        quarter.num = num;
        quarter.result = -2;
        return quarter;
    }


}
