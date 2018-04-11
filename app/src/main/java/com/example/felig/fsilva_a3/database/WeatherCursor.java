package com.example.felig.fsilva_a3.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.felig.fsilva_a3.WeatherMarker;

import java.util.UUID;

/**
 * Created by felig on 4/10/2018.
 */

/*
    Uses cursor to store data in database
 */
public class WeatherCursor extends CursorWrapper{
    public WeatherCursor(Cursor cursor) {
        super(cursor);
    }
    public WeatherMarker getMarkerInfo() {
        String uuidString = getString(getColumnIndex(WeatherDatabase.WeatherTable.Cols.UUID));
        double lat = getDouble(getColumnIndex(WeatherDatabase.WeatherTable.Cols.LAT));
        double lon = getDouble(getColumnIndex(WeatherDatabase.WeatherTable.Cols.LONG));
        String datetime = getString(getColumnIndex(WeatherDatabase.WeatherTable.Cols.DATETIME));
        String temperature = getString(getColumnIndex(WeatherDatabase.WeatherTable.Cols.TEMPERATURE));
        String weather = getString(getColumnIndex(WeatherDatabase.WeatherTable.Cols.WEATHER));

        WeatherMarker markerInfo = new WeatherMarker(UUID.fromString(uuidString));
        markerInfo.setLat(lat);
        markerInfo.setLon(lon);
        markerInfo.setDateandTime(datetime);
        markerInfo.setTemperature(temperature);
        markerInfo.setWeather(weather);

        return markerInfo;
    }
}

