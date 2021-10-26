package com.example.weatherapp.data.source

import com.example.weatherapp.data.source.remote.WeatherRemoteDataSource

object RepositoryProvider {

    private var weatherRepository: WeatherRepository? = null

    fun provideWeatherRepository(): WeatherRepository {
        synchronized(this) {
            return weatherRepository ?: weatherRepository ?: createWeatherRepository()
        }
    }

    private fun createWeatherRepository(): WeatherRepository {

        val repo = DefaultWeatherRepository(createWeatherRemoteDataSource())
        weatherRepository = repo
        return repo
    }

    private fun createWeatherRemoteDataSource(): WeatherDataSource {
        return WeatherRemoteDataSource()
    }
}
