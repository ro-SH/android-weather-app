package com.example.weatherapp.data.model

data class DailyWeather(
    val dt: Int,
    val temp: Temperature,
    val pop: Float,
    val weather: List<Weather>
)
