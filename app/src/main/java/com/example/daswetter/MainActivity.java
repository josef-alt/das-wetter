package com.example.daswetter;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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
import com.example.daswetter.models.Hour;
import com.example.daswetter.utils.DateConverter;
import com.example.daswetter.utils.Temperatures;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences preferences;
    private Temperatures.Unit temperatureUnit;
    private String location;
    private Handler weatherHandler;
    private Runnable weatherRunnable;
    private int weatherUpdateDelay = 10000;

    private TextView currentTempView, todaysHighView, todaysLowView;
    private TextView coverageView, currentFeelsLikeView, airQualityView;
    private RelativeLayout[] dailyForecasts;

    private RecyclerView hourlyForecastRecycler;
    private HourlyRVA hourlyForecastAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getPreferences(MODE_PRIVATE);
        temperatureUnit = Temperatures.Unit.valueOf(preferences.getString("temp_unit", "FAHRENHEIT"));

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

        hourlyForecastAdapter = new HourlyRVA(this, temperatureUnit);
        hourlyForecastRecycler = findViewById(R.id.hourlyForecast);
        hourlyForecastRecycler.setAdapter(hourlyForecastAdapter);
        hourlyForecastRecycler.setLayoutManager(new LinearLayoutManager(this));

        configureLocation();
    }

    /**
     * Loads the current location and sets up the weather handler/display updating
     */
    private void configureLocation() {
        location = preferences.getString("location", "Rochester, NY");

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

    /**
     * Used to retrieve changes from the Settings page
     */
    private ActivityResultLauncher<Intent> settingsLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if(o.getResultCode() == Settings.RESULT_OK) {
                        Intent data = o.getData();
                        String newLocation = data.getExtras().get("location").toString();
                        String newUnit = data.getExtras().get("unit").toString();

                        // ensure new data is valid
                        if(null != newLocation && null != newUnit) {
                            if(!newLocation.isEmpty() && !newUnit.isEmpty()) {
                                temperatureUnit = Temperatures.Unit.valueOf(newUnit);
                                location = newLocation;

                                // save to preferences
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("location", location);
                                editor.putString("temp_unit", temperatureUnit.toString());
                                editor.commit();

                                // update ui
                                configureLocation();
                            }
                        }
                    }
                }
            }
    );

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.settings) {
            Toast.makeText(this, "Opened Settings Menu", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, Settings.class);
            intent.putExtra("location", location);
            intent.putExtra("unit", temperatureUnit.toString());
            settingsLauncher.launch(intent);

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

                    setCurrentWeather(forecastResponse);
                    setDailyForcast(forecastResponse.getForecast());
                    setHourlyForecast(forecastResponse.getForecast());

                    Log.i("Forecast", forecastResponse.getForecast().getDailyForecast().size() + " days");
                }
            }

            @Override
            public void onFailure(Call<ForecastResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void setCurrentWeather(ForecastResponse forecastResponse) {
        double currentTemp = forecastResponse.getCurrent().getTemp(temperatureUnit);
        double feelsLike = forecastResponse.getCurrent().getFeelsLike(temperatureUnit);
        double high = forecastResponse.getForecast().getTodaysForecast().getDay().getMaxTemp(temperatureUnit);
        double low = forecastResponse.getForecast().getTodaysForecast().getDay().getMinTemp(temperatureUnit);

        currentTempView.setText(Temperatures.formatTemperature(currentTemp));
        todaysHighView.setText(Temperatures.formatTemperature(high));
        todaysLowView.setText(Temperatures.formatTemperature(low));

        coverageView.setText(forecastResponse.getCurrent().getCondition().getText());
        currentFeelsLikeView.setText("RealFeel: " + Temperatures.formatTemperature(feelsLike));
        airQualityView.setText("Air Quality: " + forecastResponse.getCurrent().getAirQuality());
    }

    private void setDailyForcast(Forecast forecast) {
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

            dayHigh.setText(Temperatures.formatTemperature(dailyWeather.getDay().getMaxTemp(temperatureUnit)));
            dayLow.setText(Temperatures.formatTemperature(dailyWeather.getDay().getMinTemp(temperatureUnit)));
        }
    }

    private void setHourlyForecast(Forecast forecastResponse) {
        int currentHour = LocalDateTime.now().getHour();
        List<Hour> hourList = new ArrayList<>(24);

        ForecastDay day = forecastResponse.getTodaysForecast();
        for(int h = 1; h <= 24; ++h) {
            currentHour += 1;
            if(currentHour > 23) {
                day = forecastResponse.getDailyForecast().get(1);
                currentHour = 0;
            }

            Log.d("HOURLY", day.getHourly().get(currentHour).getTime() + "");
            hourList.add(day.getHourly().get(currentHour));
        }

        hourlyForecastAdapter.setElements(hourList);
    }
}