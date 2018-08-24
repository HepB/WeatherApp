package ru.lyubimov.weather.weatherapp.fetcher;

import android.location.Location;
import android.net.Uri;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.util.Locale;

import ru.lyubimov.weather.weatherapp.R;
import ru.lyubimov.weather.weatherapp.model.ForecastWeather;
import ru.lyubimov.weather.weatherapp.model.RequestContainer;

public class FetcherByGeo extends OpenWeatherMapFetcher {
    private static final String TAG = "FetcherByGeo";
    private static final String LATITUDE = "lat";
    private static final String LONGITUDE = "lon";

    public ForecastWeather downloadWeather(RequestContainer container) {
        Location location = container.getLocation();
        Locale locale = container.getResources().getConfiguration().locale;

        ForecastWeather weather = null;
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        String country = locale.getCountry();
        Log.i(TAG, country);
        try {
            String jsonString = getUrlString(lat, lon, "metric", country);
            weather = parseItem(jsonString);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to connect", ioe);
            throw new RuntimeException(container.getResources().getString(R.string.no_internet_exception));
        } catch (JSONException jex) {
            Log.e(TAG, "Failed to fetch weather", jex);
            throw new RuntimeException();
        }
        return weather;
    }

    private String getUrlString(double lat, double lon, String units, String locale) throws IOException {
        String completeUrl = buildUrlByGeo(lat, lon, units, locale);
        return new String(getUrlBytes(completeUrl));
    }

    /**
     * Построение строки запроса для получения общей строки запроса
     * @param lat широта
     * @param lon долгота
     * @param units единицы измерения
     * @param lang язык
     * @return возвращаемое значение - строка для подключения и получения json
     */
    private String buildUrlByGeo(double lat, double lon, String units, String lang) {
        Uri.Builder builder = ENDPOINT.buildUpon();
        if (units != null) {
            builder.appendQueryParameter(UNITS, units);
        }
        if (lang != null) {
            builder.appendQueryParameter(LANG, lang);
        }
        builder.
                appendQueryParameter(LATITUDE, Double.toString(lat)).
                appendQueryParameter(LONGITUDE, Double.toString(lon)).
                //ключ правильнo было бы хранить в ресурсах, или хотя бы в константах
                //но т. к. исходники будут выкладываться в публичный репозиторий, добавим его хардкодом
                        appendQueryParameter(API_KEY, "bebf456aa9257a2086ac7ea573cc9f77");
        return builder.build().toString();
    }
}
