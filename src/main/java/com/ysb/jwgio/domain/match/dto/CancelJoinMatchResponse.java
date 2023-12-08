package com.ysb.jwgio.domain.match.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CancelJoinMatchResponse {
    private List<String> players = new ArrayList<>();
    private List<Long> playersId = new ArrayList<>();
    private boolean isExist;
    private boolean isPlaying;
}
