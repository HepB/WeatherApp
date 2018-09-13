@file:JvmName("MessageUtils")
package ru.lyubimov.weather.weatherapp.utils

import ru.lyubimov.weather.weatherapp.model.ForecastWeather

fun createMessageAboutWeather(weather: ForecastWeather): String {
    //Тут бы, конечно, доставать из ресурсов, чтобы не хардкодить и была поддержка локализации,
    // но т. к. проект учебный, сделаем быстро и по-плохому.
    return "Погода в ${weather.city?.cityName}: ${weather.weathers?.get(0)?.temperature?.temp} градусов цельсия."
}

