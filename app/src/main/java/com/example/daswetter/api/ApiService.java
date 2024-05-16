package com.example.daswetter;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
        @Headers({"Accept: application/json"})
        @GET("/v1/current.json?aqi=yes")
        Call<WeatherResponse> getCurrentWeather(@Query("key") String apiKey, @Query("q") String location);

        @Headers({"Accept: accplication/json"})
        @GET("/v1/forecast.json?aqi=yes&days=7")
        Call<ForecastResponse> getForecast(@Query("key") String apiKey, @Query("q") String location);
}
