package com.example.weather.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.weather.R;
import com.example.weather.model.api.Data;
import com.example.weather.model.api.SimpleDate;
import com.example.weather.utility.GlideUtils;

import java.util.ArrayList;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {
    private Context context;
    private String timezone;
    private ArrayList<Data> forecastData;
    private final static String degree = "°";

    public ForecastAdapter(Context context, ArrayList<Data> forecastData, String timezone) {
        this.context = context;
        this.forecastData = forecastData;
        this.timezone = timezone;
    }

    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.forecast_info_main, viewGroup,false);
        return new ForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder forecastViewHolder, int i) {
        if (i == 0) {
            forecastViewHolder.itemView.setBackgroundColor(forecastViewHolder.itemView.getResources().getColor(R.color.secondaryColor, null));
        } else if (i == getItemCount() - 1) {
            forecastViewHolder.itemView.setBackgroundColor(forecastViewHolder.itemView.getResources().getColor(R.color.primaryColor, null));
        }
        Data data = forecastData.get(i);
        long timestamp = data.getTime();
        SimpleDate date = new SimpleDate(timezone, timestamp * 1000);
        String dateText = date.getDayOfWeek() + " " + date.getDayOfMonth();
        String humidity = "Humidity: " + (int) (data.getHumidity() * 100) + "%";
        String temperatureMax = (int) data.getTemperatureMax() + degree;
        String temperatureMin = (int) data.getTemperatureMin() + degree;

        forecastViewHolder.date.setText(dateText);
        forecastViewHolder.humidity.setText(humidity);
        forecastViewHolder.tempMax.setText(temperatureMax);
        forecastViewHolder.tempMin.setText(temperatureMin);
        GlideUtils.loadIconDarkSky(context, forecastViewHolder.icon, data.getIcon());
    }

    @Override
    public int getItemCount() {
        return forecastData.size();
    }

    static class ForecastViewHolder extends RecyclerView.ViewHolder {
        TextView date, humidity, tempMax, tempMin;
        ImageView icon;

        ForecastViewHolder(@NonNull View itemView) {
            super(itemView);
            this.date = itemView.findViewById(R.id.info_date);
            this.humidity = itemView.findViewById(R.id.info_humidity);
            this.tempMax = itemView.findViewById(R.id.info_temp_max);
            this.tempMin = itemView.findViewById(R.id.info_temp_min);
            this.icon = itemView.findViewById(R.id.info_icon);
        }
    }
}
