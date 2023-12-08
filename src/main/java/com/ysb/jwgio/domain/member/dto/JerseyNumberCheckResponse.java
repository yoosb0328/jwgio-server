package com.ysb.jwgio.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JerseyNumberCheckResponse {
    private boolean isUsed;
    private String username;
}
