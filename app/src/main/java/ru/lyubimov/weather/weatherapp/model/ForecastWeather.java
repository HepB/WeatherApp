package ru.lyubimov.weather.weatherapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ForecastWeather {

    @SerializedName("list")
    private List<Weather> weathers;
    @SerializedName("city")
    private City city;

    public City getCity() {
        return city;
    }
    public void setCity(City city) {
        this.city = city;
    }

    public List<Weather> getWeathers() {
        return weathers;
    }
    public void setWeathers(List<Weather> weathers) {
        this.weathers = weathers;
    }
}
