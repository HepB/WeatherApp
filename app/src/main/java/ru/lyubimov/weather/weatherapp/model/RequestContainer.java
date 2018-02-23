package ru.lyubimov.weather.weatherapp.model;

import android.location.Location;

import java.util.Locale;

/**
 * Created by Alex on 23.02.2018.
 */

public class RequestContainer {
    private Location mLocation;
    private Locale locale;

    public Location getLocation() {
        return mLocation;
    }
    public void setLocation(Location location) {
        mLocation = location;
    }
    public Locale getLocale() {
        return locale;
    }
    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}
