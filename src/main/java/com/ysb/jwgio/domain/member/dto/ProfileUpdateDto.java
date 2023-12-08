package com.ysb.jwgio.domain.member.dto;

import com.ysb.jwgio.domain.position.Positions;
import lombok.Builder;
import lombok.Data;

@Data
public class ProfileUpdateDto {
    private String imgUrl;
    private String name;
    private int number;
    private int positionIndex;

    public ProfileUpdateDto(String imgUrl, String name, int number, int positionIndex) {
        this.imgUrl = imgUrl;
        this.name = name;
        this.number = number;
        this.positionIndex = positionIndex;
    }
}
