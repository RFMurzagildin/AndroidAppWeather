package com.app.weatherapp.db.di

import android.content.Context
import androidx.room.Room
import com.app.weatherapp.db.InceptionDatabase
import com.app.weatherapp.db.migrations.Migration_1_2
import com.app.weatherapp.db.migrations.Migration_2_3
import com.app.weatherapp.db.repository.CityRepository

object ServiceLocator {
    private const val DATABASE_NAME = "InceptionDB"
    var dbInstance: InceptionDatabase? = null
    private var cityRepository: CityRepository? = null

    private fun initDatabase(ctx: Context) {
        dbInstance = Room.databaseBuilder(ctx, InceptionDatabase::class.java, DATABASE_NAME)
            .addMigrations(
                Migration_1_2(),
                Migration_2_3()
            )
            .build()
    }

    fun initDataLayerDependencies(ctx: Context) {
        if (dbInstance == null) {
            initDatabase(ctx)
            dbInstance?.let {
                cityRepository = CityRepository(it.cityDao)
            }
        }
    }

    fun getCityRepository(): CityRepository = cityRepository
        ?: throw IllegalStateException()

}