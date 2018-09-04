package ru.lyubimov.weather.weatherapp.data.city;

import java.util.Set;

import io.reactivex.Single;

public interface CityRepository {
    String CITIES = "cities";

    Single<Set<String>> getCities();
    void addCity(String city);
}
