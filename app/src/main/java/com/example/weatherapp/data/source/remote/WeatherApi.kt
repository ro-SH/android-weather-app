package com.example.weatherapp.data.source.remote

import com.example.weatherapp.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WeatherApi {
    private val retrofit by lazy {
        Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val service: WeatherApiService by lazy {
        retrofit.create(WeatherApiService::class.java)
    }
}
