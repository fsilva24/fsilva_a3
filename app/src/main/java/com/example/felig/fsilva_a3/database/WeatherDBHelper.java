package com.example.felig.fsilva_a3.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by felig on 4/10/2018.
 */

/*
    Connects app to phone database
 */
public class WeatherDBHelper extends SQLiteOpenHelper{
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "mapBase.db";

    public WeatherDBHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + WeatherDatabase.WeatherTable.NAME + "(" + "_id integer primary key autoincrement, "
                + WeatherDatabase.WeatherTable.Cols.UUID + ", " + WeatherDatabase.WeatherTable.Cols.LAT +
                ", " + WeatherDatabase.WeatherTable.Cols.LONG + ", "
                + WeatherDatabase.WeatherTable.Cols.DATETIME + ", " + WeatherDatabase.WeatherTable.Cols.TEMPERATURE +
                ", " + WeatherDatabase.WeatherTable.Cols.WEATHER + ")"
        );
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
