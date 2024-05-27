package com.example.daswetter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;

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
                Intent resultIntent = new Intent();
                resultIntent.putExtra("location", newLocation);
                resultIntent.putExtra("unit", newUnit);
                setResult(Settings.RESULT_OK, resultIntent);
                finish();
            }
        });
    }
}