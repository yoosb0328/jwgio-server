package com.ysb.jwgio.domain.position;

import java.util.Arrays;

public enum Positions {
    LEFT_ALA(0,"LA", "LEFT ALA (LM)", "LEFT ALA"),
    RIGHT_ALA(1,"RA", "RIGHT ALA (RM)", "RIGHT ALA"),
    PIVOT(2,"PV", "PIVOT (ST)", "PIVOT"),
    FIXO(3,"FX", "FIXO (CB)", "FIXO"),
    GOLEIRO(4,"GO", "GOLEIRO (GK)", "GOLEIRO"),
    EMPTY(5,"없음", "없음", "없음");
    private int index;
    private String code;
    private String name;
    private String position;

    Positions(int index, String code, String name, String position) {
        this.index = index;
        this.code = code;
        this.name = name;
        this.position = position;
    }

    public int getIndex() {
        return index;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public static Positions findPosition(int positionIndex) {
        return Arrays.stream(Positions.values())
                .filter(position -> hasPosition(position, positionIndex))
                .findAny()
                .orElse(Positions.EMPTY);

    }
    public static Positions findPosition(String positionCode) {
        return Arrays.stream(Positions.values())
                .filter(position -> hasPosition(position, positionCode))
                .findAny()
                .orElse(Positions.EMPTY);
    }

    public static int findPositionIndex(String positionValue) {
        return Arrays.stream(Positions.values())
                .filter(position -> hasPositionValue(position, positionValue))
                .findAny()
                .orElse(Positions.EMPTY).getIndex();
    }
    private static boolean hasPosition(Positions from, int positionIndex) {
        return from.index == positionIndex;
    }
    private static boolean hasPosition(Positions from, String positionCode) {
        return from.code.equals(positionCode);
    }

    private static boolean hasPositionValue(Positions from, String positionValue) {
        return from.toString().equals(positionValue);
    }

}
