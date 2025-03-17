package com.app.weatherapp.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.app.weatherapp.R
import com.app.weatherapp.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private var binding: FragmentSettingsBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSettingsBinding.bind(view)
    }

    companion object {
        @JvmStatic
        fun newInstance() = SettingsFragment()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}