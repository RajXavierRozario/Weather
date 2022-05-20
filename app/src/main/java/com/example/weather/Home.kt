package com.example.weather

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.provider.Settings
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import com.example.weather.POJO.ModelClass
import com.example.weather.Utilities.ApiUtilities
import com.example.weather.databinding.FragmentHomeBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.RoundingMode
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.Period
import java.time.ZoneId
import java.util.*
import kotlin.math.roundToInt


class Home : Fragment() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var fragmentHomeBinding: FragmentHomeBinding


    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        getCurrentLocation()

        fragmentHomeBinding.etGetCityName.setOnEditorActionListener { v, actionId, keyEvent ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH)
            {
                getCityWeather(fragmentHomeBinding.etGetCityName.text.toString())
                val view = activity?.currentFocus
                if(view!=null)
                {
                    val imm:InputMethodManager=
                        activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                    fragmentHomeBinding.etGetCityName.clearFocus()
                }
                true
            }
            else false
        }

    }

    private fun getCityWeather(cityName:String)
    {
        fragmentHomeBinding.pbLoading.visibility = View.VISIBLE
        ApiUtilities.getApiInterface()?.getCityWeatherData(cityName, API_KEY)?.enqueue(
            object:Callback<ModelClass>{
                override fun onResponse(call: Call<ModelClass>, response: Response<ModelClass>) {
                    setDataOnViews((response.body()))
                }

                override fun onFailure(call: Call<ModelClass>, t: Throwable) {
                    Toast.makeText(activity?.applicationContext, "Wrong City", Toast.LENGTH_SHORT).show()
                }

            })
    }

    private fun getCurrentLocation()
    {
        if(checkPermission())
        {
            if(isLocationEnabled())
            {
                if(ActivityCompat.checkSelfPermission(
                        requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        requireActivity(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ){
                    requestPermission()
                    return
                }
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(requireActivity()){ task->
                    val location: Location?=task.result
                    if(location==null)
                    {
                        fetchCurrentLocationWeather(23.76137119142536.toString(), 90.35059989467042.toString())
                    }
                    else
                    {
                        fetchCurrentLocationWeather(location.latitude.toString(),location.longitude.toString())
                    }
                }

            }
            else
            {
                Toast.makeText(activity?.applicationContext, "Turn on Location", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }
    }

    private fun fetchCurrentLocationWeather(latitude: String, longitude:String)
    {
        fragmentHomeBinding.pbLoading.visibility = View.VISIBLE
        ApiUtilities.getApiInterface()?.getCurrentWeatherData(latitude, longitude, API_KEY)?.enqueue(
            object : Callback<ModelClass> {
                override fun onResponse(call: Call<ModelClass>, response: Response<ModelClass>) {
                    if(response.isSuccessful)
                    {
                        setDataOnViews(response.body())
                    }
                }

                override fun onFailure(call: Call<ModelClass>, t: Throwable) {
                    Toast.makeText(activity?.applicationContext, "ERROR", Toast.LENGTH_SHORT).show()
                }

            })
    }

    private fun setDataOnViews(body: ModelClass?) {
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm")
        val currentDate = sdf.format(Date())
        fragmentHomeBinding.tvDateTime.text = currentDate
        fragmentHomeBinding.tvDayMaxTemp.text = ""+kelvinToCelsius(body!!.main.temp_max) + "°"
        fragmentHomeBinding.tvDayMinTemp.text = ""+kelvinToCelsius(body!!.main.temp_min) + "°"
        fragmentHomeBinding.tvTemp.text = ""+kelvinToCelsius(body!!.main.temp)
        fragmentHomeBinding.tvFeelsLike.text = "Feels like: "+kelvinToCelsius(body!!.main.feels_like) + "°"
        fragmentHomeBinding.tvWeatherType.text = body.weather[0].main
        fragmentHomeBinding.tvPressure.text = body.main.pressure.toString()
        fragmentHomeBinding.tvHumidity.text = body.main.humidity.toString() + " %"
        fragmentHomeBinding.tvWindSpeed.text = body.wind.speed.toString() + " m/s"
        fragmentHomeBinding.tvCityName.text = body.name
        updateUI(body.weather[0].id)
    }


    private fun updateUI(id: Int) {
        fragmentHomeBinding.pbLoading.visibility = View.GONE
        fragmentHomeBinding.mainLayout.visibility = View.VISIBLE
    }

    private fun kelvinToCelsius(temp: Double): Double
    {
    var intTemp = temp
        intTemp = intTemp.minus(273)
        return intTemp.toBigDecimal().setScale(1, RoundingMode.UP).toDouble()
    }


    companion object
    {
        const val PERMISSION_REQUEST_ACCESS_LOCATION = 100
        const val API_KEY = "3c709ccaf2730aa1c263925f75db631a"
    }

    private fun isLocationEnabled():Boolean{
        val locationManager:LocationManager=activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun requestPermission(){
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_REQUEST_ACCESS_LOCATION
        )
    }

    private fun checkPermission():Boolean{
        if(ActivityCompat.checkSelfPermission(requireActivity(),
            android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(),
            android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            return true
        }
        return false
    }

    @Suppress("DEPRECATION")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== PERMISSION_REQUEST_ACCESS_LOCATION)
        {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(activity?.applicationContext, "Granted", Toast.LENGTH_SHORT).show()
                getCurrentLocation()
            }
            else{
                Toast.makeText(activity?.applicationContext, "Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        fragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        // Inflate the layout for this fragment
        return fragmentHomeBinding.root
    }


}