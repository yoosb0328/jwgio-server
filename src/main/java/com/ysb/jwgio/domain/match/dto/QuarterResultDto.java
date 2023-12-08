package com.ysb.jwgio.domain.match.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuarterResultDto {
    private int gf; //득점
    private int ga; //실점
    private int num; //쿼터 번호
    private int result; // 1 : 승  0 : 무 -1 : 패 -2: 업데이트X
    private boolean isUpdated;
}
