package com.ysb.jwgio.domain.match.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultWriteResponse {
    private List<QuarterResultDto> quarterResults =  new ArrayList<>();;
    private List<QuarterStatsDto> quarterStats =  new ArrayList<>();;
    private List<PlayerRatingDto> matchRatings =  new ArrayList<>();;
}
