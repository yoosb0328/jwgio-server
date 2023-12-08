package com.ysb.jwgio.domain.member.dto;

import com.ysb.jwgio.domain.match.dto.TotalMatchCountResponse;
import com.ysb.jwgio.domain.member.entity.Member;
import com.ysb.jwgio.domain.position.Positions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TopPlayerRecordData {

    private Long member_id;
    private String username;
    private String img;
    private int jerseyNumber;
    private int positionIndex;
    private int totalGoal;
    private int totalAssist;
    private long totalMatch;
    private float rate;
    private float managerRate;
    private boolean isEmpty;

    public TopPlayerRecordData(Member member) {
        this.member_id = member.getId();
        this.username = member.getUsername();
        this.img = member.getProfileImg();
        this.jerseyNumber = member.getJerseyNumber();
        this.positionIndex = member.getPosition().getIndex();
        this.totalGoal = member.getTotalGoal();
        this.totalAssist = member.getTotalAssist();
        this.totalMatch = member.getMatchCount();
        this.rate = member.getRate();
        this.managerRate = member.getManagerRate();
    }
    public TopPlayerRecordData(TotalMatchCountResponse count) {
        this.member_id = count.getMemberId();
        this.username = count.getUsername();
        this.img = count.getImg();
        this.jerseyNumber = count.getJerseyNumber();
        this.totalMatch = count.getTotalMatch();
        this.positionIndex = Positions.findPositionIndex(count.getPosition());
    }
}
