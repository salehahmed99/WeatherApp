package com.example.weatherapp.ui.forecast

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.domain.model.DailyForecast

class ForecastAdapter(
    private val context: Context,
    private val forecasts: List<DailyForecast>
) : RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>() {

    class ForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.findViewById(R.id.tv_date)
        val tvMaxTemp: TextView = itemView.findViewById(R.id.tv_max_temp)
        val tvMinTemp: TextView = itemView.findViewById(R.id.tv_min_temp)
        val tvCondition: TextView = itemView.findViewById(R.id.tv_condition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_forecast, parent, false)
        return ForecastViewHolder(view)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        val forecast = forecasts[position]
        holder.tvDate.text = forecast.date
        holder.tvMaxTemp.text = context.getString(R.string.max_temp, forecast.maxTemp)
        holder.tvMinTemp.text = context.getString(R.string.min_temp, forecast.minTemp)
        holder.tvCondition.text = forecast.condition
    }

    override fun getItemCount(): Int = forecasts.size
}