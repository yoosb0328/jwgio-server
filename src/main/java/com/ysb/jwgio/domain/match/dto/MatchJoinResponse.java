package com.ysb.jwgio.domain.match.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MatchJoinResponse {

    private Long member_id;
    private String username;
    private boolean isFull;
    private boolean isCanceled;
}
