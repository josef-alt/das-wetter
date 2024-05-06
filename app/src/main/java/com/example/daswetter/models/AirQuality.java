package com.example.daswetter.models;

import android.util.Log;

/**
 *  Represent air quality index based on:
 *  https://air.plumelabs.com/learn/en/what-is-an-aqi
 */
public class AirQuality {
    private double o3, no2, pm2_5, pm10;

    private int plumeAQI = -1;

    public int getAirQuality() {
        if(plumeAQI == -1) {
            calculateAQI();
        }
        return plumeAQI;
    }

    public String toString() {
        return String.format("%s(%d)", getIndexValue(), getAirQuality());
    }

    public String getIndexValue() {
        if(plumeAQI == -1) {
            calculateAQI();
        }

        if(plumeAQI < 21) {
            return "Excellent";
        } else if(plumeAQI < 51) {
            return "Fair";
        } else if(plumeAQI < 101) {
            return "Poor";
        } else if(plumeAQI < 151) {
            return "Unhealthy";
        } else if(plumeAQI < 251) {
            return "Very Unhealthy";
        } else {
            return "Dangerous";
        }
    }

    /**
     *  "The Plume Air Quality Index value is the maximum of all pollutant-specific PAQIs."
     *  PAQI's computed based on Table 3 of the white paper
     */
    private void calculateAQI() {
        int ozonePAQI = o3 < 51 ? 20 : o3 < 101 ? 50 : o3 < 161 ? 100 : o3 < 241 ? 150 : 250;
        int no2PAQI = no2 < 11 ? 20 : no2 < 26 ? 50 : no2 < 201 ? 100 : no2 < 401 ? 150 : 250;
        int pm10PAQI = pm10 < 16 ? 20 : pm10 < 46 ? 50 : pm10 < 81 ? 100 : pm10 < 161 ? 150 : 250;
        int pm25PAQI = pm2_5 < 6 ? 20 : pm2_5 < 16 ? 50 : pm2_5 < 31 ? 100 : pm2_5 < 61 ? 150 : 250;

        Log.d("AQI", "o3 " + ozonePAQI);
        Log.d("AQI", "no2 " + no2PAQI);
        Log.d("AQI", "pm10 " + pm10PAQI);
        Log.d("AQI", "pm25 " + pm25PAQI);

        plumeAQI = Math.max(Math.max(ozonePAQI, no2PAQI), Math.max(pm10PAQI, pm25PAQI));
    }
}
