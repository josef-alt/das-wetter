package com.example.daswetter.models;

import com.example.daswetter.utils.Temperatures;

public class Day {
    private double maxtemp_c, maxtemp_f;
    private double mintemp_c, mintemp_f;
    private double avgtemp_c, avgtemp_f;
    private Condition condition;

    public double getMinTemp(Temperatures.Unit unit) {
        if(unit == Temperatures.Unit.CELSIUS) {
            return mintemp_c;
        } else {
            return mintemp_f;
        }
    }

    public double getMaxTemp(Temperatures.Unit unit) {
        if(unit == Temperatures.Unit.CELSIUS) {
            return maxtemp_c;
        } else {
            return maxtemp_f;
        }
    }

    public Condition getCondition() {
        return condition;
    }
}
