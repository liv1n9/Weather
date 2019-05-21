package com.example.weather.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.example.weather.R;
import com.example.weather.api.ForecastApi;
import com.example.weather.database.WeatherDatabase;
import com.example.weather.model.City;

import java.net.MalformedURLException;

public class MainActivity extends AppCompatActivity {

    private WeatherDatabase weatherDatabase;
    private SearchView searchView;
    private String cityQuery;
    private Handler handler = new Handler();

    public static String SETTING = "SETTING";

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
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initWeatherView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action_bar, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int i) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int i) {

                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                CursorAdapter cursorAdapter = searchView.getSuggestionsAdapter();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                cityQuery = s;
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, 250);
                //TODO
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_setting) {
            Intent intent = new Intent(MainActivity.this, StartActivity.class);
            intent.putExtra(SETTING, true);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        initWeatherView();
        super.onNewIntent(intent);
    }

    private void initWeatherView() {
        ForecastApi api = new ForecastApi(this);
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            float lat = extra.getFloat(StartActivity.LAT, 0.0f);
            float lng = extra.getFloat(StartActivity.LNG, 0.0f);
            String unit = extra.getString(StartActivity.UNIT, "SI").toLowerCase();
            String loc = extra.getString(StartActivity.LOC, "Earth");
            api.setWeatherInfoView(lat, lng, unit, loc);
        }
    }

    private void applyCursor(Cursor cursor) {
        String cityName = cursor.getString(cursor.getColumnIndex(WeatherDatabase.COLUMN_CITY_NAME));
        int cityId = cursor.getInt(cursor.getColumnIndex(WeatherDatabase.COLUMN_CITY_ID));
        City curCity = weatherDatabase.getCity(cityId);
    }

}
