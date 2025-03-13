package com.app.weatherapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.weatherapp.MainViewModel
import com.app.weatherapp.R
import com.app.weatherapp.adapters.WeatherAdapter
import com.app.weatherapp.adapters.WeatherModel
import com.app.weatherapp.databinding.FragmentDaysBinding

class DaysFragment : Fragment(R.layout.fragment_days), WeatherAdapter.Listener {

    private var binding: FragmentDaysBinding? = null
    private var adapter: WeatherAdapter? = null
    private val model: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDaysBinding.bind(view)
        initRecycleView()
        model.liveDataList.observe(viewLifecycleOwner){
            adapter?.submitList(it)
        }
    }

    private fun initRecycleView(){
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
        model.liveDataCurrent.value = item
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}