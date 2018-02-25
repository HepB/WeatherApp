package ru.lyubimov.weather.weatherapp;

import android.location.Location;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

import ru.lyubimov.weather.weatherapp.model.ForecastWeather;
import ru.lyubimov.weather.weatherapp.model.RequestContainer;

/**
 * Для получения данных о погоде будем использовать открытый api openweathermap.org. Бесплатная
 * версия подразумевает функционал получения погоды на 5 дней, требуется регистрация и ключ.
 */

class OpenWeatherMapFetcher {
    private static final String TAG = "CoinMarketCapFetcher";

    private static final Uri ENDPOINT = Uri.parse("http://api.openweathermap.org/data/2.5/forecast");
    private static final String UNITS = "units";
    private static final String LANG = "lang";
    private static final String LATITUDE = "lat";
    private static final String LONGITUDE = "lon";
    private static final String API_KEY = "appid";

    /**
     * @param container контейнер для запроса, содержащий локацию и локаль
     * @return погода с openweathermap.org
     */
    ForecastWeather downloadWeather(RequestContainer container) {
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

    /**
     * Построение строки запроса для получения общей строки запроса
     * @param lat широта
     * @param lon долгота
     * @param units единицы измерения
     * @param lang язык
     * @return возвращаемое значение - строка для подключения и получения json
     */
    private String buildUrl(double lat, double lon, String units, String lang) {
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

    private String getUrlString(double lat, double lon, String units, String locale) throws IOException {
        String completeUrl = buildUrl(lat, lon, units, locale);
        return new String(getUrlBytes(completeUrl));
    }

    /**
     * @param urlSpec строка для запроса
     * @return массив байт, содержащий данные из сети
     * @throws IOException пробрасывается исключение в случае отсутствия подключения
     */
    private byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                IOException ioe = new IOException(connection.getResponseMessage() + ": with " + urlSpec);
                Log.e(TAG, ioe.getMessage());
                throw ioe;
            }
            int bytesRead;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        }
        finally {
            connection.disconnect();
        }
    }

    /**
     * Получение POJO из json
     * @param jsonString json в виде строки
     * @return POJO ForecastWeather
     * @throws JSONException в случае если возникает exception при парсинге
     */
    private ForecastWeather parseItem(String jsonString) throws JSONException{
        try {
            Gson gson = new Gson();
            return gson.fromJson(jsonString, ForecastWeather.class);
        } catch (Exception ex) {
            throw new JSONException(ex.getLocalizedMessage());
        }
    }
}
