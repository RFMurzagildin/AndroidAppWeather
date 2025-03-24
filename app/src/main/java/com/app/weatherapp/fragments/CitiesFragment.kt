package com.app.weatherapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.app.weatherapp.MainViewModel
import com.app.weatherapp.R
import com.app.weatherapp.adapters.citiesAdapter.CitiesAdapter
import com.app.weatherapp.databinding.FragmentCitiesBinding
import com.app.weatherapp.db.di.ServiceLocator
import com.app.weatherapp.db.entities.CitiesEntity
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

const val API_KEY_CITY = "25888f60af4ecb14b3ede9c79bd07d10"

class CitiesFragment : Fragment(R.layout.fragment_cities) {

    private var cityRepository = ServiceLocator.getCityRepository()
    private var binding: FragmentCitiesBinding? = null
    private val model: MainViewModel by activityViewModels()
    protected var cityAdapter: CitiesAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCitiesBinding.bind(view)
        updateCurrentCityCard()

        binding?.run {
            cvCurrentCity.setOnClickListener {
                parentFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(
                        R.anim.slide_in_from_right,
                        R.anim.slide_out_to_left,
                        R.anim.slide_in_from_right,
                        R.anim.slide_out_to_left
                    )
                    .replace(R.id.fragment_container, MainFragment.newInstance())
                    .commit()
            }
            ivBack.setOnClickListener {
                parentFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(
                        R.anim.slide_in_from_right,
                        R.anim.slide_out_to_left,
                        R.anim.slide_in_from_right,
                        R.anim.slide_out_to_left
                    )
                    .replace(R.id.fragment_container, MainFragment.newInstance())
                    .commit()
            }
            btnAdd.setOnClickListener {
                val cityName = editText.text.toString().trim()
                if (cityName.isNotEmpty()) {
                    searchCity(cityName)
                }else{
                    Toast.makeText(
                        requireContext(),
                        "Поле не может быть пустым",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding?.textInputLayout?.error = "Поле не может быть пустым"
                }
            }

        }

        lifecycleScope.launch {
            val cities = cityRepository.getAllCities()
            setupRecycleView(cities)
        }
//        getCitiesWeather()
    }

//    private fun getCitiesWeather(): ArrayList<WeatherModel>{
//        val list = ArrayList<WeatherModel>()
//        lifecycleScope.launch {
//            val cities = cityRepository.getAllCities()
//            for(i in 0 until cities.size){
//                val weatherModel = getCityWeather(cities[i].lat + "," + cities[i].lon)
//                weatherModel?.let { list.add(it) }
//            }
//        }
//        model.liveDataCitiesWeather.value = list
//        return list
//    }

//    private fun getCityWeather(city: String): WeatherModel?{
//        val url = "https://api.weatherapi.com/v1/forecast.json?key=${API_KEY_WEATHER}&q=${city}&days=${3}&aqi=no&alerts=no"
//        val queue = Volley.newRequestQueue(context)
//        var weatherModel: WeatherModel? = null
//        val stringRequest = StringRequest(
//            Request.Method.GET,
//            url,
//            { response ->
//                val mainObject = JSONObject(response)
//                val incorrectName = mainObject.getJSONObject("location").getString("name")
//                val name = String(incorrectName.toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8)
//                val currentTemp = mainObject.getJSONObject("current").getString("temp_c")
//                val icon = mainObject.getJSONObject("current").getJSONObject("condition").getString("icon")
//                weatherModel = WeatherModel(
//                    city = name,
//                    time = "",
//                    condition = "",
//                    imageUrl = icon,
//                    currentTemp = currentTemp,
//                    maxTemp = "",
//                    minTemp = "",
//                    hours = ""
//                )
//                return weatherModel
//            },
//            { error ->
//                Log.d("WeatherLog", "Error $error")
//            }
//        )
//        queue.add(stringRequest)
//        return weatherModel
//    }

    private fun updateCurrentCityCard() {
        model.liveDataCurrent.observe(viewLifecycleOwner) { item ->
            binding?.run {
                tvCurrentCity.text = item.city
                tvCurrentTemp.text = item.currentTemp
                Picasso.get().load("https:" + item.imageUrl).into(ivCurrentCondition)
            }
        }
    }

    private fun searchCity(city: String) {
        val url =
            "http://api.openweathermap.org/geo/1.0/direct?q=${city}&limit=${1}&appid=${API_KEY_CITY}"
        val queue = Volley.newRequestQueue(context)
        val stringRequest = StringRequest(
            Request.Method.GET,
            url,
            { response ->
                binding?.textInputLayout?.error = null
                val jsonArray = JSONArray(response)
                if (jsonArray.length() >= 1) {
                    val mainObject = jsonArray.getJSONObject(0)
                    val cityName: String
                    val lat: String
                    val lon: String
                    val country: String
                    val state: String
                    if (checkKeyInJSON(mainObject, "name")) {
                        cityName = mainObject.getString("name")
                    } else {
                        cityName = "неизвестно"
                    }
                    if (checkKeyInJSON(mainObject, "lat")) {
                        lat = mainObject.getString("lat")
                    } else {
                        lat = "неизвестно"
                    }
                    if (checkKeyInJSON(mainObject, "lon")) {
                        lon = mainObject.getString("lon")
                    } else {
                        lon = "неизвестно"
                    }
                    if (checkKeyInJSON(mainObject, "country")) {
                        country = mainObject.getString("country")
                    } else {
                        country = "неизвестно"
                    }
                    if (checkKeyInJSON(mainObject, "state")) {
                        state = mainObject.getString("state")
                    } else {
                        state = "неизвестно"
                    }
                    lifecycleScope.launch {
                        val citiesFromDatabase = cityRepository.getAllCities()
                        for (i in 0 until citiesFromDatabase.size) {
                            if (citiesFromDatabase[i].lat == lat && citiesFromDatabase[i].lon == lon) {
                                binding?.textInputLayout?.error =
                                    "Данный город уже добавлен в список"
                                Toast.makeText(
                                    requireContext(),
                                    "Данный город уже добавлен в список",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.e("WeatherLog", "Данный город уже добавлен в список")
                                return@launch
                            }
                        }
                        val newCity = CitiesEntity(
                            UUID.randomUUID().toString(),
                            cityName,
                            lat,
                            lon,
                            country,
                            state
                        )
                        cityRepository.insert(newCity)
                        cityAdapter?.updateData(cityRepository.getAllCities())
                        Toast.makeText(requireContext(), "Город добавлен", Toast.LENGTH_SHORT)
                            .show()
                        binding?.editText?.setText("")
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Неправильно введено название города",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding?.textInputLayout?.error = "Неправильно введено название города"
                    Log.e("WeatherLog", "Неправильно введено название города")
                }

            },
            { error ->
                Log.e("WeatherLog", "Ошибка при выборке данных $error")
            }
        )
        queue.add(stringRequest)
    }

    private fun setupRecycleView(cities: MutableList<CitiesEntity>?) {
        cityAdapter = cities?.let {
            CitiesAdapter(it, requireContext())
        }
        binding?.run {
            rcView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                this.adapter = cityAdapter
            }
        }
    }

    private fun checkKeyInJSON(jsonObject: JSONObject, keyToCheck: String): Boolean {
        try {

            return jsonObject.has(keyToCheck)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("WeatherLog", "Ошибка при разборе JSON: ${e.message}")
            return false
        }
    }

    private fun loading(visible: Boolean){
        if(visible){
            binding?.run {

            }
        }
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