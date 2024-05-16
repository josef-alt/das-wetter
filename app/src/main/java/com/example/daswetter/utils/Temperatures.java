package com.example.daswetter.utils;

public class Temperatures {
    public enum Unit {
        FAHRENHEIT, CELSIUS
    }

    public static String formatTemperature(double temp) {
        return String.format("%.0f°",temp);
    }
}
