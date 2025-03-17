package com.app.weatherapp.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.weatherapp.db.entities.CitiesEntity

@Dao
interface CityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(city: CitiesEntity)

    @Query("SELECT * FROM cities WHERE id = :id")
    suspend fun get(id: String): MutableList<CitiesEntity>?

    @Query("DELETE FROM cities WHERE id = :id")
    suspend fun delete(id: String)
}