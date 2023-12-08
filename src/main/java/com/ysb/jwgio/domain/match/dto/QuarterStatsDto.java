package com.ysb.jwgio.domain.match.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuarterStatsDto {
    private int quarterNum;
    private int goal;
    private int assist;
    private int positionIndex;
    private boolean isUpdated;
}
