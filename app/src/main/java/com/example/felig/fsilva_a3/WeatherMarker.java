package com.example.felig.fsilva_a3;

import java.util.UUID;
import java.util.Date;

/**
 * Created by felig on 4/10/2018.
 */

public class WeatherMarker {
    private String mTemperature;
    private String mWeather;
    private Date mDateandTime;
    private String mLat;
    private String mLon;
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

    public Date getDateandTime() {
        return mDateandTime;
    }

    public void setDateandTime(Date dateandTime) {
        mDateandTime = dateandTime;
    }

    public String getLat() {
        return mLat;
    }

    public void setLat(String lat) {
        mLat = lat;
    }

    public String getLon() {
        return mLon;
    }

    public void setLon(String lon) {
        mLon = lon;
    }
}
