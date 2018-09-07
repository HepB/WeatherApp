package ru.lyubimov.weather.weatherapp.model

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DbRequestModelConverter {

    fun requestToDbModelConvert(requestModel: ForecastWeather): DatabaseWeatherModel {
        val type = object: TypeToken<List<Weather>>(){}.type
        val jsonWeathers: String = Gson().toJson(requestModel.weathers, type)
        return DatabaseWeatherModel(
                city = requestModel.city?.cityName,
                lat = requestModel.city?.coordinate?.latitude,
                lon = requestModel.city?.coordinate?.longitude,
                weathers = jsonWeathers
        )
    }

    fun dbToRequestModelConvert(databaseWeatherModel: DatabaseWeatherModel): ForecastWeather {
        val type = object:  TypeToken<List<Weather>>(){}.type
        val weathers: List<Weather>? = Gson().fromJson(databaseWeatherModel.weathers, type)
        val forecastWeather = ForecastWeather()
        val city = City()
        val coordinate = Coordinate()

        coordinate.latitude = databaseWeatherModel.lat
        coordinate.longitude = databaseWeatherModel.lon

        city.cityName = databaseWeatherModel.city
        city.coordinate = coordinate

        forecastWeather.city = city
        forecastWeather.weathers = weathers
        return forecastWeather
    }
}