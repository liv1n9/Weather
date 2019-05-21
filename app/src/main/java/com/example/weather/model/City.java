package com.example.weather.model;

public class City {
    private int _id;
    private String cityName;
    private String cityCountry;
    private float cityLat;
    private float cityLng;

    public City(int _id, String cityName, String cityCountry, float cityLat, float cityLng) {
        this._id = _id;
        this.cityName = cityName;
        this.cityCountry = cityCountry;
        this.cityLat = cityLat;
        this.cityLng = cityLng;
    }

    public int getId() {
        return _id;
    }

    public String getCityName() {
        return cityName;
    }

    public String getCityCountry() {
        return cityCountry;
    }

    public float getCityLat() {
        return cityLat;
    }

    public float getCityLng() {
        return cityLng;
    }
}
