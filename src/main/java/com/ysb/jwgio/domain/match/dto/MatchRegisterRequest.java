package com.ysb.jwgio.domain.match.dto;

import lombok.*;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchRegisterRequest {
    private String date;
    private String stadiumCode;
    private String link;
    private int num;
}

