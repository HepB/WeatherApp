package ru.lyubimov.weather.weatherapp.model

data class DatabaseWeatherModel(
        var city: String? = null,
        var lat: Double? = 0.0,
        var lon: Double? = 0.0,
        //т. к. первоначально приложение не проектировалось для использования с базой данных и первоначальная
        //архитектура - жесткая, без следования принципам SOLID, будем костылить: List<Weather> сохраним как
        //Json строку и при сохранении и чтении из базы будем конвертировать туда-обратно.
        var weathers: String? = null
) {
    var id: Int = 0
}
