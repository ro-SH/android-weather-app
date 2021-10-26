package com.example.weatherapp.data.source

import com.example.weatherapp.data.Result
import com.example.weatherapp.data.model.WeatherResponse

interface WeatherRepository {

    suspend fun getWeather(lat: Double, lon: Double): Result<WeatherResponse>
}
