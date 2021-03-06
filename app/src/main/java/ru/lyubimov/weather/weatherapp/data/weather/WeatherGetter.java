package ru.lyubimov.weather.weatherapp.data.weather;

import io.reactivex.Single;
import ru.lyubimov.weather.weatherapp.model.ForecastWeather;
import ru.lyubimov.weather.weatherapp.model.RequestContainer;

public interface WeatherGetter {
    Single<ForecastWeather> getWeatherResult(RequestContainer container);
}
