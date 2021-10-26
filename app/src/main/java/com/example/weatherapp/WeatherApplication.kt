package com.example.weatherapp

import android.app.Application
import com.example.weatherapp.data.source.RepositoryProvider
import com.example.weatherapp.data.source.WeatherRepository

class WeatherApplication : Application() {

    // Weather Repository
    val weatherRepository: WeatherRepository
        get() = RepositoryProvider.provideWeatherRepository()
}
