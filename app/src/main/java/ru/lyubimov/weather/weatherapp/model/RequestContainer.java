package ru.lyubimov.weather.weatherapp.model;

import android.content.res.Resources;
import android.location.Location;

/**
 * Created by Alex on 23.02.2018.
 */

public class RequestContainer {
    private Location mLocation;
    private Resources mResources;

    public Location getLocation() {
        return mLocation;
    }
    public void setLocation(Location location) {
        mLocation = location;
    }
    public Resources getResources() {
        return mResources;
    }
    public void setResources(Resources resources) {
        this.mResources = resources;
    }
}
