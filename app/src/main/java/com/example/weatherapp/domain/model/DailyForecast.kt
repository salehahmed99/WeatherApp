package com.example.weatherapp.domain.model

data class DailyForecast(
    val date: String,
    val maxTemp: Double,
    val minTemp: Double,
    val condition: String
)