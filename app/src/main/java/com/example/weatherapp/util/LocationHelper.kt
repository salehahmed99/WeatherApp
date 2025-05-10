package com.example.weatherapp.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.core.content.ContextCompat


class LocationHelper(private val context: Context) {
    private var locationManager: LocationManager? = null
    private var locationCallback: ((Location?) -> Unit)? = null

    fun getLocation(callback: (Location?) -> Unit) {
        this.locationCallback = callback
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Try to get last known location
            val lastKnownLocation = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                ?: locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            if (lastKnownLocation != null) {
                callback(lastKnownLocation)
                return
            }

            // Request location updates
            try {
                locationManager?.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000L,
                    10f,
                    locationListener
                )
            } catch (e: SecurityException) {
                callback(null)
            }
        } else {
            callback(null)
        }
    }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            locationCallback?.invoke(location)
            locationManager?.removeUpdates(this)
        }

        override fun onProviderDisabled(provider: String) {
            locationCallback?.invoke(null)
        }

    }

    fun stop() {
        locationManager?.removeUpdates(locationListener)
    }
}