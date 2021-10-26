package com.example.weatherapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.data.model.DailyWeather
import com.example.weatherapp.databinding.ItemDailyBinding
import com.example.weatherapp.getDateFromUnix
import com.example.weatherapp.getDayFromUnix
import com.example.weatherapp.loadImage

class DailyAdapter : RecyclerView.Adapter<DailyAdapter.ViewHolder>() {

    private var daily: List<DailyWeather> = emptyList()
    private var offset: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemDailyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val weather = daily[position]
        holder.bind(weather, position == 0)
    }

    override fun getItemCount(): Int = daily.size

    inner class ViewHolder(
        private val binding: ItemDailyBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(weather: DailyWeather, isFirst: Boolean) {
            binding.itemDailyTvDay.text =
                if (isFirst) binding.root.context.getString(R.string.today)
                else getDayFromUnix(weather.dt)
            binding.itemDailyTvDate.text = getDateFromUnix(weather.dt, offset)

            binding.itemDailyIvWeather.loadImage(weather.weather[0].icon)
            with((weather.pop * 100).toInt()) {
                binding.itemDailyTvPop.text = binding.root.context.getString(R.string.humidity_format).format(this)
                binding.itemDailyTvPop.visibility = if (this == 0) View.GONE else View.VISIBLE
            }

            binding.itemDailyTvTempHigh.text =
                binding.root.context.getString(R.string.temperature_format).format(weather.temp.max.toInt())
            binding.itemDailyTvTempLow.text =
                binding.root.context.getString(R.string.temperature_format).format(weather.temp.min.toInt())
        }
    }

    fun setData(newDaily: List<DailyWeather>, newOffset: Int) {
        daily = newDaily
        offset = newOffset
        notifyDataSetChanged()
    }
}
