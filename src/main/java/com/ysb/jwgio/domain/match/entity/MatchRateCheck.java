package com.ysb.jwgio.domain.match.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchRateCheck {

    @Id
    @GeneratedValue
    @Column(name = "MATCH_RATE_CHECK_ID")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MATCH_PLAYER_ID")
    private MatchPlayer matchPlayer;
    private Long receiver_id;

    public MatchRateCheck(Long receiver_id) {
        this.receiver_id = receiver_id;
    }

    public void setMatchPlayer(MatchPlayer matchPlayer) {
        this.matchPlayer = matchPlayer;
    }
}
