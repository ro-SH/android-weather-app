package com.example.weatherapp.data.source

import com.example.weatherapp.data.Result
import com.example.weatherapp.data.model.WeatherResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class DefaultWeatherRepository(
    private val weatherRemoteDataSource: WeatherDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : WeatherRepository {

    override suspend fun getWeather(lat: Double, lon: Double): Result<WeatherResponse> {
        return weatherRemoteDataSource.getWeather(lat, lon)
    }
}
