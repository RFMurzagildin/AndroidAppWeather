package com.app.weatherapp.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.app.weatherapp.DialogManager
import com.app.weatherapp.MainViewModel
import com.app.weatherapp.R
import com.app.weatherapp.adapters.viewPagerAdapter.VPAdapter
import com.app.weatherapp.models.WeatherModel
import com.app.weatherapp.databinding.FragmentMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso
import org.json.JSONObject

const val API_KEY_WEATHER = "c77d7f3b70914b85ae8200056251502"

class MainFragment() : Fragment(R.layout.fragment_main) {

    private var fLocationClient: FusedLocationProviderClient? = null
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
        loading(true)
        checkPermission()
        fLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        initAdapter()
        updateCurrentCard()
        binding?.run {
            ivUpdate.setOnClickListener {
                tabLayout.selectTab(tabLayout.getTabAt(0))
                checkLocation()
            }
            ivCities.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        R.anim.slide_in_from_left,
                        R.anim.slide_out_to_right,
                        R.anim.slide_in_from_left,
                        R.anim.slide_out_to_right
                    )
                    .replace(R.id.fragment_container, CitiesFragment.newInstance())
                    .addToBackStack(null)
                    .commit()
            }

            ivSettings.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        R.anim.slide_in_left,
                        R.anim.slide_out_right,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                    )
                    .replace(R.id.fragment_container, SettingsFragment.newInstance())
                    .addToBackStack(null)
                    .commit()
            }
        }

    }

    override fun onResume() {
        super.onResume()
        checkLocation()
    }

    private fun initAdapter() {
        val adapter = VPAdapter(activity as FragmentActivity, fList)
        binding?.run {
            vp.adapter = adapter
            TabLayoutMediator(tabLayout, vp) { tab, pos ->
                tab.text = tList[pos]
            }.attach()
        }
    }

    private fun checkLocation() {
        if (isLocationEnabled()) {
            getLocation()
        } else {
            DialogManager.locationSettingsDialog(requireContext(), object : DialogManager.Listener {
                override fun onClick() {
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
            })
        }
    }

    private fun isLocationEnabled(): Boolean {
        val lm = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun getLocation() {
        if (!isLocationEnabled()) {
            Toast.makeText(requireContext(), "Location disabled.", Toast.LENGTH_SHORT).show()
            return
        }
        val ct = CancellationTokenSource()
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fLocationClient?.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, ct.token)
            ?.addOnCompleteListener {
                requestWeatherData("${it.result.latitude},${it.result.longitude}")
            }
    }

    private fun updateCurrentCard() {
        model.liveDataCurrent.observe(viewLifecycleOwner) { item ->
            binding?.run {
                tvData.text = item.time
                textCity.text = item.city
                if (item.currentTemp.isEmpty()) {
                    tvCurrentTemp.text = "${item.maxTemp}°/${item.minTemp}°"
                } else {
                    tvCurrentTemp.text = "${item.currentTemp}°"
                }
                tvCondition.text = item.condition
                tvMaxMinTemp.text =
                    if (item.currentTemp.isEmpty()) "" else "${item.maxTemp}°/${item.minTemp}°"
                Picasso.get().load("https:" + item.imageUrl).into(imWeather)
            }
        }
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
        model.liveDataList.value = list
        return list
    }

    private fun parseCurrentData(mainObject: JSONObject, weatherItem: WeatherModel) {
        val item = WeatherModel(
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
        model.liveDataCurrent.value = item
        loading(false)
    }


    private fun permissionListener() {
        pLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            Toast.makeText(activity, "Permission is $it", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkPermission() {
        if (!isPermissionsGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            permissionListener()
            pLauncher?.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun loading(progress: Boolean){
        if (progress){
            binding?.run {
                tabLayout.visibility = View.GONE
                currentWeatherZone.visibility = View.GONE
                vp.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
            }
        }else{
            binding?.run {
                tabLayout.visibility = View.VISIBLE
                currentWeatherZone.visibility = View.VISIBLE
                vp.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        fun newInstance() = MainFragment()
    }
}