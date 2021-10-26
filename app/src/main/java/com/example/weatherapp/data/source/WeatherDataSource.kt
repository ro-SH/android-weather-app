package com.example.weatherapp.data.source

import com.example.weatherapp.data.Result
import com.example.weatherapp.data.model.WeatherResponse

interface WeatherDataSource {

    suspend fun getWeather(lat: Double, lon: Double): Result<WeatherResponse>
}
