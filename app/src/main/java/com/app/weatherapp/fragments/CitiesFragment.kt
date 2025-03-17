package com.app.weatherapp.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.app.weatherapp.R
import com.app.weatherapp.databinding.FragmentCitiesBinding

class CitiesFragment : Fragment(R.layout.fragment_cities) {

    private var binding: FragmentCitiesBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCitiesBinding.bind(view)
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