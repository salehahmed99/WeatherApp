package com.example.weatherapp.data.model

data class ForecastResponse(
    val days: List<Day>
) {
    data class Day(
        val datetime: String,
        val tempmax: Double,
        val tempmin: Double,
        val conditions: String
    )
}