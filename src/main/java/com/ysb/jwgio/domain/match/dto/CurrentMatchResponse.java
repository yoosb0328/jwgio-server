package com.ysb.jwgio.domain.match.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurrentMatchResponse {
    private Long match_id;
    private LocalDateTime date;
    private int stadiumIndex;
    private String link;
    private int num;
    private int status;
    private List<String> players = new ArrayList<>();
    private List<Long> playersId = new ArrayList<>();
    private Long createdBy;
    private boolean isEmpty;
}
