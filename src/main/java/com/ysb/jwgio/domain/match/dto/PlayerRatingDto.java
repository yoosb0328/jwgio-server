package com.ysb.jwgio.domain.match.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerRatingDto {
    private Long member_id;
    private String username;
    private float rate;
    private boolean isUpdated;
}
