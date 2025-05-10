package com.example.weatherapp.domain.model

import java.util.Date

data class DailyWeather(
    val date: Date,
    val temperature: Float,
    val condition: String,
    val humidity: Float,
    val windSpeed: Float
)