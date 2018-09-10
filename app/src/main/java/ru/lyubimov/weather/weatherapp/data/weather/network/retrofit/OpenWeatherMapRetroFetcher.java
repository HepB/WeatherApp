package ru.lyubimov.weather.weatherapp.data.weather.network.retrofit;

import android.content.res.Resources;
import android.util.Log;

import java.util.Locale;

import io.reactivex.Single;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.lyubimov.weather.weatherapp.R;
import ru.lyubimov.weather.weatherapp.data.weather.WeatherGetter;
import ru.lyubimov.weather.weatherapp.model.ForecastWeather;
import ru.lyubimov.weather.weatherapp.model.RequestContainer;

public class OpenWeatherMapRetroFetcher implements WeatherGetter {
    private static final String TAG = "OWMapRetroFetcher";

    @Override
    public Single<ForecastWeather> getWeatherResult(RequestContainer container) {

        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://api.openweathermap.org")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        OpenWeatherApi api = retrofit.create(OpenWeatherApi.class);
        Single<ForecastWeather> result;

        Resources resources = container.getResources();
        Locale locale = container.getResources().getConfiguration().locale;
        String country = locale.getCountry();

        if (container.getLocation() != null) {
            double lat = container.getLocation().getLatitude();
            double lon = container.getLocation().getLongitude();
            result = api.fetchWeatherByGeo(lat, lon, resources.getString(R.string.metric), country, resources.getString(R.string.weather_key)).singleOrError();
        } else if (container.getCityName() != null) {
            result = api.fetchWeatherByCity(container.getCityName(), resources.getString(R.string.metric), country, resources.getString(R.string.weather_key)).singleOrError();
        } else {
            Log.d(TAG, "Something wrong");
            result = api.fetchWeatherByCity("Moscow,RU", resources.getString(R.string.metric), country, resources.getString(R.string.weather_key)).singleOrError();
        }
        return result;
    }
}
