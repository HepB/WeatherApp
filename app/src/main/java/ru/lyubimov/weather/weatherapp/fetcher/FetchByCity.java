package ru.lyubimov.weather.weatherapp.fetcher;

import android.net.Uri;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.util.Locale;

import ru.lyubimov.weather.weatherapp.R;
import ru.lyubimov.weather.weatherapp.model.ForecastWeather;
import ru.lyubimov.weather.weatherapp.model.RequestContainer;

import static android.provider.ContactsContract.CommonDataKinds.StructuredPostal.CITY;

public class FetchByCity extends OpenWeatherMapFetcher {
    private static final String TAG = "FetchByCity";
    private static final String CITY = "q";

    @Override
    public ForecastWeather downloadWeather(RequestContainer container) {
        Locale locale = container.getResources().getConfiguration().locale;

        ForecastWeather weather = null;
        String city = container.getCityName();
        String country = locale.getCountry();
        Log.i(TAG, country);
        try {
            String jsonString = getUrlString(city, "metric", country);
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

    private String getUrlString(String city, String units, String locale) throws IOException {
        String completeUrl = buildUrlByCity(city, units, locale);
        return new String(getUrlBytes(completeUrl));
    }

    private String buildUrlByCity(String city, String units, String lang) {
        Uri.Builder builder = ENDPOINT.buildUpon();
        if (units != null) {
            builder.appendQueryParameter(UNITS, units);
        }
        if (lang != null) {
            builder.appendQueryParameter(LANG, lang);
        }
        builder.appendQueryParameter(CITY, city);
        builder.appendQueryParameter(API_KEY, "bebf456aa9257a2086ac7ea573cc9f77");
        return builder.build().toString();
    }
}
