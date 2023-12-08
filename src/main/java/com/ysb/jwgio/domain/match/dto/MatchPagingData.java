package com.ysb.jwgio.domain.match.dto;

import com.ysb.jwgio.domain.match.entity.Match;
import com.ysb.jwgio.domain.match.entity.MatchPlayer;
import com.ysb.jwgio.domain.match.entity.Quarter;
import com.ysb.jwgio.domain.match.entity.QuarterStats;
import com.ysb.jwgio.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchPagingData {
    private Long matchId;
    private LocalDateTime date;
    private int stadiumIndex;
    private String mvp;
    private int[] results = new int[6];
    private Long[] playerIds;
    private String[] playerNames;
    private String[] profileImgs;
    private float[] ratings;
    private float[] managerRatings;
    private List<QuarterPagingStats>[] pagingStats; //1번 인덱스에는 1번 플레이어의 쿼터별 기록 리스트가 들어있습니다. 리스트에는 1쿼터~6쿼터까지의 기록이 들어있습니다.
    public MatchPagingData(Match match, String mvpName) {
        this.matchId = match.getId();
        this.date = match.getDate();
        this.stadiumIndex = match.getStadium().getIndex();
        this.mvp = mvpName;

        List<MatchPlayer> players = match.getPlayers();
        playerIds = new Long[players.size()];
        playerNames = new String[players.size()];
        profileImgs = new String[players.size()];
        ratings = new float[players.size()];
        managerRatings = new float[players.size()];
        pagingStats = (List<QuarterPagingStats>[]) new List<?>[players.size()];

        int idx = 0;
        for(MatchPlayer player : players) {
            Member member = player.getMember();
            playerIds[idx] = member.getId();
            playerNames[idx] = member.getUsername();
            profileImgs[idx] = member.getProfileImg();
            ratings[idx] = player.getRate();
            managerRatings[idx] = player.getManagerRate();

            List<QuarterStats> quarterStats = player.getQuarterStats();
            List<QuarterPagingStats> quarterPagingStatsList = new ArrayList<>();
            for(QuarterStats stats : quarterStats) {
                QuarterPagingStats quarterPagingStats = new QuarterPagingStats();
                quarterPagingStats.setQuarterNum(stats.getQuarter_num());
                quarterPagingStats.setGoals(stats.getGoal());
                quarterPagingStats.setAssist(stats.getAssist());
                quarterPagingStats.setPositionIndex(stats.getPositions().getIndex());
                quarterPagingStatsList.add(quarterPagingStats);
            }
            pagingStats[idx] = quarterPagingStatsList;
            idx++;
        }
        List<Quarter> quarters = match.getQuarters();
        int idx2 = 0;
        for(Quarter quarter : quarters) {
            results[idx2] = quarter.getResult();
            idx2++;
        }
    }

}
