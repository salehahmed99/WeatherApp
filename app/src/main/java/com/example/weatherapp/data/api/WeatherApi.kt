package com.example.weatherapp.data.api

import com.example.weatherapp.data.model.CurrentWeatherResponse
import com.example.weatherapp.data.model.ForecastResponse
import com.example.weatherapp.util.Constants
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class WeatherApi {
    fun getCurrentWeather(latitude: Double, longitude: Double): CurrentWeatherResponse? {
        val url =
            "${Constants.BASE_URL}$latitude,$longitude?key=${Constants.API_KEY}&unitGroup=metric&contentType=json&include=current&elements=datetime,temp,conditions,humidity,windspeed"
        val response = makeHttpRequest(url) ?: return null
        val json = JSONObject(response)
        val currentConditionsJSON = json.getJSONObject("currentConditions")
        val currentConditions = CurrentWeatherResponse.CurrentConditions(
            temp = currentConditionsJSON.getDouble("temp"),
            conditions = currentConditionsJSON.getString("conditions"),
            humidity = currentConditionsJSON.getDouble("humidity"),
            windspeed = currentConditionsJSON.getDouble("windspeed")
        )
        return CurrentWeatherResponse(currentConditions)
    }

    fun getForecast(latitude: Double, longitude: Double): ForecastResponse? {
        val url =
            "${Constants.BASE_URL}$latitude,$longitude/today/next5days?key=${Constants.API_KEY}&unitGroup=metric&contentType=json&include=days&elements=tempmax,tempmin,datetime,conditions"
        val response = makeHttpRequest(url) ?: return null
        val json = JSONObject(response)
        val daysArrayJSON = json.getJSONArray("days")
        val days = mutableListOf<ForecastResponse.Day>()

        for (i in 0 until daysArrayJSON.length()) {
            val day = daysArrayJSON.getJSONObject(i)
            days.add(
                ForecastResponse.Day(
                    datetime = day.getString("datetime"),
                    tempmax = day.getDouble("tempmax"),
                    tempmin = day.getDouble("tempmin"),
                    conditions = day.getString("conditions")
                )
            )
        }
        return ForecastResponse(days)
    }

    private fun makeHttpRequest(urlString: String): String? {
        var connection: HttpURLConnection? = null
        try {
            val url = URL(urlString)
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            connection.connect()

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream = connection.inputStream
                return inputStream.bufferedReader().use { it.readText() }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.disconnect()
        }
        return null
    }
}