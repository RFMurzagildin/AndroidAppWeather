package com.app.weatherapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import android.Manifest
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.app.weatherapp.MainViewModel
import com.app.weatherapp.R
import com.app.weatherapp.adapters.VPAdapter
import com.app.weatherapp.adapters.WeatherModel
import com.app.weatherapp.databinding.FragmentMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso
import org.json.JSONObject

const val API_KEY = "c77d7f3b70914b85ae8200056251502"

class MainFragment : Fragment(R.layout.fragment_main) {

    private val fList = listOf(
        HoursFragment.newInstance(),
        DaysFragment.newInstance()
    )
    private val tList = listOf("Hours", "Days")
    private var pLauncher: ActivityResultLauncher<String>? = null
    private var binding: FragmentMainBinding? = null
    private val model: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMainBinding.bind(view)
        checkPermission()
        initAdapter()
        updateCurrentCard()
        requestWeatherData("Barcelona")
    }

    private fun initAdapter(){
        val adapter = VPAdapter(activity as FragmentActivity, fList)
        binding?.run {
            vp.adapter = adapter
            TabLayoutMediator(tabLayout, vp){
                    tab, pos -> tab.text = tList[pos]
            }.attach()
        }
    }

    private fun updateCurrentCard(){
        model.liveDataCurrent.observe(viewLifecycleOwner){ item ->
            binding?.run {
                tvData.text = item.time
                textCity.text = item.city
                if(item.currentTemp.isEmpty()){
                    tvCurrentTemp.text = "${item.maxTemp}°/${item.minTemp}°"
                }else{
                    tvCurrentTemp.text = "${item.currentTemp}°"
                }
                tvCondition.text = item.condition
                tvMaxMinTemp.text = if(item.currentTemp.isEmpty()) "" else "${item.maxTemp}°/${item.minTemp}°"
                Picasso.get().load("https:" + item.imageUrl).into(imWeather)
            }
        }
    }

    private fun requestWeatherData(city: String){
        val url = "https://api.weatherapi.com/v1/forecast.json?key=${API_KEY}&q=${city}&days=${3}&aqi=no&alerts=no"
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

    private fun parseWeatherData(result: String){
        val mainObject = JSONObject(result)
        val list = parseDays(mainObject)
        parseCurrentData(mainObject, list[0])
    }

    private fun parseDays(mainObject: JSONObject): List<WeatherModel>{
        val list = ArrayList<WeatherModel>()
        val name = mainObject.getJSONObject("location").getString("name")
        val daysArray = mainObject.getJSONObject("forecast").getJSONArray("forecastday")
        for(i in 0 until daysArray.length()){
            val day = daysArray[i] as JSONObject
            val item = WeatherModel(
                city = name,
                time = day.getString("date"),
                condition = day.getJSONObject("day").getJSONObject("condition").getString("text"),
                imageUrl = day.getJSONObject("day").getJSONObject("condition").getString("icon"),
                currentTemp = "",
                maxTemp = day.getJSONObject("day").getString("maxtemp_c").toFloat().toInt().toString(),
                minTemp = day.getJSONObject("day").getString("mintemp_c").toFloat().toInt().toString(),
                hours = day.getJSONArray("hour").toString(),
            )
            list.add(item)
        }
        model.liveDataList.value = list
        return list
    }

    private fun parseCurrentData(mainObject: JSONObject, weatherItem: WeatherModel){
        val item = WeatherModel(
            city = mainObject.getJSONObject("location").getString("name"),
            time = mainObject.getJSONObject("current").getString("last_updated"),
            condition = mainObject.getJSONObject("current").getJSONObject("condition").getString("text"),
            imageUrl = mainObject.getJSONObject("current").getJSONObject("condition").getString("icon"),
            currentTemp = mainObject.getJSONObject("current").getString("temp_c"),
            maxTemp = weatherItem.maxTemp,
            minTemp = weatherItem.minTemp,
            hours = weatherItem.hours,
        )
        model.liveDataCurrent.value = item
    }


    private fun permissionListener(){
        pLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()){
            Toast.makeText(activity, "Permission is $it", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkPermission(){
        if(!isPermissionsGranted(Manifest.permission.ACCESS_FINE_LOCATION)){
            permissionListener()
            pLauncher?.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object{
        fun newInstance() = MainFragment()
    }
}