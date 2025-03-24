package com.app.weatherapp.db.repository

import com.app.weatherapp.db.dao.CityDao
import com.app.weatherapp.db.entities.CitiesEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CityRepository(private val cityDao: CityDao) {
    suspend fun insert(city: CitiesEntity) {
        withContext(Dispatchers.IO) {
            cityDao.insert(city)
        }
    }

    suspend fun get(id: String): MutableList<CitiesEntity>? {
        return withContext(Dispatchers.IO) {
            cityDao.get(id)
        }
    }

    suspend fun delete(cityName: String) {
        withContext(Dispatchers.IO) {
            cityDao.delete(cityName)
        }
    }

    suspend fun getAllCities(): MutableList<CitiesEntity> {
        return withContext(Dispatchers.IO) {
            cityDao.getAllCities()
        }
    }

}