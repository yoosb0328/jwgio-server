package com.ysb.jwgio.domain.match.dto;

import lombok.Data;

@Data
public class QuarterPagingStats {
    private int quarterNum;
    private int goals;
    private int assist;
    private int positionIndex;
}
