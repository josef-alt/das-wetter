package com.example.daswetter.models;

import java.util.List;

public class Forecast {
    private List<ForecastDay> forecastday;

    public ForecastDay getTodaysForecast() {
        if(null == forecastday || forecastday.isEmpty())
            return null;
        return forecastday.get(0);
    }

    public List<ForecastDay> getDailyForecast() {
        return forecastday;
    }
}
