package com.ysb.jwgio.domain.match.entity;

import com.ysb.jwgio.domain.match.dto.MatchRegisterRequest;
import com.ysb.jwgio.domain.match.dto.MatchRegisterResponse;
import com.ysb.jwgio.domain.member.entity.Member;
import com.ysb.jwgio.domain.position.Positions;
import com.ysb.jwgio.domain.stadium.Stadiums;
import com.ysb.jwgio.global.common.entity.BaseEntity;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Slf4j
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Match extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "match_id")
    private Long id;

    private int num; //매치 인원수 ex: 5 or 6

    private LocalDateTime date; //매치 일자

    private int status; //매치 상태 - 0 : 대기중 / 1 : 확정 / 2 : 진행중 / 3: 종료(기록 입력 대기) / 4: 종료(기록 입력 완료)

    @Enumerated(EnumType.STRING)
    private Stadiums stadium;//경기장

    @OneToMany(mappedBy = "match", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<MatchPlayer> players = new ArrayList<>();

    @OneToMany(mappedBy = "match",  cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Quarter> quarters = new ArrayList<>();

    private String link;

    private Long mvp; //mvp userid

    public static Match registerNewMatch(LocalDateTime date, String stadiumCode, String link, int num) {
        Match match = new Match();
        match.date = date;
        match.stadium = Stadiums.findStadium(stadiumCode);
        match.link = link;
        match.num = num;
        match.status = 0;
        for(int i=1; i<=6; i++) {
            Quarter quarter = Quarter.createQuarter(i);
            quarter.setMatch(match);
            match.quarters.add(quarter);
        }
        return match;
    }

    public void addMatchPlayer(MatchPlayer matchPlayer) {
        this.players.add(matchPlayer);
    }
    public int checkStatus() {
        log.info("checkStatus - LocalDateTime.now() = {} ", LocalDateTime.now());
        log.info("checkStatus - LocalDateTime.now().isBefore(this.date.plusHours(2)) = {} ", LocalDateTime.now().isBefore(this.date.plusHours(2)));
        log.info("checkStatus - LocalDateTime.now().isAfter(this.date.plusHours(2))  = {}", LocalDateTime.now().isAfter(this.date.plusHours(2)));

        //경기 시간 전 -> 인원 수 꽉참 -> 상태 1, 인원수 꽉 안참 -> 상태 0
        if(LocalDateTime.now().isBefore(this.date)) {
            if(this.players.size() == this.num) {
                this.status = 1;
            } else if(this.players.size() < this.num) {
                this.status = 0;
            }
        } else if(this.date.isEqual(LocalDateTime.now()) || LocalDateTime.now().isBefore(this.date.plusHours(2))) {
            //경기 시간 중 -> 무조건 상태 = 2
            this.status = 2;
        } else if(LocalDateTime.now().isAfter(this.date.plusHours(2))) {
            //경기 시간 후 -> 무조건 상태 = 3
            this.status = 3;
        }
        log.info("checkStatus  : {}", this.status);
        return this.status;
    }

    public void setStatus(int status) { this.status = status; }
    public void setMvp(Long mvp) { this.mvp = mvp; }


}
