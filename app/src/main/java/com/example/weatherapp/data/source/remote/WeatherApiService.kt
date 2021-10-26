package com.example.weatherapp.data.source.remote

import com.example.weatherapp.API_KEY
import com.example.weatherapp.BASE_EXCLUDE
import com.example.weatherapp.BASE_UNITS
import com.example.weatherapp.data.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    @GET("onecall")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = BASE_UNITS,
        @Query("exclude") exclude: String = BASE_EXCLUDE,
        @Query("appid") apiKey: String = API_KEY
    ): WeatherResponse
}
