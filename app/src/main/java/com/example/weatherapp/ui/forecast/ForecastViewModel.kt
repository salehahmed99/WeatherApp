package com.example.weatherapp.ui.forecast

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.domain.model.DailyForecast
import com.example.weatherapp.domain.repository.WeatherRepository
import com.example.weatherapp.util.NetworkHelper

class ForecastViewModel(
    private val repository: WeatherRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {
    private val _forecast = MutableLiveData<List<DailyForecast>?>()
    val forecast: LiveData<List<DailyForecast>?> = _forecast

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadForecast(latitude: Double, longitude: Double) {
        if (latitude == 0.0 && longitude == 0.0) {
            _error.value = "Invalid location"
            _forecast.value = null
            return
        }

        if (networkHelper.isNetworkAvailable()) {
            Thread {
                try {
                    val forecast = repository.getForecast(latitude, longitude)
                    if (forecast != null) {
                        if (forecast.isNotEmpty()) {
                            _forecast.postValue(forecast)
                            _error.postValue(null)
                        } else {
                            _error.postValue("No forecast data available")
                            _forecast.postValue(null)
                        }
                    }
                } catch (e: Exception) {
                    _error.postValue("Error fetching forecast: ${e.message}")
                    _forecast.postValue(null)
                }
            }.start()
        } else {
            val cachedForecast = repository.getCachedForecast()
            if (cachedForecast != null) {
                if (cachedForecast.isNotEmpty()) {
                    _forecast.postValue(cachedForecast)
                    _error.postValue("Offline: Showing cached data")
                } else {
                    _error.postValue("Offline: No cached data available")
                    _forecast.postValue(null)
                }
            }
        }
    }
}

class ForecastViewModelFactory(
    private val repository: WeatherRepository,
    private val networkHelper: NetworkHelper
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ForecastViewModel::class.java)) {
            return ForecastViewModel(repository, networkHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}