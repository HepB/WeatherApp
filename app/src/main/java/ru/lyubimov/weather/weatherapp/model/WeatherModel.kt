package ru.lyubimov.weather.weatherapp.model

import com.google.gson.annotations.SerializedName
import java.util.ArrayList

class ForecastWeather {
    @SerializedName("list") var weathers: ArrayList<Weather>? = null
    @SerializedName("city") var city: City? = null
}

class Weather {
    @SerializedName("dt") var dateStamp: Long = 0
    @SerializedName("main") var temperature: Temperature? = null
    @SerializedName("weather") var conditions: List<Condition>? = null
    @SerializedName("clouds") var clouds: Clouds? = null
    @SerializedName("wind") var wind: Wind? = null
}

class Temperature {
    @SerializedName("temp") var temp: Float = 0f
    @SerializedName("temp_min") var tempMin: Float = 0f
    @SerializedName("temp_max") var tempMax: Float = 0f
}

class Condition {
    @SerializedName("description") var description: String? = null
    @SerializedName("icon") var iconName: String? = null
}

class Wind {
    @SerializedName("speed") var speed: Float = 0f
    @SerializedName("deg") var deg: Float = 0f
}

class Clouds {
    @SerializedName("all") var cloudPercent: Int = 0
}

class City {
    @SerializedName("name") var cityName: String? = null
    @SerializedName("county") var country: String? = null
    @SerializedName("coord") var coordinate: Coordinate? = null
}

class Coordinate {
    @SerializedName("lat") var latitude: Float = 0f
    @SerializedName("lon") var longitude: Float = 0f
}