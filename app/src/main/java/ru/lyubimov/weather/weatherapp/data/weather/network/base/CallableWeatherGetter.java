package ru.lyubimov.weather.weatherapp.data.weather.network.base;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.lyubimov.weather.weatherapp.data.weather.WeatherGetter;
import ru.lyubimov.weather.weatherapp.model.ForecastWeather;
import ru.lyubimov.weather.weatherapp.model.RequestContainer;

public class CallableWeatherGetter implements WeatherGetter{

    private WeatherFetcher mWeatherFetcher;

    public CallableWeatherGetter(WeatherFetcher fetcher) {
        this.mWeatherFetcher = fetcher;
    }

    @Override
    public Single<ForecastWeather> getWeatherResult(final RequestContainer container) {
        return Single.fromCallable(() -> mWeatherFetcher.downloadWeather(container)).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
