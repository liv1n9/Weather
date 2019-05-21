package com.example.weather.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.example.weather.R;
import com.example.weather.database.WeatherDatabase;
import com.example.weather.model.City;

import java.util.Objects;

public class StartActivity extends AppCompatActivity {

    private static final String SHARED_PREFERENCE_NAME = "shared_info";
    private static final String FIRST_USE = "FIRST_USE";
    static final String LAT = "LAT";
    static final String LNG = "LNG";
    static final String UNIT = "UNIT";
    static final String LOC = "LOC";

    private SharedPreferences.Editor editor;
    private WeatherDatabase weatherDatabase;
    private SearchView searchView;
    private Button saveButton;

    private float curLat, curLng;
    private String curUnit, curLoc;
    private Handler handler = new Handler();
    private String cityQuery;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            searchView.setSuggestionsAdapter(weatherDatabase.getCitySuggestionAdapter(cityQuery));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        weatherDatabase = new WeatherDatabase(this);
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        boolean setting = false;
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            setting = extra.getBoolean(MainActivity.SETTING);
            curLat = extra.getFloat(LAT);
            curLng = extra.getFloat(LNG);
            curUnit = extra.getString(UNIT);
            curLoc = extra.getString(LOC);
        }
        boolean firstUse = sharedPreferences.getBoolean(FIRST_USE, true);
        if (firstUse || setting) {
            setContentView(R.layout.activity_start);
            editor = sharedPreferences.edit();
            initSaveButton();
            initSearchView(setting);
            initRadioGroup(setting);
            initCancel(setting);
            editor.apply();
        } else {
            float lat = sharedPreferences.getFloat(LAT, 0.0f);
            float lng = sharedPreferences.getFloat(LNG, 0.0f);
            String unit = sharedPreferences.getString(UNIT, "SI");
            String loc = sharedPreferences.getString(LOC, "Earth");

            Intent intent = new Intent(StartActivity.this, MainActivity.class);
            intent.putExtra(LAT, lat);
            intent.putExtra(LNG, lng);
            intent.putExtra(UNIT, unit);
            intent.putExtra(LOC, loc);
            startActivity(intent);

            finish();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void initSaveButton() {
        saveButton = findViewById(R.id.action_save);
        saveButton.setEnabled(false);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                intent.putExtra(LAT, curLat);
                intent.putExtra(LNG, curLng);
                intent.putExtra(UNIT, curUnit);
                intent.putExtra(LOC, curLoc);

                editor.putBoolean(FIRST_USE, false);
                editor.putFloat(LAT, curLat);
                editor.putFloat(LNG, curLng);
                editor.putString(UNIT, curUnit);
                editor.putString(LOC, curLoc);
                editor.commit();
                startActivity(intent);
                StartActivity.this.finish();
            }
        });
    }

    private void initSearchView(boolean setting) {
        searchView = findViewById(R.id.search_view_city);

        if (setting) {
            String[] temp = curLoc.split(" - ");
            searchView.setQuery(temp[0], false);
        }

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int i) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int i) {
                Cursor cursor = searchView.getSuggestionsAdapter().getCursor();
                cursor.moveToPosition(i);
                applyCursor(cursor);
                View view = StartActivity.this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager manager = (InputMethodManager) StartActivity.this.getSystemService(INPUT_METHOD_SERVICE);
                    Objects.requireNonNull(manager).hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                return true;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                CursorAdapter adapter = searchView.getSuggestionsAdapter();
                if (adapter != null && !adapter.isEmpty()) {
                    Cursor cursor = adapter.getCursor();
                    cursor.moveToFirst();
                    applyCursor(cursor);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                cityQuery = s;
                saveButton.setEnabled(false);
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, 250);
                return false;
            }
        });
    }

    private void initRadioGroup(boolean setting) {
        RadioGroup radioGroup = findViewById(R.id.radio_units);
        if (setting) {
            if (curUnit.equals("US")) {
                RadioButton usButton = findViewById(R.id.radio_us);
                usButton.setChecked(true);
            } else {
                RadioButton siButton = findViewById(R.id.radio_si);
                siButton.setChecked(true);
            }
        }
        RadioButton checkedButton = findViewById(radioGroup.getCheckedRadioButtonId());
        curUnit = checkedButton.getText().toString();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedButton = StartActivity.this.findViewById(checkedId);
                curUnit = checkedButton.getText().toString();
            }
        });
    }

    private void initCancel(boolean setting) {
        Button cancelButton = findViewById(R.id.action_cancel);
        if (!setting) cancelButton.setVisibility(View.GONE);
        else {
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StartActivity.this.finish();
                }
            });
        }
    }

    private void applyCursor(Cursor cursor) {
        String cityName = cursor.getString(cursor.getColumnIndex(WeatherDatabase.COLUMN_CITY_NAME));
        int cityId = cursor.getInt(cursor.getColumnIndex(WeatherDatabase.COLUMN_CITY_ID));
        City curCity = weatherDatabase.getCity(cityId);
        if (curCity != null) {
            curLat = curCity.getCityLat();
            curLng = curCity.getCityLng();
            curLoc = curCity.getCityName() + " - " + curCity.getCityCountry();
            searchView.setQuery(cityName, false);
            saveButton.setEnabled(true);
        }
    }
}
