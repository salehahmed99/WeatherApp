package com.example.weatherapp.data.model

data class CurrentWeatherResponse(
    val currentConditions: CurrentConditions
) {
    data class CurrentConditions(
        val temp: Double,
        val conditions: String,
        val humidity: Double,
        val windspeed: Double
    )
}