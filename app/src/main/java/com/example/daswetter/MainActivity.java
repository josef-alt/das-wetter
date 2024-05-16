package com.example.daswetter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.daswetter.api.ForecastResponse;
import com.example.daswetter.api.RestfulHandler;
import com.example.daswetter.models.Forecast;
import com.example.daswetter.models.ForecastDay;
import com.example.daswetter.utils.DateConverter;
import com.example.daswetter.utils.Temperatures;

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
    private RelativeLayout[] dailyForecasts;

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

        dailyForecasts = new RelativeLayout[] {
                findViewById(R.id.day1),
                findViewById(R.id.day2),
                findViewById(R.id.day3),
                findViewById(R.id.day4),
                findViewById(R.id.day5),
                findViewById(R.id.day6)
        };

        location = preferences.getString("location", "Canada");

        weatherHandler = new Handler();
        Log.d("loc", location);
        getSupportActionBar().setTitle(location);
        updateWeatherDisplay();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.settings) {
            Toast.makeText(this, "Opened Settings Menu", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
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

                    setCurrentWeather(forecastResponse, unit);
                    setDailyForcast(forecastResponse.getForecast(), unit);

                    Log.i("Forecast", forecastResponse.getForecast().getDailyForecast().size() + " days");
                }
            }

            @Override
            public void onFailure(Call<ForecastResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void setCurrentWeather(ForecastResponse forecastResponse, Temperatures.Unit unit) {
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

    private void setDailyForcast(Forecast forecast, Temperatures.Unit unit) {
        Log.d("FORECAST", Integer.toString(forecast.getDailyForecast().size()));

        // retrieved 7-day forecast
        // 0 - today
        // dailyForecast[0] <=> forecast.get(1)
        for(int day = 1; day < 7; ++day) {
            RelativeLayout view = dailyForecasts[day - 1];
            ForecastDay dailyWeather = forecast.getDailyForecast().get(day);

            TextView dayOfWeek = view.findViewById(R.id.dayLabel);
            ImageView dayWeather = view.findViewById(R.id.dayIcon);
            TextView dayHigh = view.findViewById(R.id.dayHigh);
            TextView dayLow = view.findViewById(R.id.dayLow);

            dayOfWeek.setText(DateConverter.dateToDay(dailyWeather.getDate()));

            // instead of pulling every weather icon from the web I decided to include them in res
            String iconPath = dailyWeather.getDay().getCondition().getIcon();
            int index = iconPath.indexOf("64x64") + 6;
            String resource = iconPath.substring(index)
                    .replace("/", "")
                    .replace(".png", "");
            int resourceID = getResources().getIdentifier(resource, "drawable", getPackageName());
            dayWeather.setImageResource(resourceID);

            dayHigh.setText(Temperatures.formatTemperature(dailyWeather.getDay().getMaxTemp(unit)));
            dayLow.setText(Temperatures.formatTemperature(dailyWeather.getDay().getMinTemp(unit)));
        }
    }
}