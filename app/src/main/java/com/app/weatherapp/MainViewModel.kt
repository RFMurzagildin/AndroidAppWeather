package com.app.weatherapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.weatherapp.models.WeatherModel

class MainViewModel : ViewModel() {
    val liveDataCurrentCity = MutableLiveData<WeatherModel>()
    val liveDataCurrentSelection = MutableLiveData<WeatherModel>()
    val liveDataList = MutableLiveData<List<WeatherModel>>()
}