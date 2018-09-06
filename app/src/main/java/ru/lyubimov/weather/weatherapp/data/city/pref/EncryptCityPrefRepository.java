package ru.lyubimov.weather.weatherapp.data.city.pref;

import android.content.SharedPreferences;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import io.reactivex.Single;
import io.reactivex.disposables.Disposable;

public class EncryptCityPrefRepository extends CityPrefRepository {

    public EncryptCityPrefRepository(SharedPreferences preferences) {
        super(preferences);
    }

    @Override
    public Single<Set<String>> getCities() {
        String encryptData = preferences.getString(CITIES, "");
        String jsonCities = new String(Base64.decode(encryptData, Base64.DEFAULT));
        Set<String> set = getCitiesFromJson(jsonCities);
        return Single.just(set);
    }

    @Override
    public void addCity(String city) {
        final Set<String> cities = new HashSet<>();
        Disposable disposable = getCities().subscribe(cities::addAll);
        cities.add(city);
        SharedPreferences.Editor editor = preferences.edit();
        Type type = new TypeToken<HashSet<String>>() {}.getType();
        String jsonCities = new Gson().toJson(cities, type);
        String encryptData = Base64.encodeToString(jsonCities.getBytes(), Base64.DEFAULT);
        editor.putString(CITIES, encryptData);
        editor.apply();
        disposable.dispose();
    }
}
