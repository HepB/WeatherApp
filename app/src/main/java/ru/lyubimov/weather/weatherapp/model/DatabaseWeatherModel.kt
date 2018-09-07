package ru.lyubimov.weather.weatherapp.model

data class DatabaseWeatherModel(
        var city: String? = null,
        var lat: Float? = 0f,
        var lon: Float? = 0f,
        //т. к. первоначально приложение не проектировалось для использования с базой данных и первоначальная
        //архитектура - жесткая, без следования принципам SOLID, будем костылить: List<Weather> сохраним как
        //Json строку и при сохранении и чтении из базы будем конвертировать туда-обратно.
        var weathers: String? = null
) {
    var id: Int = 0
}
