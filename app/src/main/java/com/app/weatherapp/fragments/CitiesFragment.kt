package com.app.weatherapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.app.weatherapp.MainViewModel
import com.app.weatherapp.R
import com.app.weatherapp.databinding.FragmentCitiesBinding
import com.app.weatherapp.db.di.ServiceLocator
import com.app.weatherapp.db.entities.CitiesEntity
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class CitiesFragment : Fragment(R.layout.fragment_cities) {

    private var cityRepository = ServiceLocator.getCityRepository()
    private var binding: FragmentCitiesBinding? = null
    private val model: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCitiesBinding.bind(view)
        updateCurrentCityCard()
        binding?.run {
            ivBack.setOnClickListener {
                parentFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(
                        R.anim.slide_in_from_left,
                        R.anim.slide_out_to_right,
                        R.anim.slide_in_from_left,
                        R.anim.slide_out_to_right
                    )
                    .replace(R.id.fragment_container, MainFragment.newInstance())
                    .commit()
            }
            btnAdd.setOnClickListener {
                getWeatherByCity(editText.text.toString())
                val city =CitiesEntity(
                    id = UUID.randomUUID().toString(),
                    cityName = editText.text.toString()
                )
                lifecycleScope.launch {
                    cityRepository.insert(city)
                }


            }
            cvCurrentCity.setOnClickListener {
                parentFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(
                        R.anim.slide_in_from_left,
                        R.anim.slide_out_to_right,
                        R.anim.slide_in_from_left,
                        R.anim.slide_out_to_right
                    )
                    .replace(R.id.fragment_container, MainFragment.newInstance())
                    .commit()
            }
        }

    }

    private fun updateCurrentCityCard() {
        model.liveDataCurrent.observe(viewLifecycleOwner) { item ->
            binding?.run {
                tvCurrentCity.text = item.city
                tvCurrentTemp.text = item.currentTemp
                Picasso.get().load("https:" + item.imageUrl).into(ivCurrentCondition)
            }
        }
    }

    private fun getWeatherByCity(city: String){
        val url =
            "https://api.weatherapi.com/v1/forecast.json?key=${API_KEY}&q=${city}&days=${3}&aqi=no&alerts=no"
        val queue = Volley.newRequestQueue(context)
        val stringRequest = StringRequest(
            Request.Method.GET,
            url,
            { response ->
                Log.d("WeatherLog", "Response: $response")
            },
            { error ->
                Log.d("WeatherLog", "Error: $error")
            }
        )
        queue.add(stringRequest)
    }

    companion object {
        @JvmStatic
        fun newInstance() = CitiesFragment()

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}