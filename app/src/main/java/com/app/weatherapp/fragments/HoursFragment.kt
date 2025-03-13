package com.app.weatherapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.app.weatherapp.MainViewModel
import com.app.weatherapp.R
import com.app.weatherapp.adapters.WeatherAdapter
import com.app.weatherapp.adapters.WeatherModel
import com.app.weatherapp.databinding.FragmentHoursBinding
import com.app.weatherapp.databinding.FragmentMainBinding
import org.json.JSONArray
import org.json.JSONObject

class HoursFragment : Fragment(R.layout.fragment_hours) {

    private var binding: FragmentHoursBinding? = null
    private var adapter: WeatherAdapter? = null
    private val model: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHoursBinding.bind(view)
        initRecycleView()
        model.liveDataCurrent.observe(viewLifecycleOwner){
            adapter?.submitList(getHoursList(it))
        }
    }

    private fun initRecycleView(){
        adapter = WeatherAdapter(null)
        binding?.run {
            rcView.layoutManager = LinearLayoutManager(activity)
            rcView.adapter = adapter
        }

    }
    private fun getHoursList(wm: WeatherModel): List<WeatherModel>{
        val hoursArray = JSONArray(wm.hours)
        val list = ArrayList<WeatherModel>()
        for (i in 0 until hoursArray.length()) {
            val item = WeatherModel(
                city = wm.city,
                time = (hoursArray[i] as JSONObject).getString("time"),
                condition = (hoursArray[i] as JSONObject).getJSONObject("condition").getString("text"),
                imageUrl = (hoursArray[i] as JSONObject).getJSONObject("condition").getString("icon"),
                currentTemp = (hoursArray[i] as JSONObject).getString("temp_c"),
                maxTemp = "",
                minTemp = "",
                hours = ""
            )
            list.add(item)
        }
        return list
    }

    companion object {
        @JvmStatic
        fun newInstance() = HoursFragment()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}