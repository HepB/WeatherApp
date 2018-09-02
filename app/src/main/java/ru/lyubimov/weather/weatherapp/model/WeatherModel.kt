package ru.lyubimov.weather.weatherapp.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.ArrayList

class ForecastWeather {
    @Expose @SerializedName("list") var weathers: ArrayList<Weather>? = null
    @Expose @SerializedName("city") var city: City? = null
}

class Weather {
    @Expose @SerializedName("dt") var dateStamp: Long = 0
    @Expose @SerializedName("main") var temperature: Temperature? = null
    @Expose @SerializedName("weather") var conditions: List<Condition>? = null
    @Expose @SerializedName("clouds") var clouds: Clouds? = null
    @Expose @SerializedName("wind") var wind: Wind? = null
}

class Temperature {
    @Expose @SerializedName("temp") var temp: Float = 0f
    @Expose @SerializedName("temp_min") var tempMin: Float = 0f
    @Expose @SerializedName("temp_max") var tempMax: Float = 0f
}

class Condition {
    @Expose @SerializedName("description") var description: String? = null
    @Expose @SerializedName("icon") var iconName: String? = null
}

class Wind {
    @Expose @SerializedName("speed") var speed: Float = 0f
    @Expose @SerializedName("deg") var deg: Float = 0f
}

class Clouds {
    @Expose @SerializedName("all") var cloudPercent: Int = 0
}

class City {
    @Expose @SerializedName("name") var cityName: String? = null
    @Expose @SerializedName("coord") var coordinate: Coordinate? = null
}

class Coordinate {
    @Expose @SerializedName("lat") var latitude: Float = 0f
    @Expose @SerializedName("lon") var longitude: Float = 0f
}