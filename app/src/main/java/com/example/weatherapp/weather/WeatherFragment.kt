package com.example.weatherapp.weather

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.*
import com.example.weatherapp.R
import com.example.weatherapp.adapters.DailyAdapter
import com.example.weatherapp.adapters.HourlyAdapter
import com.example.weatherapp.data.model.Coordinates
import com.example.weatherapp.data.model.areFilled
import com.example.weatherapp.databinding.FragmentWeatherBinding
import com.google.android.gms.location.*
import java.util.*

class WeatherFragment : Fragment() {

    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: FragmentWeatherBinding
    private lateinit var viewModel: WeatherViewModel

    private val hourlyAdapter = HourlyAdapter()
    private val dailyAdapter = DailyAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWeatherBinding.inflate(inflater, container, false)

        val repository =
            (requireContext().applicationContext as WeatherApplication).weatherRepository

        val viewModelFactory = WeatherViewModelFactory(repository)
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(WeatherViewModel::class.java)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        setupRecyclerView()
        getLastLocation(true)
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservable()

        binding.fragmentWeatherSrl.setOnRefreshListener {
            viewModel.getWeather()
        }
    }

    private fun setupRecyclerView() {
        binding.fragmentWeatherRvHourlyForecast.apply {
            adapter = hourlyAdapter
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        binding.fragmentWeatherRvDailyForecast.apply {
            adapter = dailyAdapter
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun setupObservable() {
        viewModel.dataLoading.observe(
            viewLifecycleOwner,
            {
                binding.fragmentWeatherSrl.isRefreshing = it
                binding.fragmentWeatherGroup.visibility =
                    if (!it && viewModel.requestSucceeded.value!!) View.VISIBLE else View.GONE
            }
        )

        viewModel.requestSucceeded.observe(
            viewLifecycleOwner,
            {
                binding.fragmentWeatherIvNoConnection.visibility =
                    if (!it) View.VISIBLE else View.GONE
            }
        )

        viewModel.cityName.observe(
            viewLifecycleOwner,
            {
                (activity as MainActivity).supportActionBar?.title = it
                binding.fragmentWeatherTvCity.text = it
            }
        )

        viewModel.coordinates.observe(
            viewLifecycleOwner,
            {
                binding.fragmentWeatherTvCoordinates.text = getCoordinates(it)
            }
        )

        viewModel.weatherResponse.observe(
            viewLifecycleOwner,
            {
                binding.fragmentWeatherTvUpdateTime.text =
                    getDateTimeFromUnix(it.current.dt)
                binding.fragmentWeatherTvTemperature.text = getTemperature(it.current.temp)
                binding.fragmentWeatherIvWeatherIcon
                    .loadImage(it.current.weather[0].icon)
                binding.fragmentWeatherTvWeatherMain.text =
                    it.current.weather[0].description.firstToCapital()
                binding.fragmentWeatherTvFeelsLike.text =
                    getFeelLikeTemperature(it.current.feels_like)
                binding.fragmentWeatherTvHighLowTemp.text =
                    getHighLowTemperature(it.daily[0].temp.max, it.daily[0].temp.min)
                binding.fragmentWeatherTvSunrise.text =
                    getTimeFromUnix(it.current.sunrise, it.timezone_offset)
                binding.fragmentWeatherTvPressure.text = getPressure(it.current.pressure)
                binding.fragmentWeatherTvHumidity.text = getHumidity(it.current.humidity)
                binding.fragmentWeatherTvSunset.text =
                    getTimeFromUnix(it.current.sunset, it.timezone_offset)
                binding.fragmentWeatherTvVisibility.text = getVisibility(it.current.visibility)
                binding.fragmentWeatherTvWindSpeed.text = getWindSpeed(it.current.wind_speed)
                binding.fragmentWeatherIvWindDir
                    .setImageResource(getWindDirectionDrawable(it.current.wind_deg))

                hourlyAdapter.setData(it.hourly.subList(0, 24), it.timezone_offset)
                binding.fragmentWeatherRvHourlyForecast.scrollToPosition(0)
                dailyAdapter.setData(it.daily, it.timezone_offset)
            }
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.overflow_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.overflow_menu__search -> {
                val searchView = item.actionView as SearchView
                searchView.queryHint = getString(R.string.enter_city)
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        query?.let {
                            searchView.clearFocus()
                            searchView.setQuery("", false)
                            item.collapseActionView()
                            binding.fragmentWeatherSvScroll.fullScroll(View.FOCUS_UP)
                            setupCoordinatesAndCity(it)
                        }

                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        return false
                    }
                })
            }
            R.id.overflow_menu__geopos -> {
                getLastLocation()
                binding.fragmentWeatherSvScroll.fullScroll(View.FOCUS_UP)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {

            val mLastLocation: Location = locationResult.lastLocation

            val city = getCityNameFromCoordinate(mLastLocation.latitude, mLastLocation.longitude)
            viewModel.setCoordinates(mLastLocation.latitude, mLastLocation.longitude, city)
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun isPermissionGranted(permission: String): Boolean =
        ActivityCompat.checkSelfPermission(
            requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                ACCESS_COARSE_LOCATION,
                ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != PERMISSION_ID) return

        if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            getLastLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 0
            fastestInterval = 0
            numUpdates = 1
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation(firstCall: Boolean = false) {
        if (isPermissionGranted(ACCESS_COARSE_LOCATION) &&
            isPermissionGranted(ACCESS_FINE_LOCATION)
        ) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnSuccessListener(requireActivity()) { location ->
                    if (location != null) {
                        val city = getCityNameFromCoordinate(location.latitude, location.longitude)
                        viewModel.setCoordinates(location.latitude, location.longitude, city)
                    } else {
                        requestNewLocationData()
                    }
                }
            } else {
                if (firstCall) setupCoordinatesAndCity(DEFAULT_CITY)
                makeToast(getString(R.string.turn_on_location))
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            if (firstCall) setupCoordinatesAndCity(DEFAULT_CITY)
            requestPermissions()
        }
    }

    private fun getCoordinateAndCityFromQuery(city: String): Pair<Coordinates, String> {

        val geocoder = Geocoder(context, Locale.ENGLISH)
        for (i in 1..10) {
            try {
                geocoder.getFromLocationName(city, 10)?.let {
                    it.forEach { it1 ->
                        return Pair(
                            Coordinates(it1.latitude, it1.longitude),
                            getCityNameFromCoordinate(it1.latitude, it1.longitude)
                        )
                    }
                }
            } catch (ex: Exception) {}
        }

        return Pair(Coordinates(0.0, 0.0), "")
    }

    private fun getCityNameFromCoordinate(lat: Double, lon: Double): String {

        val locationAddress = Geocoder(context, Locale.ENGLISH)
        for (i in 1..10) {
            locationAddress.getFromLocation(lat, lon, 10).forEach {
                if (!it.locality.isNullOrEmpty()) return it.locality
            }
        }

        return getString(R.string.city_not_found)
    }

    private fun setupCoordinatesAndCity(query: String) {
        val (coordinates, city) = getCoordinateAndCityFromQuery(query)
        if (!coordinates.areFilled() || city.isEmpty() ||
            city == getString(R.string.city_not_found)
        ) {
            makeToast(getString(R.string.city_not_found))
            return
        }

        viewModel.setCoordinates(coordinates.lat, coordinates.lon, city)
    }
}
