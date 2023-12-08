package com.ysb.jwgio.domain.member.dto;

import com.ysb.jwgio.domain.match.entity.MatchPlayer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileReadResponse {

    private String imgUrl;
    private String name;
    private int number;
    private int positionIndex;
    private List<MatchPlayer> matches;

    private int totalGoal;
    private int totalAssist;
    private int totalWin;
    private int totalDraw;
    private int totalLose;

    private int left_ala_Goal;
    private int left_ala_Assist;
    private int left_ala_Win;
    private int left_ala_Draw;
    private int left_ala_Lose;

    private int right_ala_Goal;
    private int right_ala_Assist;
    private int right_ala_Win;
    private int right_ala_Draw;
    private int right_ala_Lose;

    private int pivot_Goal;
    private int pivot_Assist;
    private int pivot_Win;
    private int pivot_Draw;
    private int pivot_Lose;

    private int fixo_Goal;
    private int fixo_Assist;
    private int fixo_Win;
    private int fixo_Draw;
    private int fixo_Lose;

    private int goleiro_Goal;
    private int goleiro_Assist;
    private int goleiro_cs;
    private int goleiro_Win;
    private int goleiro_Draw;
    private int goleiro_Lose;

    private int mvpCount;
    private long matchCount;
    private float rate;
    private float managerRate;
}
