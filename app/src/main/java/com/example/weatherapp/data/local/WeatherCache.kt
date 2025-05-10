package com.example.weatherapp.data.local

import com.example.weatherapp.domain.model.CurrentWeather
import com.example.weatherapp.domain.model.DailyForecast
import android.content.Context
import android.content.SharedPreferences

class WeatherCache(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("WeatherCache", Context.MODE_PRIVATE)

    fun cacheCurrentWeather(weather: CurrentWeather) {
        with(prefs.edit()) {
            putFloat("temp", weather.temperature.toFloat())
            putString("condition", weather.condition)
            putFloat("humidity", weather.humidity.toFloat())
            putFloat("windSpeed", weather.windSpeed.toFloat())
            apply()
        }
    }

    fun cacheForecast(forecast: List<DailyForecast>) {
        with(prefs.edit()) {
            putInt("forecast_size", forecast.size)
            forecast.forEachIndexed { index, dailyForecast ->
                putString("date_$index", dailyForecast.date)
                putFloat("maxTemp_$index", dailyForecast.maxTemp.toFloat())
                putFloat("minTemp_$index", dailyForecast.minTemp.toFloat())
                putString("condition_$index", dailyForecast.condition)
            }
            apply()
        }
    }

    fun getCachedCurrentWeather(): CurrentWeather? {
        return if (prefs.contains("temp")) {
            CurrentWeather(
                temperature = prefs.getFloat("temp", 0f).toDouble(),
                condition = prefs.getString("condition", "") ?: "",
                humidity = prefs.getFloat("humidity", 0f).toDouble(),
                windSpeed = prefs.getFloat("windSpeed", 0f).toDouble()
            )
        } else {
            null
        }
    }

    fun getCachedForecast(): List<DailyForecast>? {
        val size = prefs.getInt("forecast_size", 0)
        if (size == 0) return null
        val forecast = mutableListOf<DailyForecast>()
        for (i in 0 until size) {
            forecast.add(
                DailyForecast(
                    date = prefs.getString("date_$i", "") ?: "",
                    maxTemp = prefs.getFloat("maxTemp_$i", 0f).toDouble(),
                    minTemp = prefs.getFloat("minTemp_$i", 0f).toDouble(),
                    condition = prefs.getString("condition_$i", "") ?: ""
                )
            )
        }
        return forecast
    }
}