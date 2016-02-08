package com.zooop.sunshine.data;

import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Created by stephenokennedy on 08/02/2016.
 */
public class WeatherContract {
    // To make it easy to query for the exact date, we normalize all dates that go into
    // the database to the start of the the Julian day at UTC.
    public static long normailiseDate(long startDtate){
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDtate);
        int juilianDay = Time.getJulianDay(startDtate,time.gmtoff);
        return time.setJulianDay(juilianDay);
    }

    public static final class LocationEntry implements BaseColumns{
        public static final String TABLE_NAME = "location";
    }

    /* Inner class that defines the table contents of the weather table */
    public static final class WeatherEntry implements BaseColumns{
        public static final String TABLE_NAME = "wather";
        public static final String COLUMN_LOC_KEY = "location_id";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_WEATHER_ID = "weather_id";
        public static final String COLUMN_SHORT_DESC = "short_desc";
        public static final String COLUMN_MIN_TEMP = "min";
        public static final String COLUMN_MAX_TEMP = "max";
        public static final String COLUMN_HUMIDITY = "humidity";
        public static final String COLUMN_PRESSURE = "pressure";
        public static final String COLUMN_WIND_SPEED = "wind";
        public static final String COLUMN_DEGREES = "degrees";
    }
}
