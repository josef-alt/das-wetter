package com.example.daswetter.models;

import com.example.daswetter.utils.Temperatures;

public class Hour {
    private String time;

    private double temp_c, temp_f;

    private Condition condition;

    private double wind_mph, wind_kph;

    private int chance_of_rain;

    public String getTime() {
        return time;
    }

    public double getTemp_c() {
        return temp_c;
    }

    public double getTemp_f() {
        return temp_f;
    }

    public Condition getCondition() {
        return condition;
    }

    public double getWind_mph() {
        return wind_mph;
    }

    public double getWind_kph() {
        return wind_kph;
    }

    public int getChance_of_rain() {
        return chance_of_rain;
    }

    public double getTemp(Temperatures.Unit unit) {
        if(unit == Temperatures.Unit.CELSIUS) {
            return temp_c;
        } else {
            return temp_f;
        }
    }

    public double getWindSpeed() {
        return wind_mph;
    }
}
