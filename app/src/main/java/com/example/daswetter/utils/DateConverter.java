package com.example.daswetter.utils;

import java.time.LocalDate;

public class DateConverter {
    public static String dateToDay(String date) {
        LocalDate localDate = LocalDate.parse(date);
        return localDate.getDayOfWeek().name().toUpperCase().substring(0, 3);
    }
}
