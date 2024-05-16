package com.example.daswetter.api;

import com.example.daswetter.models.Current;
import com.example.daswetter.models.Forecast;

public class ForecastResponse {
    private Current current;

    private Forecast forecast;

    public Current getCurrent() {
        return current;
    }

    public Forecast getForecast() {
        return forecast;
    }
}
