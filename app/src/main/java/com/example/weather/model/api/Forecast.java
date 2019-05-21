package com.example.weather.model.api;

public class Forecast {
    private float latitude;
    private float longitude;
    private String timezone;
    private Data currently;
    private Block daily;

    public Forecast(float latitude, float longitude, String timezone, Data currently, Block daily) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timezone = timezone;
        this.currently = currently;
        this.daily = daily;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public String getTimezone() {
        return timezone;
    }

    public Data getCurrently() {
        return currently;
    }

    public Block getDaily() {
        return daily;
    }
}
