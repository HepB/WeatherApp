package ru.lyubimov.weather.weatherapp.data.city.pref;

import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import ru.lyubimov.weather.weatherapp.data.city.CityRepository;

public class CityPrefRepository implements CityRepository {

    SharedPreferences preferences;
    public CityPrefRepository(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    @Override
    public Set<String> getCities() {
        String jsonCities = preferences.getString(CITIES, "");
        Type type = new TypeToken<HashSet<String>>() {}.getType();
        Set<String> result = new Gson().fromJson(jsonCities, type);
        return result == null ? new HashSet<String>(): result;
    }

    @Override
    public void addCity(String city) {
        Set<String> cities = getCities();
        cities.add(city);
        SharedPreferences.Editor editor = preferences.edit();
        Type type = new TypeToken<HashSet<String>>() {}.getType();
        String jsonCities = new Gson().toJson(cities, type);
        editor.putString(CITIES, jsonCities);
        editor.apply();
    }
}
