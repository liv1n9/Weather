package com.example.weather.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.example.weather.R;
import com.example.weather.api.ForecastApi;
import com.example.weather.database.WeatherDatabase;
import com.example.weather.model.City;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private WeatherDatabase weatherDatabase;
    private SearchView searchView;
    private String cityQuery;
    private float curLat;
    private float curLng;
    private String curUnit;
    private String curLoc;
    private Handler handler = new Handler();
    private boolean isSearchResult;

    public static String SETTING = "SETTING";
    private static String SEARCH_RESULT = "SEARCH_RESULT";

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

        isSearchResult = false;
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            isSearchResult = extra.getBoolean(SEARCH_RESULT, false);
        }

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initWeatherView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action_bar, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        MenuItem settingItem = menu.findItem(R.id.action_setting);

        if (isSearchResult) {
            searchItem.setVisible(false);
            settingItem.setVisible(false);
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            invalidateOptionsMenu();
        } else {
            searchView = (SearchView) searchItem.getActionView();
            searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
                @Override
                public boolean onSuggestionSelect(int i) {
                    return true;
                }

                @Override
                public boolean onSuggestionClick(int i) {
                    Cursor cursor = searchView.getSuggestionsAdapter().getCursor();
                    cursor.moveToPosition(i);
                    applyCursor(cursor);
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
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    cityQuery = s;
                    handler.removeCallbacks(runnable);
                    handler.postDelayed(runnable, 250);
                    return true;
                }
            });
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_setting:
                Intent intent = new Intent(MainActivity.this, StartActivity.class);
                intent.putExtra(SETTING, true);
                intent.putExtra(StartActivity.LAT, curLat);
                intent.putExtra(StartActivity.LNG, curLng);
                intent.putExtra(StartActivity.UNIT, curUnit);
                intent.putExtra(StartActivity.LOC, curLoc);
                startActivityForResult(intent, 1);
                return true;
            case android.R.id.home:
                if (isSearchResult) finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == 1) {
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (searchView != null) {
            searchView.clearFocus();
        }
    }

    private void initWeatherView() {
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            curLat = extra.getFloat(StartActivity.LAT, 0.0f);
            curLng = extra.getFloat(StartActivity.LNG, 0.0f);
            curUnit = extra.getString(StartActivity.UNIT, "SI");
            curLoc = extra.getString(StartActivity.LOC, "Earth");
            ForecastApi api = new ForecastApi(this, curLat, curLng, curUnit.toLowerCase(), curLoc);
            api.setWeatherInfoView();
        }
    }

    private void applyCursor(Cursor cursor) {
        cityQuery = cursor.getString(cursor.getColumnIndex(WeatherDatabase.COLUMN_CITY_NAME));
        int cityId = cursor.getInt(cursor.getColumnIndex(WeatherDatabase.COLUMN_CITY_ID));
        City curCity = weatherDatabase.getCity(cityId);
        if (curCity != null) {
            searchView.setQuery(cityQuery, false);
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(SEARCH_RESULT, true);
            intent.putExtra(StartActivity.LAT, curCity.getCityLat());
            intent.putExtra(StartActivity.LNG, curCity.getCityLng());
            intent.putExtra(StartActivity.UNIT, curUnit);
            intent.putExtra(StartActivity.LOC, curCity.getCityName() + " - " + curCity.getCityCountry());
            startActivity(intent);
        }
    }

}
