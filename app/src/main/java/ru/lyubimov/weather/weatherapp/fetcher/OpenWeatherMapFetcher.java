package ru.lyubimov.weather.weatherapp.fetcher;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import ru.lyubimov.weather.weatherapp.model.ForecastWeather;
import ru.lyubimov.weather.weatherapp.model.RequestContainer;

/**
 * Для получения данных о погоде будем использовать открытый api openweathermap.org. Бесплатная
 * версия подразумевает функционал получения погоды на 5 дней, требуется регистрация и ключ.
 */

abstract class OpenWeatherMapFetcher implements WeatherFetcher{
    private static final String TAG = "OpenWeatherMapFetcher";

    static final Uri ENDPOINT = Uri.parse("http://api.openweathermap.org/data/2.5/forecast");
    static final String UNITS = "units";
    static final String LANG = "lang";
    static final String API_KEY = "appid";

    /**
     * @param container контейнер для запроса, содержащий локацию и локаль
     * @return погода с openweathermap.org
     */
    public abstract ForecastWeather downloadWeather(RequestContainer container);

    /**
     * Здесь и далее приватность выбрана default, т к. подразумевается, что все реализации фетчера будут
     * лежать в пакете fetcher, т. к. package более закрытый, чем protected.
     * @param urlSpec строка для запроса
     * @return массив байт, содержащий данные из сети
     * @throws IOException пробрасывается исключение в случае отсутствия подключения
     */
    byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
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
        } finally {
            connection.disconnect();
        }
    }

    /**
     * Получение POJO из json
     *
     * @param jsonString json в виде строки
     * @return POJO ForecastWeather
     * @throws JSONException в случае если возникает exception при парсинге
     */
    ForecastWeather parseItem(String jsonString) throws JSONException {
        try {
            Gson gson = new Gson();
            return gson.fromJson(jsonString, ForecastWeather.class);
        } catch (Exception ex) {
            throw new JSONException(ex.getLocalizedMessage());
        }
    }
}
