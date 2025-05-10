package com.example.weatherapp.data.repository

import com.example.weatherapp.data.local.WeatherCache
import com.example.weatherapp.data.api.WeatherApi
import com.example.weatherapp.domain.model.CurrentWeather
import com.example.weatherapp.domain.model.DailyForecast
import com.example.weatherapp.domain.repository.WeatherRepository

class WeatherRepositoryImpl(
    private val weatherApi: WeatherApi,
    private val cache: WeatherCache
) : WeatherRepository {
    override fun getCurrentWeather(latitude: Double, longitude: Double): CurrentWeather? {
        val response = weatherApi.getCurrentWeather(latitude, longitude)
        return response?.let {
            val weather = CurrentWeather(
                temperature = it.currentConditions.temp,
                condition = it.currentConditions.conditions,
                humidity = it.currentConditions.humidity,
                windSpeed = it.currentConditions.windspeed
            )
            cacheCurrentWeather(weather)
            weather
        }
    }

    override fun getForecast(latitude: Double, longitude: Double): List<DailyForecast>? {
        val response = weatherApi.getForecast(latitude, longitude)
        return response?.let {
            val forecast = it.days.map { day ->
                DailyForecast(
                    date = day.datetime,
                    maxTemp = day.tempmax,
                    minTemp = day.tempmin,
                    condition = day.conditions
                )
            }
            cacheForecast(forecast)
            forecast
        }
    }

    override fun cacheCurrentWeather(weather: CurrentWeather) {
        cache.cacheCurrentWeather(weather)
    }

    override fun cacheForecast(forecast: List<DailyForecast>) {
        cache.cacheForecast(forecast)
    }

    override fun getCachedCurrentWeather(): CurrentWeather? {
        return cache.getCachedCurrentWeather()
    }

    override fun getCachedForecast(): List<DailyForecast>? {
        return cache.getCachedForecast()
    }
}