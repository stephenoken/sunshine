package com.zooop.sunshine.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.zooop.sunshine.data.WeatherContract.WeatherEntry;
import com.zooop.sunshine.data.WeatherContract.LocationEntry;

/**
 * Created by stephenokennedy on 08/02/2016.
 */
public class WeatherDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "weather.db";

    public WeatherDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_WEATHER_TABLE = "CREATE TABLE " + WeatherEntry.TABLE_NAME +
                " ( " + WeatherContract.WeatherEntry._ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                WeatherEntry.COLUMN_LOC_KEY + " INTEGER NOT NULL, " +
                WeatherEntry.COLUMN_DATE + " INTEGER NOT NULL, " +
                WeatherEntry.COLUMN_SHORT_DESC + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_WEATHER_ID + " INTEGER NOT NULL," +

                WeatherEntry.COLUMN_MIN_TEMP + " REAL NOT NULL, " +
                WeatherEntry.COLUMN_MAX_TEMP + " REAL NOT NULL, " +

                WeatherEntry.COLUMN_HUMIDITY + " REAL NOT NULL, " +
                WeatherEntry.COLUMN_PRESSURE + " REAL NOT NULL, " +
                WeatherEntry.COLUMN_WIND_SPEED + " REAL NOT NULL, " +
                WeatherEntry.COLUMN_DEGREES + " REAL NOT NULL, " +
                " FOREIGN KEY (" + WeatherEntry.COLUMN_LOC_KEY + ") REFERENCES " +
                LocationEntry.TABLE_NAME + " (" + LocationEntry._ID + "), " +

                // To assure the application have just one weather entry per day
                // per location, it's created a UNIQUE constraint with REPLACE strategy
                " UNIQUE (" + WeatherEntry.COLUMN_DATE + ", " +
                WeatherEntry.COLUMN_LOC_KEY + ") ON CONFLICT REPLACE);";

//        final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE " + LocationEntry.TABLE_NAME + " ( " +
//                LocationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
//
        db.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + LocationEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + WeatherEntry.TABLE_NAME);
        onCreate(db);
    }
}
