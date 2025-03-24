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
            toggleGroupTemperature.addOnButtonCheckedListener { _, checkedId, isChecked ->
                if(isChecked){
                    when(checkedId){
                        R.id.buttonCelsius -> {
                            println("Выбраны градусы Цельсия")
                        }
                        R.id.buttonFahrenheit -> {
                            println("Выбраны градусы Фаренгейта")
                        }
                    }
                }
            }
            toggleGroupDecoration.addOnButtonCheckedListener{ _, checkedId, isChecked ->
                if(isChecked){
                    when(checkedId){
                        R.id.buttonLight -> {
                            println("Выбрана светлая тема")
                        }
                        R.id.buttonDark -> {
                            println("Выбрана темная тема")
                        }
                    }
                }

            }
        }
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