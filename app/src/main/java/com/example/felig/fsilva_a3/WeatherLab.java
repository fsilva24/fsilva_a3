package com.example.felig.fsilva_a3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.felig.fsilva_a3.database.WeatherCursor;
import com.example.felig.fsilva_a3.database.WeatherDBHelper;
import com.example.felig.fsilva_a3.database.WeatherDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by felig on 4/10/2018.
 */

public class WeatherLab {
    private static WeatherLab sMarkerLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static WeatherLab get(Context context) {
        if (sMarkerLab == null) {
            sMarkerLab = new WeatherLab(context);
        }
        return sMarkerLab;
    }
    private WeatherLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new WeatherDBHelper(mContext).getWritableDatabase();
    }

    public void clearDB (){
        mDatabase.delete(WeatherDatabase.WeatherTable.NAME, null, null);
    }

    public void addWeather(WeatherMarker m) {
        ContentValues values = getContentValues(m);
        mDatabase.insert(WeatherDatabase.WeatherTable.NAME, null, values);
    }
    public List<WeatherMarker> getMarkers() {

        List<WeatherMarker> markers = new ArrayList<>();
        WeatherCursor cursor = queryCrimes(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                markers.add(cursor.getMarkerInfo());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return markers;
    }
    private WeatherCursor queryCrimes(String whereClause, String[] whereArgs) {       Cursor cursor = mDatabase.query(
            WeatherDatabase.WeatherTable.NAME,
            null, // columns - null selects all columns
            whereClause,
            whereArgs,
            null, // groupBy
            null, // having
            null // orderBy
    );
        return new WeatherCursor(cursor);
    }
    private static ContentValues getContentValues(WeatherMarker marker) {
        ContentValues values = new ContentValues();
        values.put(WeatherDatabase.WeatherTable.Cols.UUID, marker.getId().toString());
        values.put(WeatherDatabase.WeatherTable.Cols.LAT, marker.getLat());
        values.put(WeatherDatabase.WeatherTable.Cols.LONG, marker.getLon());
        values.put(WeatherDatabase.WeatherTable.Cols.DATETIME, marker.getDateandTime());
        values.put(WeatherDatabase.WeatherTable.Cols.TEMPERATURE, marker.getTemperature());
        values.put(WeatherDatabase.WeatherTable.Cols.WEATHER, marker.getWeather());
        return values;
    }
}
