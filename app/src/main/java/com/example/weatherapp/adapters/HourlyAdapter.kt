package com.example.weatherapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.data.model.HourlyWeather
import com.example.weatherapp.databinding.ItemHourlyBinding
import com.example.weatherapp.getHourFromUnix
import com.example.weatherapp.loadImage

class HourlyAdapter : RecyclerView.Adapter<HourlyAdapter.ViewHolder>() {

    private var hourly: List<HourlyWeather> = emptyList()
    private var offset: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemHourlyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val weather = hourly[position]
        holder.bind(weather, position == 0)
    }

    override fun getItemCount(): Int = hourly.size

    inner class ViewHolder(
        private val binding: ItemHourlyBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(weather: HourlyWeather, isFirst: Boolean) {
            binding.itemHourlyTvTime.text =
                if (isFirst) binding.root.context.getString(R.string.now)
                else getHourFromUnix(weather.dt, offset)

            with((weather.pop * 100).toInt()) {
                binding.itemHourlyTvPop.text =
                    binding.root.context.getString(R.string.humidity_format).format(this)
                binding.itemHourlyTvPop.visibility = if (this == 0) View.GONE else View.VISIBLE
            }

            binding.itemHourlyTvTemperature.text =
                binding.root.context
                    .getString(R.string.temperature_format).format(weather.temp.toInt())

            binding.itemHourlyIvWeather.loadImage(weather.weather[0].icon)
        }
    }

    fun setData(newHourly: List<HourlyWeather>, newOffset: Int) {
        hourly = newHourly
        offset = newOffset
        notifyDataSetChanged()
    }
}
