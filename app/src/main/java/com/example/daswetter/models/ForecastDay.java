package com.example.daswetter.models;

import java.util.List;

public class ForecastDay {
    private String date;

    private Day day;

    private List<Hour> hour;

    public Day getDay() {
        return day;
    }

    public String getDate() {
        return date;
    }

    public List<Hour> getHourly() {
        return hour;
    }
}
