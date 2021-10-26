package com.example.weatherapp

import android.widget.ImageView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.weatherapp.data.model.Coordinates
import java.text.SimpleDateFormat
import java.util.*

const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
const val BASE_IMAGE_URL = "http://openweathermap.org/img/wn/"
const val API_KEY = "c5247ae5e847c15b99ee79c8e9b09cf6"
const val BASE_UNITS = "metric"
const val BASE_EXCLUDE = "minutely,alerts"

const val DEFAULT_CITY = "Moscow"

fun getOffsetTime(time: Int, offset: Int): Int =
    time - TimeZone.getDefault().rawOffset / 1000 + offset

fun Fragment.getDateTimeFromUnix(time: Int): String {
    val sdf = SimpleDateFormat("MMM d, yyyy hh:mm:ss a", Locale.ENGLISH)
    val date = Date(time * 1000L)
    return getString(R.string.time_format).format(sdf.format(date))
}

fun getDateFromUnix(time: Int, offset: Int): String {
    val sdf = SimpleDateFormat("MMM d", Locale.ENGLISH)
    val date = Date(getOffsetTime(time, offset) * 1000L)
    return sdf.format(date)
}

fun getDayFromUnix(time: Int): String {
    val sdf = SimpleDateFormat("EEEE", Locale.ENGLISH)
    val date = Date(time * 1000L)
    return sdf.format(date)
}

fun getTimeFromUnix(time: Int, offset: Int): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
    val date = Date(getOffsetTime(time, offset) * 1000L)
    return sdf.format(date)
}

fun getHourFromUnix(time: Int, offset: Int): String {
    val sdf = SimpleDateFormat("hh a", Locale.ENGLISH)
    val date = Date(getOffsetTime(time, offset) * 1000L)
    return sdf.format(date)
}

fun Fragment.getTemperature(temp: Float): String =
    getString(R.string.temperature_format).format(temp.toInt())

fun Fragment.getFeelLikeTemperature(temp: Float): String =
    getString(R.string.feels_like_format).format(temp.toInt())

fun Fragment.getHighLowTemperature(max: Float, min: Float): String =
    getString(R.string.high_low_format).format(max.toInt(), min.toInt())

fun Fragment.getPressure(pressureHPa: Int): String {
    val pressuremmHg: Int = (pressureHPa * 0.75006375541921).toInt()
    return getString(R.string.pressure_format).format(pressuremmHg)
}

fun Fragment.getHumidity(humidity: Int) = getString(R.string.humidity_format).format(humidity)

fun Fragment.getVisibility(visibility: Int) =
    getString(R.string.visibility_format).format((visibility / 1000).toFloat())

fun Fragment.getWindSpeed(speed: Float) = getString(R.string.wind_speed_format).format(speed)

fun Fragment.getCoordinates(coordinates: Coordinates) =
    getString(R.string.coordinates_format).format(coordinates.lat, coordinates.lon)

fun getWindDirectionDrawable(windDeg: Int): Int {
    val wind = (windDeg + 180) % 360
    with(wind.toFloat()) {
        return when {
            this >= 337.5 || this < 22.5 -> R.drawable.ic_wi_direction_up
            this >= 22.5 && this < 67.5 -> R.drawable.ic_wi_direction_up_right
            this >= 67.5 && this < 112.5 -> R.drawable.ic_wi_direction_right
            this >= 112.5 && this < 157.5 -> R.drawable.ic_wi_direction_down_right
            this >= 157.5 && this < 202.5 -> R.drawable.ic_wi_direction_down
            this >= 202.5 && this < 247.5 -> R.drawable.ic_wi_direction_down_left
            this >= 247.5 && this < 292.5 -> R.drawable.ic_wi_direction_left
            else -> R.drawable.ic_wi_direction_up_left
        }
    }
}

fun String.firstToCapital(): String {
    val result = mutableListOf<String>()

    this.split(" ").forEach {
        result.add(it[0].uppercaseChar() + it.substring(1))
    }

    return result.joinToString(" ")
}

fun ImageView.loadImage(url: String) {
    val imgUrl = "$BASE_IMAGE_URL$url.png"
    val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()

    val circleProgressDrawable = CircularProgressDrawable(this.context).apply {
        strokeWidth = 5f
        centerRadius = 30f
        start()
    }

    Glide.with(this.context)
        .load(imgUri)
        .placeholder(circleProgressDrawable)
        .fitCenter()
        .centerCrop()
        .into(this)
}

fun Fragment.makeToast(query: String) {
    Toast.makeText(requireContext(), query, Toast.LENGTH_SHORT).show()
}
