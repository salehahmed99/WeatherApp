package com.example.weatherapp.domain.model

data class CurrentWeather(
    val temperature: Double,
    val condition: String,
    val humidity: Double,
    val windSpeed: Double
)
