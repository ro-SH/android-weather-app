package com.example.weatherapp.weather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.Result
import com.example.weatherapp.data.model.Coordinates
import com.example.weatherapp.data.model.WeatherResponse
import com.example.weatherapp.data.source.WeatherRepository
import com.example.weatherapp.data.succeeded
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _weatherResponse = MutableLiveData<WeatherResponse>()
    val weatherResponse: LiveData<WeatherResponse>
        get() = _weatherResponse

    private val _cityName = MutableLiveData<String>()
    val cityName: LiveData<String>
        get() = _cityName

    private val _coordinates = MutableLiveData<Coordinates>(Coordinates(0.0, 0.0))
    val coordinates: LiveData<Coordinates>
        get() = _coordinates

    private val _dataLoading = MutableLiveData<Boolean>(true)
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _requestSucceeded = MutableLiveData<Boolean>(true)
    val requestSucceeded: LiveData<Boolean> = _requestSucceeded

    fun setCoordinates(lat: Double, lon: Double, city: String = "") {
        _coordinates.value = Coordinates(lat, lon)
        if (city.isNotEmpty()) _cityName.value = city
        getWeather()
    }

    fun getWeather() {
        _dataLoading.value = true
        viewModelScope.launch {
            _coordinates.value?.let {
                val result = repository.getWeather(it.lat, it.lon)
                _requestSucceeded.value = if (result.succeeded) {
                    _weatherResponse.value = (result as Result.Success).data!!
                    true
                } else false
            }

            _dataLoading.value = false
        }
    }
}
