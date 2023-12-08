package com.ysb.jwgio.global.common.converter;

import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;

@Component
public class LocalDateTimeConverter {
    public static LocalDateTime ISOStringToLocalDateTime(String ISOStringDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        return LocalDateTime.parse(ISOStringDate, formatter).withSecond(0).withNano(0);
    }

    public static LocalDateTime ISOStringToLocalDateTimeHHmmSS(String ISOStringDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return LocalDateTime.parse(ISOStringDate, formatter).withSecond(0).withNano(0);
    }

    public static String DayOfWeekToString(DayOfWeek dayOfWeek) {
        String dow = "";
        switch (dayOfWeek) {
            case MONDAY:
                dow = "월요일";
                break;
            case TUESDAY:
                dow = "화요일";
                break;
            case WEDNESDAY:
                dow = "수요일";
                break;
            case THURSDAY:
                dow = "목요일";
                break;
            case FRIDAY:
                dow = "금요일";
                break;
            case SATURDAY:
                dow = "토요일";
                break;
            case SUNDAY:
                dow = "일요일";
                break;
        }
        return dow;
    }

    public static String MonthtoString(Month month) {
        String mon = "";
        switch (month) {
            case JANUARY:
                mon = "1월";
                break;
            case FEBRUARY:
                mon = "2월";
                break;
            case MARCH:
                mon = "3월";
                break;
            case APRIL:
                mon = "4월";
                break;
            case MAY:
                mon = "5월";
                break;
            case JUNE:
                mon = "6월";
                break;
            case JULY:
                mon = "7월";
                break;
            case AUGUST:
                mon = "8월";
                break;
            case SEPTEMBER:
                mon = "9월";
                break;
            case OCTOBER:
                mon = "10월";
                break;
            case NOVEMBER:
                mon = "11월";
                break;
            case DECEMBER:
                mon = "12월";
                break;
        }
        return mon;
    }
}
