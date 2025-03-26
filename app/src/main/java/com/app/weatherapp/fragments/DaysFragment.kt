package com.app.weatherapp.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.weatherapp.MainViewModel
import com.app.weatherapp.R
import com.app.weatherapp.adapters.daysAndHoursAdapter.WeatherAdapter
import com.app.weatherapp.databinding.FragmentDaysBinding
import com.app.weatherapp.models.WeatherModel

class DaysFragment : Fragment(R.layout.fragment_days), WeatherAdapter.Listener {

    private var binding: FragmentDaysBinding? = null
    private var adapter: WeatherAdapter? = null
    private val model: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDaysBinding.bind(view)
        initRecycleView()
        model.liveDataList.observe(viewLifecycleOwner) {
            adapter?.submitList(it)
        }
    }

    private fun initRecycleView() {
        adapter = WeatherAdapter(this@DaysFragment)
        binding?.run {
            rcView.layoutManager = LinearLayoutManager(activity)
            rcView.adapter = adapter
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = DaysFragment()
    }

    override fun onClick(item: WeatherModel) {
        model.liveDataCurrentSelection.value = item
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}