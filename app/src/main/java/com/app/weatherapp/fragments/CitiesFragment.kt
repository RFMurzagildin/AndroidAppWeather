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
import com.app.weatherapp.models.WeatherModel
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

const val API_KEY_CITY = "25888f60af4ecb14b3ede9c79bd07d10"

class CitiesFragment : Fragment(R.layout.fragment_cities), CitiesAdapter.Listener {

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
                parentFragmentManager.popBackStack()
            }
            ivBack.setOnClickListener {
                parentFragmentManager.popBackStack()
            }
            btnAdd.setOnClickListener {
                val cityName = editText.text.toString().trim()
                if (cityName.isNotEmpty()) {
                    searchCity(cityName)
                } else {
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
    }

    private fun updateCurrentCityCard() {
        model.liveDataCurrentCity.observe(viewLifecycleOwner) { item ->
            binding?.run {
                val parseCurrentTemp = item.currentTemp.toFloat().toInt()
                if (parseCurrentTemp <= 0) {
                    tvCurrentTemp.text = "$parseCurrentTemp°"
                } else {
                    tvCurrentTemp.text = "+$parseCurrentTemp°"
                }
                tvCurrentCity.text = item.city
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
                        cityName = "*Unknown*"
                    }
                    if (checkKeyInJSON(mainObject, "lat")) {
                        lat = mainObject.getString("lat")
                    } else {
                        lat = "*Unknown*"
                    }
                    if (checkKeyInJSON(mainObject, "lon")) {
                        lon = mainObject.getString("lon")
                    } else {
                        lon = "*Unknown*"
                    }
                    if (checkKeyInJSON(mainObject, "country")) {
                        country = mainObject.getString("country")
                    } else {
                        country = "*Unknown*"
                    }
                    if (checkKeyInJSON(mainObject, "state")) {
                        state = mainObject.getString("state")
                    } else {
                        state = "*Unknown*"
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
            CitiesAdapter(it, requireContext(), listener = this@CitiesFragment)
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

    private fun loading(visible: Boolean) {
        if (visible) {
            binding?.run {

            }
        }
    }

    override fun onClick(cityName: String) {
        requestWeatherData(cityName)
        parentFragmentManager.popBackStack()
    }

    private fun requestWeatherData(city: String) {
        val url =
            "https://api.weatherapi.com/v1/forecast.json?key=${API_KEY_WEATHER}&q=${city}&days=${3}&aqi=no&alerts=no"
        val queue = Volley.newRequestQueue(context)
        val stringRequest = StringRequest(
            Request.Method.GET,
            url,
            { response ->
                parseWeatherData(response)
            },
            { error ->
                Log.d("WeatherLog", "Error: $error")
            }
        )
        queue.add(stringRequest)
    }

    private fun parseWeatherData(result: String) {
        val mainObject = JSONObject(result)
        val list = parseDays(mainObject)
        parseCurrentData(mainObject, list[0])

    }

    private fun parseDays(mainObject: JSONObject): List<WeatherModel> {
        val list = ArrayList<WeatherModel>()
        val incorrectName = mainObject.getJSONObject("location").getString("name")
        val name = String(incorrectName.toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8)
        val daysArray = mainObject.getJSONObject("forecast").getJSONArray("forecastday")
        for (i in 0 until daysArray.length()) {
            val day = daysArray[i] as JSONObject
            val item = WeatherModel(
                current = false,
                city = name,
                time = day.getString("date"),
                condition = day.getJSONObject("day").getJSONObject("condition").getString("text"),
                imageUrl = day.getJSONObject("day").getJSONObject("condition").getString("icon"),
                currentTemp = "",
                maxTemp = day.getJSONObject("day").getString("maxtemp_c").toFloat().toInt()
                    .toString(),
                minTemp = day.getJSONObject("day").getString("mintemp_c").toFloat().toInt()
                    .toString(),
                hours = day.getJSONArray("hour").toString(),
            )
            list.add(item)
        }
        for (weatherModel in list) {
            println(weatherModel)
        }
        model.liveDataList.value = list
        return list
    }

    private fun parseCurrentData(mainObject: JSONObject, weatherItem: WeatherModel) {
        val item = WeatherModel(
            current = true,
            city = String(
                mainObject.getJSONObject("location").getString("name")
                    .toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8
            ),
            time = mainObject.getJSONObject("current").getString("last_updated"),
            condition = mainObject.getJSONObject("current").getJSONObject("condition")
                .getString("text"),
            imageUrl = mainObject.getJSONObject("current").getJSONObject("condition")
                .getString("icon"),
            currentTemp = mainObject.getJSONObject("current").getString("temp_c").toFloat().toInt()
                .toString(),
            maxTemp = weatherItem.maxTemp,
            minTemp = weatherItem.minTemp,
            hours = weatherItem.hours,
        )
        model.liveDataCurrentSelection.value = item
        loading(false)
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