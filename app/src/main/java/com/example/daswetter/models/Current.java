package com.example.daswetter.models;

import com.example.daswetter.utils.Temperatures;

public class Current {
    private Location location;
    private double temp_c, temp_f;

    private double feelslike_c, feelslike_f;

    private int is_day;

    private Condition condition;

    private AirQuality air_quality;

    private double wind_mph, wind_kph;
    private double wind_degree;
    private String wind_dir;

    private double pressure_mb, pressure_in;

    private double precip_mm, precip_in;

    private double humidity;
    private double cloud;

    private double vis_km, vis_miles;
    private double uv;
    private double gust_mph, gust_kph;

    public Location getLocation() {
        return location;
    }

    public double getTemp(Temperatures.Unit unit) {
        if(unit == Temperatures.Unit.CELSIUS) {
            return temp_c;
        } else {
            return temp_f;
        }
    }

    public double getFeelsLike(Temperatures.Unit unit) {
        if(unit == Temperatures.Unit.CELSIUS) {
            return feelslike_c;
        } else {
            return feelslike_f;
        }
    }

    public Condition getCondition() {
        return condition;
    }

    public AirQuality getAirQuality() {
        return air_quality;
    }
}
