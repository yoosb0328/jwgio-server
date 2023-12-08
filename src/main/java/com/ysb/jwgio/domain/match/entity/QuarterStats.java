package com.ysb.jwgio.domain.match.entity;

import com.ysb.jwgio.domain.position.Positions;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class QuarterStats {
    /**
     * 각 쿼터마다 선수별 스탯을 기록하는 Entitiy
     */

    @Id
    @GeneratedValue
    @Column(name = "quarter_player_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_player_id")
    private MatchPlayer matchPlayer; // 매치에 참여한 선수 정보

    private int quarter_num; // 쿼터 번호.

    @Enumerated(EnumType.STRING)
    private Positions positions; //해당 쿼터에 선수가 플레이한 포지션

    private int goal; //해당 쿼터에 선수가 기록한 득점
    private int assist; //해당 쿼터에 선수가 기록한 어시스트

    public void setMatchPlayer(MatchPlayer matchPlayer) {
        this.matchPlayer = matchPlayer;
    }


    public static QuarterStats createQuarterStats(int quarter_num) {
        QuarterStats quarterStats = new QuarterStats();
        quarterStats.quarter_num = quarter_num;
        return quarterStats;
    }

    public void setStats(int goal, int assist, int positionIndex) {
        this.goal = goal;
        this.assist = assist;
        this.positions = Positions.findPosition(positionIndex);
        this.matchPlayer.getMember().addStats(goal, assist);
        //통산 스탯 업데이트
        switch (this.positions) {
            case FIXO:
                this.matchPlayer.getMember().addFixoStats(goal, assist);
                break;
            case PIVOT:
                this.matchPlayer.getMember().addPivotStats(goal, assist);
                break;
            case GOLEIRO:
                this.matchPlayer.getMember().addGoleiroStats(goal, assist);
                break;
            case LEFT_ALA:
                this.matchPlayer.getMember().addLeftAlaStats(goal, assist);
                break;
            case RIGHT_ALA:
                this.matchPlayer.getMember().addRightAlaStats(goal, assist);
                break;
            default: break;
        }
    }
}
