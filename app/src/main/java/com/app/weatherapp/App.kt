package com.app.weatherapp

import android.app.Application
import com.app.weatherapp.db.di.ServiceLocator

class App : Application() {

    private val serviceLocator = ServiceLocator

    override fun onCreate(){
        super.onCreate()
        serviceLocator.initDataLayerDependencies(ctx = this)
    }
}