package com.example.daswetter.api;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestfulHandler {
    private static final String weatherURL = "http://api.weatherapi.com/";

    public Call<WeatherResponse> getCurrentWeather(String key, String location) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(weatherURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        return apiService.getCurrentWeather(key, location);
    }

    public Call<ForecastResponse> getForecast(String key, String location) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(weatherURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        return apiService.getForecast(key, location);
    }
}
