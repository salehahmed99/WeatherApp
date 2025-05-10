package com.example.weatherapp.ui.current

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.weatherapp.R
import com.example.weatherapp.data.local.WeatherCache
import com.example.weatherapp.data.api.WeatherApi
import com.example.weatherapp.data.repository.WeatherRepositoryImpl
import com.example.weatherapp.ui.forecast.ForecastActivity
import com.example.weatherapp.util.LocationHelper
import com.example.weatherapp.util.NetworkHelper

class CurrentWeatherActivity : AppCompatActivity() {
    private lateinit var viewModel: CurrentWeatherViewModel
    private lateinit var locationHelper: LocationHelper
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var tvTemperature: TextView
    private lateinit var tvCondition: TextView
    private lateinit var tvHumidity: TextView
    private lateinit var tvWindSpeed: TextView
    private lateinit var tvError: TextView
    private lateinit var btnForecast: Button
    private lateinit var weatherCard: ConstraintLayout
    private var tvLocation: TextView? = null // Optional, not in current layout
    private var ivWeatherIcon: ImageView? = null // Optional, not in current layout
    private val locationPermissionCode = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_current_weather)

        // Make status bar transparent
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        // Initialize dependencies and UI
        locationHelper = LocationHelper(this)
        setupViewModel()
        setupUI()
        observeViewModel()

        // Check location permission after UI setup
        checkLocationPermission()

        // Setup pull-to-refresh
        swipeRefreshLayout.setOnRefreshListener {
            checkLocationPermission()
        }

        // Setup forecast button
        btnForecast.setOnClickListener {
            val intent = Intent(this, ForecastActivity::class.java)
            viewModel.location.value?.let {
                intent.putExtra("latitude", it.latitude)
                intent.putExtra("longitude", it.longitude)
            }
            startActivity(intent)
        }
    }

    private fun setupUI() {
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout)
        tvTemperature = findViewById(R.id.tv_temperature)
        tvCondition = findViewById(R.id.tv_condition)
        tvHumidity = findViewById(R.id.tv_humidity)
        tvWindSpeed = findViewById(R.id.tv_wind_speed)
        tvError = findViewById(R.id.tv_error)
        btnForecast = findViewById(R.id.btn_forecast)
        weatherCard = findViewById(R.id.weather_card)
    }

    private fun setupViewModel() {
        val repository = WeatherRepositoryImpl(WeatherApi(), WeatherCache(this))
        val networkHelper = NetworkHelper(this)
        val factory = CurrentWeatherViewModelFactory(repository, networkHelper)
        viewModel = ViewModelProvider(this, factory).get(CurrentWeatherViewModel::class.java)
    }

    private fun observeViewModel() {
        viewModel.weather.observe(this) { weather ->
            swipeRefreshLayout.isRefreshing = false
            if (weather != null) {
                // Fade-in animation for weather card
                weatherCard.alpha = 0f
                weatherCard.animate().alpha(1f).setDuration(500).start()

                // Update UI
                tvTemperature.text = getString(R.string.temperature, weather.temperature)
                tvCondition.text = weather.condition
                tvHumidity.text = getString(R.string.humidity, weather.humidity)
                tvWindSpeed.text = getString(R.string.wind_speed, weather.windSpeed)

                // Optional: Set weather icon if present
                ivWeatherIcon?.let {
                    val iconRes = when (weather.condition.lowercase()) {
                        "sunny", "clear" -> R.drawable.ic_sunny
                        "cloudy", "partly cloudy" -> R.drawable.ic_cloudy
                        "rain", "shower" -> R.drawable.ic_rain
                        else -> R.drawable.ic_sunny
                    }
                    it.setImageResource(iconRes)
                }

                // Set dynamic background
                val rootLayout = findViewById<ConstraintLayout>(R.id.root_layout)
                val backgroundRes = when (weather.condition.lowercase()) {
                    "sunny", "clear" -> R.drawable.background_gradient
                    "cloudy", "partly cloudy" -> R.drawable.background_cloudy
                    "rain", "shower" -> R.drawable.background_rain
                    else -> R.drawable.background_gradient
                }
                rootLayout.setBackgroundResource(backgroundRes)
            }
        }

        viewModel.error.observe(this) { error ->
            swipeRefreshLayout.isRefreshing = false
            tvError.text = error
            tvError.visibility = if (error != null) View.VISIBLE else View.GONE
        }

        viewModel.location.observe(this) { location ->
            tvLocation?.text = if (location != null) {
                "Lat ${String.format("%.2f", location.latitude)}, Lon ${String.format("%.2f", location.longitude)}"
            } else {
                "Location: Unknown"
            }
        }
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION),
                locationPermissionCode
            )
            swipeRefreshLayout.isRefreshing = false
        } else {
            fetchLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchLocation()
        } else {
            viewModel.refreshWeather(null)
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun fetchLocation() {
        locationHelper.getLocation { location ->
            viewModel.refreshWeather(location)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        locationHelper.stop()
    }
}