package com.example.weatherapp.ui.current

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.domain.model.CurrentWeather
import com.example.weatherapp.domain.repository.WeatherRepository
import com.example.weatherapp.util.NetworkHelper

class CurrentWeatherViewModel(
    private val repository: WeatherRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {
    private val _weather = MutableLiveData<CurrentWeather?>()
    val weather: LiveData<CurrentWeather?> = _weather

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _location = MutableLiveData<Location?>()
    val location: LiveData<Location?> = _location

    fun refreshWeather(location: Location?) {
        if (location == null) {
            _error.value = "Location unavailable"
            return
        }
        _location.value = location

        if (networkHelper.isNetworkAvailable()) {
            Thread {
                val weather = repository.getCurrentWeather(location.latitude, location.longitude)
                if (weather != null) {
                    _weather.postValue(weather)
                    _error.postValue(null)
                } else {
                    _error.postValue("Failed to fetch weather data")
                }
            }.start()
        } else {
            val cachedWeather = repository.getCachedCurrentWeather()
            if (cachedWeather != null) {
                _weather.postValue(cachedWeather)
                _error.postValue("Offline: Showing cached data")
            } else {
                _error.postValue("Offline: No cached data available")
            }
        }
    }
}

class CurrentWeatherViewModelFactory(
    private val repository: WeatherRepository,
    private val networkHelper: NetworkHelper
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CurrentWeatherViewModel::class.java)) {
            return CurrentWeatherViewModel(repository, networkHelper) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}