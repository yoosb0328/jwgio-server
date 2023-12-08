package com.ysb.jwgio.domain.member.dto;

import com.ysb.jwgio.domain.match.entity.MatchPlayer;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class ProfileReadRequest {

    private String imgUrl;
    private String name;
    private int number;
    private int positionIndex;
    private List<MatchPlayer> matches;
}
