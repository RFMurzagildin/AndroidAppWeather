package com.app.weatherapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.weatherapp.db.dao.CityDao
import com.app.weatherapp.db.entities.CitiesEntity

@Database(entities = [CitiesEntity::class], version = 3)
abstract class InceptionDatabase : RoomDatabase(){

    abstract val cityDao: CityDao

    companion object{
        const val DB_LOG_KEY = "InceptionDB"
    }
}