package com.example.weatherapp.data.model

data class HourlyWeather(
    val dt: Int,
    val temp: Float,
    val pop: Float,
    val weather: List<Weather>
)
