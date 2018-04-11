package com.example.felig.fsilva_a3;

import java.util.UUID;
import java.util.Date;

/**
 * Created by felig on 4/10/2018.
 */

/*
    This Class stores the Weathar and Marker info
 */
public class WeatherMarker {
    private String mTemperature;
    private String mWeather;
    private String mDateandTime;
    private double mLat;
    private double mLon;
    private UUID mUID;

    public WeatherMarker() {
        this(UUID.randomUUID());
    }

    public WeatherMarker(UUID id) {
        mUID = id;
    }

    public UUID getId() {
        return mUID;
    }

    @Override
    public String toString(){
        return mWeather;
    }

    public String getTemperature() {
        return mTemperature;
    }

    public void setTemperature(String temperature) {
        mTemperature = temperature;
    }

    public String getWeather() {
        return mWeather;
    }

    public void setWeather(String weather) {
        mWeather = weather;
    }

    public String getDateandTime() {
        return mDateandTime;
    }

    public void setDateandTime(String dateandTime) {
        mDateandTime = dateandTime;
    }

    public double getLat() {
        return mLat;
    }

    public void setLat(double lat) {
        mLat = lat;
    }

    public double getLon() {
        return mLon;
    }

    public void setLon(double lon) {
        mLon = lon;
    }
}
