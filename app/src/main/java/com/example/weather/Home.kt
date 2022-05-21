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
import android.graphics.Color
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
    override fun onCreateView(



        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        fragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        fetchCurrentLocationWeather(23.76137119142536.toString(), 90.35059989467042.toString())

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



        // Inflate the layout for this fragment
        return fragmentHomeBinding.root
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
        fragmentHomeBinding.tvTemp.text = ""+kelvinToCelsius(body!!.main.temp) + "°"
        fragmentHomeBinding.tvFeelsLike.text = "Feels like: "+kelvinToCelsius(body!!.main.feels_like) + "°"
        fragmentHomeBinding.tvWeatherType.text = body.weather[0].main
        fragmentHomeBinding.tvPressure.text = body.main.pressure.toString()
        fragmentHomeBinding.tvHumidity.text = body.main.humidity.toString() + " %"
        fragmentHomeBinding.tvWindSpeed.text = body.wind.speed.toString() + " m/s"
        fragmentHomeBinding.tvCityName.text = body.name
        updateUI(body.weather[0].id)
    }

    private fun updateUI(id: Int) {

        if (id in 200..232)
        {
            fragmentHomeBinding.ivWeatherBg.setImageResource(R.drawable.thunder_bg)
            fragmentHomeBinding.tvCityName.setTextColor(Color.WHITE)
            fragmentHomeBinding.tvTemp.setTextColor(Color.WHITE)
            fragmentHomeBinding.tvDateTime.setTextColor(Color.WHITE)
            fragmentHomeBinding.tvFeelsLike.setTextColor(Color.WHITE)
            fragmentHomeBinding.tvWeatherType.setTextColor(Color.WHITE)
            fragmentHomeBinding.blackBar.setBackgroundColor(Color.WHITE)
        }
        else if(id in 300..321)
        {
            fragmentHomeBinding.ivWeatherBg.setImageResource(R.drawable.drizzle_bg)
            fragmentHomeBinding.tvCityName.setTextColor(resources.getColor(R.color.new_black))
            fragmentHomeBinding.tvTemp.setTextColor(resources.getColor(R.color.new_black))
            fragmentHomeBinding.tvDateTime.setTextColor(resources.getColor(R.color.new_black))
            fragmentHomeBinding.tvFeelsLike.setTextColor(resources.getColor(R.color.new_black))
            fragmentHomeBinding.tvWeatherType.setTextColor(resources.getColor(R.color.new_black))
            fragmentHomeBinding.blackBar.setBackgroundColor(resources.getColor(R.color.new_black))
        }
        else if(id in 500..531)
        {
            fragmentHomeBinding.ivWeatherBg.setImageResource(R.drawable.rain_bg)
            fragmentHomeBinding.tvCityName.setTextColor(Color.WHITE)
            fragmentHomeBinding.tvTemp.setTextColor(Color.WHITE)
            fragmentHomeBinding.tvDateTime.setTextColor(Color.WHITE)
            fragmentHomeBinding.tvFeelsLike.setTextColor(Color.WHITE)
            fragmentHomeBinding.tvWeatherType.setTextColor(Color.WHITE)
            fragmentHomeBinding.blackBar.setBackgroundColor(Color.WHITE)
        }
        else if(id in 600..620)
        {
            fragmentHomeBinding.ivWeatherBg.setImageResource(R.drawable.snow_bg)
            fragmentHomeBinding.tvCityName.setTextColor(resources.getColor(R.color.new_black))
            fragmentHomeBinding.tvTemp.setTextColor(resources.getColor(R.color.new_black))
            fragmentHomeBinding.tvDateTime.setTextColor(resources.getColor(R.color.new_black))
            fragmentHomeBinding.tvFeelsLike.setTextColor(resources.getColor(R.color.new_black))
            fragmentHomeBinding.tvWeatherType.setTextColor(resources.getColor(R.color.new_black))
            fragmentHomeBinding.blackBar.setBackgroundColor(resources.getColor(R.color.new_black))
        }
        else if(id in 700..781)
        {
            fragmentHomeBinding.ivWeatherBg.setImageResource(R.drawable.mist_bg)
            fragmentHomeBinding.tvCityName.setTextColor(resources.getColor(R.color.new_black))
            fragmentHomeBinding.tvTemp.setTextColor(resources.getColor(R.color.new_black))
            fragmentHomeBinding.tvDateTime.setTextColor(resources.getColor(R.color.new_black))
            fragmentHomeBinding.tvFeelsLike.setTextColor(resources.getColor(R.color.new_black))
            fragmentHomeBinding.tvWeatherType.setTextColor(resources.getColor(R.color.new_black))
            fragmentHomeBinding.blackBar.setBackgroundColor(resources.getColor(R.color.new_black))
        }
        else if(id == 800)
        {
            fragmentHomeBinding.ivWeatherBg.setImageResource(R.drawable.clear_bg)
            fragmentHomeBinding.tvCityName.setTextColor(resources.getColor(R.color.new_black))
            fragmentHomeBinding.tvTemp.setTextColor(resources.getColor(R.color.new_black))
            fragmentHomeBinding.tvDateTime.setTextColor(resources.getColor(R.color.new_black))
            fragmentHomeBinding.tvFeelsLike.setTextColor(resources.getColor(R.color.new_black))
            fragmentHomeBinding.tvWeatherType.setTextColor(resources.getColor(R.color.new_black))
            fragmentHomeBinding.blackBar.setBackgroundColor(resources.getColor(R.color.new_black))
        }
        else
        {
            fragmentHomeBinding.ivWeatherBg.setImageResource(R.drawable.cloud_bg)
            fragmentHomeBinding.tvCityName.setTextColor(resources.getColor(R.color.new_black))
            fragmentHomeBinding.tvTemp.setTextColor(resources.getColor(R.color.new_black))
            fragmentHomeBinding.tvDateTime.setTextColor(resources.getColor(R.color.new_black))
            fragmentHomeBinding.tvFeelsLike.setTextColor(resources.getColor(R.color.new_black))
            fragmentHomeBinding.tvWeatherType.setTextColor(resources.getColor(R.color.new_black))
            fragmentHomeBinding.blackBar.setBackgroundColor(resources.getColor(R.color.new_black))
        }



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
        const val API_KEY = "3c709ccaf2730aa1c263925f75db631a"
    }









}