package com.ysb.jwgio.domain.member.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProfileUpdateRequest {

    private String name;
    private int number;
    private int position; //positionIndex

}
