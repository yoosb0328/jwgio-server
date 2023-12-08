package com.ysb.jwgio.domain.stadium;

import com.ysb.jwgio.domain.position.Positions;

import java.util.Arrays;

public enum Stadiums {

    YATAP_NC(0, "yt-nc", "야탑 NC백화점 스카이필드", 5),
    MORAN(1, "mr", "모란 풋살장", 5),
    SUWON_PLAB_INDOOR(2, "sw-plab-in", "플랩 스타디움 수원 실내", 5),
    BUNDANG_KJ1(3, "bd-kj1", "성남 분당 킹주니어 스포츠 클럽 1호점", 6),
    BUNDANG_KJ2(4, "bd-kj2", "성남 분당 킹주니어 스포츠 클럽 2호점", 6),
    EMPTY(5, "없음","없음", 0);

    private int index;
    private String code;
    private String name;
    private int number;
    Stadiums(int index, String code, String name, int number) {
        this.index = index;
        this.code = code;
        this.name = name;
        this.number = number;
    }

    public int getIndex() { return index; }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getNumber() {
        return number;
    }

    public static Stadiums findStadium(String code) {
        return Arrays.stream(Stadiums.values())
                .filter(stadium -> hasStadium(stadium, code))
                .findAny()
                .orElse(Stadiums.EMPTY);

    }

    public static Stadiums findStadium(int stadiumIndex) {
        return Arrays.stream(Stadiums.values())
                .filter(stadium -> hasStadium(stadium, stadiumIndex))
                .findAny()
                .orElse(Stadiums.EMPTY);
    }
    private static boolean hasStadium(Stadiums from, String code) {
        return from.code.equals(code);
    }
    private static boolean hasStadium(Stadiums from, int stadiumIndex) {
        return from.index == stadiumIndex;
    }

}