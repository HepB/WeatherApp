package ru.lyubimov.weather.weatherapp.data.city.pref;

import android.content.SharedPreferences;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

public class EncryptCityPrefRepository extends CityPrefRepository {

    public EncryptCityPrefRepository(SharedPreferences preferences) {
        super(preferences);
    }

    @Override
    public Set<String> getCities() {
        String encryptData = preferences.getString(CITIES, "");
        String jsonCities = new String(Base64.decode(encryptData, Base64.DEFAULT));
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
        String encryptData = Base64.encodeToString(jsonCities.getBytes(), Base64.DEFAULT);
        editor.putString(CITIES, encryptData);
        editor.apply();
    }
}
