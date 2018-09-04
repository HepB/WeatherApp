package ru.lyubimov.weather.weatherapp.data.city;

import java.util.Set;

public interface CityRepository {
    String CITIES = "cities";

    Set<String> getCities();
    void addCity(String city);
}
