package com.app.weatherapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.weatherapp.databinding.ActivityMainBinding
import com.app.weatherapp.fragments.MainFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragment_container, MainFragment.newInstance())
            .commit()
    }
}

