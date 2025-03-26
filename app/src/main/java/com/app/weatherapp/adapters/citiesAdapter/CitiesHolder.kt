package com.app.weatherapp.adapters.citiesAdapter

import android.content.Context
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.app.weatherapp.databinding.CityItemBinding
import com.app.weatherapp.db.entities.CitiesEntity
import com.app.weatherapp.fragments.API_KEY_WEATHER
import com.squareup.picasso.Picasso
import org.json.JSONObject

class CitiesHolder(
    private val binding: CityItemBinding,
    private val context: Context,
    private val listener: CitiesAdapter.Listener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(city: CitiesEntity, onLongClickListener: (CitiesEntity) -> Unit) {
        itemView.setOnClickListener {
            listener.onClick(cityName = city.lat + "," + city.lon)
        }
        itemView.setOnLongClickListener {
            onLongClickListener(city)
            true
        }

        loading(true)
        with(binding) {
            tvCity.text = city.cityName
            if (city.country != "*Unknown*" && city.state != "*Unknown*") {
                tvState.text = buildString {
                    append(city.country)
                    append("/")
                    append(city.state)
                }
            } else if (city.country != "*Unknown*" && city.state == "*Unknown*") {
                tvState.text = city.country
            } else if (city.country == "*Unknown*" && city.state == "*Unknown*") {
                tvState.text = ""
            } else {
                tvState.text = city.state
            }

            getCityWeather(cityName = city.lat + "," + city.lon)

        }
        loading(false)
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
                val imageUrl =
                    mainObject.getJSONObject("current").getJSONObject("condition").getString("icon")
                val currentTemp = mainObject.getJSONObject("current").getString("temp_c")

                with(binding) {
                    val parseCurrentTemp = currentTemp.toFloat().toInt()
                    if (parseCurrentTemp <= 0) {
                        tvTemp.text = "$parseCurrentTemp°"
                    } else {
                        tvTemp.text = "+$parseCurrentTemp°"
                    }

                    Picasso.get().load("https:$imageUrl").into(ivCondition)
                }
            },
            { error ->
                Log.d("WeatherLog", "Error : $error")
            }
        )
        queue.add(stringRequest)
    }

    private fun loading(progress: Boolean) {
        binding.run {
            if (progress) {
                progressBar.visibility = View.VISIBLE
                tvTemp.visibility = View.GONE
                ivCondition.visibility = View.GONE
            } else {
                progressBar.visibility = View.GONE
                tvTemp.visibility = View.VISIBLE
                ivCondition.visibility = View.VISIBLE
            }

        }
    }
}