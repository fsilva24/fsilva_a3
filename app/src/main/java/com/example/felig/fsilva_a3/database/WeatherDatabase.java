package com.example.felig.fsilva_a3.database;

/**
 * Created by felig on 4/10/2018.
 */

/*
    Creates the table necessary for storing the created markers and weather
 */
public class WeatherDatabase {
    public static final class WeatherTable {
        public static final String NAME = "markers";
        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String LAT = "lat";
            public static final String LONG = "lon";
            public static final String DATETIME = "datetime";
            public static final String TEMPERATURE = "temperature";
            public static final String WEATHER = "weather";
        }
    }
}
