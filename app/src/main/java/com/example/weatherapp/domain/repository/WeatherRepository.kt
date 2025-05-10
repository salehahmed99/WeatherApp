package com.example.weatherapp.domain.repository

import com.example.weatherapp.domain.model.CurrentWeather
import com.example.weatherapp.domain.model.DailyForecast

interface WeatherRepository {
    fun getCurrentWeather(latitude: Double, longitude: Double): CurrentWeather?
    fun getForecast(latitude: Double, longitude: Double): List<DailyForecast>?
    fun cacheCurrentWeather(weather: CurrentWeather)
    fun cacheForecast(forecast: List<DailyForecast>)
    fun getCachedCurrentWeather(): CurrentWeather?
    fun getCachedForecast(): List<DailyForecast>?
}