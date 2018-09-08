package ru.lyubimov.weather.weatherapp.data.weather.network.base;

import ru.lyubimov.weather.weatherapp.model.ForecastWeather;
import ru.lyubimov.weather.weatherapp.model.RequestContainer;

public interface WeatherFetcher {
    ForecastWeather downloadWeather(RequestContainer container);
}
