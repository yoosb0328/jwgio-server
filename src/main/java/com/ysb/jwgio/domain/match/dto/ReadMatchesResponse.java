package com.ysb.jwgio.domain.match.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadMatchesResponse {
    private List<MatchPagingData> matchPagingData = new ArrayList<>();
    private Long lastIndex;
    private boolean isEmpty;
}
