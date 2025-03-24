package com.app.weatherapp.adapters.citiesAdapter

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.app.weatherapp.databinding.CityItemBinding
import com.app.weatherapp.db.entities.CitiesEntity
import com.app.weatherapp.fragments.API_KEY_WEATHER
import com.app.weatherapp.models.WeatherModel
import com.squareup.picasso.Picasso
import org.json.JSONObject

class CitiesHolder(
    private val binding: CityItemBinding,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(city: CitiesEntity, onLongClickListener: (CitiesEntity) -> Unit) {
        with(binding) {
            tvCity.text = city.cityName
            getCityWeather(cityName = city.lat + "," + city.lon)
            itemView.setOnLongClickListener {
                onLongClickListener(city)
                true
            }
        }
    }

    private fun getCityWeather(cityName: String) {
        val url =
            "https://api.weatherapi.com/v1/forecast.json?key=${API_KEY_WEATHER}&q=${cityName}&days=${3}&aqi=no&alerts=no"
        val queue = Volley.newRequestQueue(context)
        val stringRequest = StringRequest(
            Request.Method.GET,
            url,
            { response ->
                val mainObject = JSONObject(response)
                val weatherModel = WeatherModel(
                    city = cityName,
                    time = "",
                    condition = "",
                    imageUrl = mainObject.getJSONObject("current").getJSONObject("condition").getString("icon"),
                    currentTemp = mainObject.getJSONObject("current").getString("temp_c"),
                    maxTemp = "",
                    minTemp = "",
                    hours = ""
                )
                with(binding){
                    tvTemp.text = "${weatherModel.currentTemp.toFloat().toInt()}"
                    Picasso.get().load("https:" + weatherModel.imageUrl).into(ivCondition)
                }
            },
            { error ->
                Log.d("WeatherLog", "Error : $error")
            }
        )
        queue.add(stringRequest)
    }
}