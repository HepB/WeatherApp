package ru.lyubimov.weather.weatherapp.data.weather.network.retrofit;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.lyubimov.weather.weatherapp.model.ForecastWeather;

public interface OpenWeatherApi {

    @GET("data/2.5/forecast")
    Observable<ForecastWeather> fetchWeatherByCity(@Query("q") String city, @Query("units") String units, @Query("lang") String locale, @Query("appid") String apiKey);

    @GET("data/2.5/forecast")
    Observable<ForecastWeather> fetchWeatherByGeo(@Query("lat") double lat, @Query("lon") double lon, @Query("units") String units, @Query("lang")String locale, @Query("appid") String apiKey);
}
