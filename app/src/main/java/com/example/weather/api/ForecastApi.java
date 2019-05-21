package com.example.weather.api;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.weather.R;
import com.example.weather.adapter.ForecastAdapter;
import com.example.weather.model.api.Data;
import com.example.weather.model.api.Forecast;
import com.example.weather.utility.GlideUtils;
import com.google.gson.Gson;

public class ForecastApi {
    private static final String FORECAST_URL = "https://api.darksky.net/forecast/96e0788aafdf65a6ffe04079e42a1702";
    private AppCompatActivity context;
    private static final String degree = "°";

    public ForecastApi(Context context) {
        this.context = (AppCompatActivity) context;
    }

    public void setWeatherInfoView(float lat, float lng, final String unit, final String loc) {
        RequestQueue queue = Volley.newRequestQueue(context);
        final String tempUnit, speedUnit;
        if (unit.equals("us")) {
            tempUnit = "F";
            speedUnit = "mph";
        } else {
            tempUnit = "C";
            speedUnit = "m/s";
        }
        String url = String.format(
                "%s/%s,%s?units=%s",
                FORECAST_URL,
                Float.toString(lat),
                Float.toString(lng),
                unit
        );
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                Forecast forecast = gson.fromJson(response, Forecast.class);

                ViewGroup viewGroup = context.findViewById(R.id.info_layout);
                TextView location = viewGroup.findViewById(R.id.info_location);
                TextView temperature = viewGroup.findViewById(R.id.info_temperature);
                TextView summary = viewGroup.findViewById(R.id.info_description);
                TextView humidity = viewGroup.findViewById(R.id.info_humidity);
                TextView wind = viewGroup.findViewById(R.id.info_wind);
                ImageView icon = viewGroup.findViewById(R.id.info_icon);

                Data current = forecast.getCurrently();
                String temp = (int) current.getTemperature() + degree + tempUnit;
                String dec = current.getSummary();
                String hum = "Humidity: " + (int)(current.getHumidity() * 100) + "%";
                String windSpd = "Wind speed: " + current.getWindSpeed() + speedUnit;
                GlideUtils.loadIconDarkSky(context, icon, current.getIcon());

                location.setText(loc);
                temperature.setText(temp);
                summary.setText(dec);
                humidity.setText(hum);
                wind.setText(windSpd);

                ForecastAdapter adapter = new ForecastAdapter(
                        context, forecast.getDaily().getData(), forecast.getTimezone());
                RecyclerView forecastView = context.findViewById(R.id.forecast_view);
                forecastView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                forecastView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL));
                forecastView.setAdapter(adapter);
            }
        }, null);
        queue.add(stringRequest);
    }
}
