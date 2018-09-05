package ru.lyubimov.weather.weatherapp.data.city.pref;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import ru.lyubimov.weather.weatherapp.data.city.CityRepository;

public class CityPrefRepository implements CityRepository {

    SharedPreferences preferences;

    public CityPrefRepository(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    @Override
    public Single<Set<String>> getCities() {
        String jsonCities = preferences.getString(CITIES, "");
        Type type = new TypeToken<HashSet<String>>() {
        }.getType();
        Set<String> set = new Gson().fromJson(jsonCities, type);
        set = set == null ? new HashSet<String>() : set;
        return Single.just(set);
    }

    @Override
    public void addCity(String city) {
        final Set<String> cities = new HashSet<>();
        Disposable disposable = getCities().subscribe(new Consumer<Set<String>>() {
            @Override
            public void accept(Set<String> strings) throws Exception {
                cities.addAll(strings);
            }
        });
        cities.add(city);
        SharedPreferences.Editor editor = preferences.edit();
        Type type = new TypeToken<HashSet<String>>() {}.getType();
        String jsonCities = new Gson().toJson(cities, type);
        editor.putString(CITIES, jsonCities);
        editor.apply();
        disposable.dispose();
    }
}
