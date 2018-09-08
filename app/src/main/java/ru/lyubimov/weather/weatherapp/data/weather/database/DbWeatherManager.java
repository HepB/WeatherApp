package ru.lyubimov.weather.weatherapp.data.weather.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Date;

import ru.lyubimov.weather.weatherapp.model.DatabaseWeatherModel;

public class DbWeatherManager {
    private static final String TAG = "DbWeatherManager";

    private final DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    public DbWeatherManager(Context context) {
        databaseHelper = new DatabaseHelper(context);

    }

    public void open() {
        Log.i(TAG, "Open DB");
        database = databaseHelper.getWritableDatabase();
    }
    public void close() {
        Log.i(TAG, "Close DB");
        database.close();
    }

    public void addWeather(DatabaseWeatherModel databaseWeatherModel) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_CITY, databaseWeatherModel.getCity());
        values.put(DatabaseHelper.COLUMN_LATITUDE, databaseWeatherModel.getLat());
        values.put(DatabaseHelper.COLUMN_LONGITUDE, databaseWeatherModel.getLon());
        values.put(DatabaseHelper.COLUMN_WEATHERS, databaseWeatherModel.getWeathers());
        values.put(DatabaseHelper.COLUMN_TIMESTAMP, new Date().getTime());
        database.replace(DatabaseHelper.TABLE_WEATHERS, null, values);
    }

    public DatabaseWeatherModel getLastWeather() {
        try (Cursor cursor = database.query(DatabaseHelper.TABLE_WEATHERS,
                null,
                null,
                null,
                null,
                null,
                DatabaseHelper.COLUMN_TIMESTAMP + " DESC")) {
            cursor.moveToFirst();
            return cursorToWeather(cursor);
        }
    }

    private DatabaseWeatherModel cursorToWeather(Cursor cursor) {
        DatabaseWeatherModel result = new DatabaseWeatherModel();
        result.setCity(cursor.getString(0));
        result.setLat(cursor.getDouble(1));
        result.setLon(cursor.getDouble(2));
        result.setWeathers(cursor.getString(3));
        return result;
    }
}
