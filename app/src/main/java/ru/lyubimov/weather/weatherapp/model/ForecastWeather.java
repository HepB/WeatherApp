package ru.lyubimov.weather.weatherapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ForecastWeather {

    @SerializedName("list")
    private ArrayList<Weather> weathers;
    @SerializedName("city")
    private City city;

    public City getCity() {
        return city;
    }
    public void setCity(City city) {
        this.city = city;
    }

    public ArrayList<Weather> getWeathers() {
        return weathers;
    }
    public void setWeathers(ArrayList<Weather> weathers) {
        this.weathers = weathers;
    }
}
