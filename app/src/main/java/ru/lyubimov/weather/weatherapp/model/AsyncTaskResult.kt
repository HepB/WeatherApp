package ru.lyubimov.weather.weatherapp.model

class AsyncTaskResult<T> private constructor() {
    var result: T? = null
    var error: Exception? = null

    constructor(result: T) : this() {
        this.result = result
    }
    constructor(error: Exception) : this() {
        this.error = error
    }
}