package ru.lyubimov.weather.weatherapp;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class WeatherActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return WeatherFragment.newInstance();
    }

}
