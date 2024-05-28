package com.example.daswetter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.daswetter.api.ForecastResponse;
import com.example.daswetter.api.RestfulHandler;
import com.example.daswetter.api.WeatherResponse;
import com.example.daswetter.models.Location;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Settings extends AppCompatActivity {

    private String[] LOCATIONS;
    private AutoCompleteTextView locationTextView;
    private RadioButton fahrenheit, celsius;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Intent intent = getIntent();

        String oldLocation = "Canada";
        String oldUnit = "FAHRENHEIT";
        if (null != intent) {
            oldLocation = intent.getStringExtra("location");
            oldUnit = intent.getStringExtra("unit");
        }

        LOCATIONS = getResources().getStringArray(R.array.location_options);
        locationTextView = findViewById(R.id.locationTextView);
        locationTextView.setText(oldLocation);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, LOCATIONS);
        locationTextView.setAdapter(adapter);

        fahrenheit = findViewById(R.id.fahrenheit);
        celsius = findViewById(R.id.celsius);
        if (oldUnit.equals("FAHRENHEIT")) {
            fahrenheit.setChecked(true);
        } else {
            celsius.setChecked(true);
        }

        saveButton = findViewById(R.id.saveChanges);
        saveButton.setOnClickListener((click) -> {
            String newLocation = locationTextView.getText().toString();
            String newUnit = fahrenheit.isChecked() ? "FAHRENHEIT" : "CELSIUS";

            if(!newLocation.isEmpty()) {
                validateAndExit(newLocation, newUnit);
            }
        });
    }

    /**
     *  Sends a dummy api call to determine whether the new location is valid or not
     */
    private void validateAndExit(String location, String unit) {
        RestfulHandler r = new RestfulHandler();
        Call<WeatherResponse> call = r.getCurrentWeather(getString(R.string.weatherapi_key), location);

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Location found = response.body().getLocation();
                    String newLocation = found.getName() + ", " + found.getRegion();
                    Toast.makeText(Settings.this, "Setting location to: " + newLocation, Toast.LENGTH_LONG).show();

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("location", newLocation);
                    resultIntent.putExtra("unit", unit);
                    setResult(Settings.RESULT_OK, resultIntent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Toast.makeText(Settings.this, "invalid location", Toast.LENGTH_SHORT).show();
            }
        });
    }
}