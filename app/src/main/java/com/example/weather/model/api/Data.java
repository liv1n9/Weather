package com.example.weather.model.api;

public class Data {
    private long time;
    private String summary;
    private String icon;
    private float temperature;
    private float temperatureMin;
    private float temperatureMax;
    private float humidity;
    private float windSpeed;

    public Data(long time, String summary, String icon, float temperature, float temperatureMin, float temperatureMax, float humidity, float windSpeed) {
        this.time = time;
        this.summary = summary;
        this.icon = icon;
        this.temperature = temperature;
        this.temperatureMin = temperatureMin;
        this.temperatureMax = temperatureMax;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
    }

    public long getTime() {
        return time;
    }

    public String getSummary() {
        return summary;
    }

    public String getIcon() {
        return icon;
    }

    public float getTemperatureMin() {
        return temperatureMin;
    }

    public float getTemperatureMax() {
        return temperatureMax;
    }

    public float getHumidity() {
        return humidity;
    }

    public float getWindSpeed() {
        return windSpeed;
    }

    public float getTemperature() {
        return temperature;
    }
}
