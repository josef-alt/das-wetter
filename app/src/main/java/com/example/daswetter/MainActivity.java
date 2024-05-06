package com.example.daswetter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences preferences;
    private String location;
    private Handler weatherHandler;
    private Runnable weatherRunnable;
    private int weatherUpdateDelay = 10000;

    private TextView currentTempView, todaysHighView, todaysLowView;
    private TextView coverageView, currentFeelsLikeView, airQualityView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getPreferences(MODE_PRIVATE);

        currentTempView = findViewById(R.id.currentTemp);
        todaysHighView = findViewById(R.id.currentHigh);
        todaysLowView = findViewById(R.id.currentLow);

        coverageView = findViewById(R.id.cloudCoverage);
        currentFeelsLikeView = findViewById(R.id.feelsLike);
        airQualityView = findViewById(R.id.airQuality);
        location = preferences.getString("location", "Canada");

        weatherHandler = new Handler();
        Log.d("loc", location);
        updateWeatherDisplay();
    }

    @Override
    protected void onResume() {
        weatherHandler.postDelayed(weatherRunnable = new Runnable() {
            @Override
            public void run() {
                updateWeatherDisplay();
                weatherHandler.postDelayed(weatherRunnable, weatherUpdateDelay);
            }
        }, weatherUpdateDelay);
        super.onResume();
    }

    @Override
    protected void onPause() {
        weatherHandler.removeCallbacks(weatherRunnable);
        super.onPause();
    }

    public void updateWeatherDisplay() {
        RestfulHandler r = new RestfulHandler();
        Call<ForecastResponse> call = r.getForecast(getString(R.string.weatherapi_key), location);

        call.enqueue(new Callback<ForecastResponse>() {
            @Override
            public void onResponse(Call<ForecastResponse> call, Response<ForecastResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ForecastResponse forecastResponse = response.body();

                    Temperatures.Unit unit = Temperatures.Unit.valueOf(preferences.getString("temp_unit", "FAHRENHEIT"));
                    double currentTemp = forecastResponse.getCurrent().getTemp(unit);
                    double feelsLike = forecastResponse.getCurrent().getFeelsLike(unit);
                    double high = forecastResponse.getForecast().getTodaysForecast().getDay().getMaxTemp(unit);
                    double low = forecastResponse.getForecast().getTodaysForecast().getDay().getMinTemp(unit);

                    currentTempView.setText(Temperatures.formatTemperature(currentTemp));
                    todaysHighView.setText(Temperatures.formatTemperature(high));
                    todaysLowView.setText(Temperatures.formatTemperature(low));

                    coverageView.setText(forecastResponse.getCurrent().getCondition().getText());
                    currentFeelsLikeView.setText("RealFeel: " + Temperatures.formatTemperature(feelsLike));
                    airQualityView.setText("Air Quality: " + forecastResponse.getCurrent().getAirQuality());
                }
            }

            @Override
            public void onFailure(Call<ForecastResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}