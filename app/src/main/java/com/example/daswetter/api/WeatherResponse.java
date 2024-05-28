package com.example.daswetter.api;

import com.example.daswetter.models.Current;
import com.example.daswetter.models.Location;

public class WeatherResponse {
    private Location location;

    private Current current;

    public Location getLocation() {
        return location;
    }

    public Current getCurrent() {
        return current;
    }
}
