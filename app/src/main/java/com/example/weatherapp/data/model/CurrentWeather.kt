package com.example.weatherapp.data.model

data class CurrentWeather(
    val dt: Int,
    val sunrise: Int,
    val sunset: Int,
    val temp: Float, // Def: Celsius
    val feels_like: Float, // Def: Celsius
    val pressure: Int, // hPa
    val humidity: Int, // %
    val visibility: Int, // Metres
    val wind_speed: Float, // Def: metres/sec
    val wind_deg: Int, // Degrees
    val weather: List<Weather>
)
