package ru.lyubimov.weather.weatherapp.model

import android.content.Context
import android.location.Location

class RequestContainer {
    var cityName: String? = null
    var location: Location? = null
    var context: Context? = null
    fun getResources() = context?.resources
}