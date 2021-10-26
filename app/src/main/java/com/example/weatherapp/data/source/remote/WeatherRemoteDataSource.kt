package com.example.weatherapp.data.source.remote

import com.example.weatherapp.data.Result
import com.example.weatherapp.data.Result.Error
import com.example.weatherapp.data.Result.Success
import com.example.weatherapp.data.model.WeatherResponse
import com.example.weatherapp.data.source.WeatherDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherRemoteDataSource(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : WeatherDataSource {

    override suspend fun getWeather(lat: Double, lon: Double): Result<WeatherResponse> =
        withContext(dispatcher) {
            return@withContext try {
                val response = WeatherApi.service.getWeather(lat, lon)
                Success(response)
            } catch (ex: Exception) {
                Error(ex)
            }
        }
}