package com.example.weatherapp.ui.forecast

import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.data.local.WeatherCache
import com.example.weatherapp.data.api.WeatherApi
import com.example.weatherapp.data.repository.WeatherRepositoryImpl
import com.example.weatherapp.util.NetworkHelper

class ForecastActivity : AppCompatActivity() {
    private lateinit var viewModel: ForecastViewModel
    private lateinit var rvForecastAdapter: ForecastAdapter
    private lateinit var rvForecast: RecyclerView
    private lateinit var tvError: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast)

        setupViewModel()
        setupUI()
        prepareRecyclerView()
        observeViewModel()


        // Load forecast
        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)
        if (latitude != 0.0 && longitude != 0.0) {
            viewModel.loadForecast(latitude, longitude)
        } else {
            tvError.text = "Location unavailable"
            tvError.visibility = View.VISIBLE
        }
    }

    private fun setupUI(){
        rvForecast = findViewById(R.id.rv_forecast)
        tvError = findViewById(R.id.tv_error)
    }

    private fun prepareRecyclerView(){
        rvForecastAdapter = ForecastAdapter(this, emptyList())
        rvForecast.adapter = rvForecastAdapter
        rvForecast.layoutManager = LinearLayoutManager(this)
    }

    private fun observeViewModel(){
        viewModel.forecast.observe(this) { forecast ->
            if (forecast != null) {
                val adapter = ForecastAdapter(this, forecast)
                rvForecast.adapter = adapter
            }
        }

        viewModel.error.observe(this) { error ->
            tvError.text = error
            tvError.visibility = if (error != null) View.VISIBLE else View.GONE
        }
    }

    private fun setupViewModel(){
        val repository = WeatherRepositoryImpl(WeatherApi(), WeatherCache(this))
        val networkHelper = NetworkHelper(this)
        val factory = ForecastViewModelFactory(repository, networkHelper)
        viewModel = ViewModelProvider(this, factory).get(ForecastViewModel::class.java)
    }
}