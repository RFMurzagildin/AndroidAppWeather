package com.app.weatherapp.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cities")
data class CitiesEntity (
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    val cityName: String,
    @ColumnInfo(name = "lat")
    val lat: String,
    @ColumnInfo(name = "lon")
    val lon: String,
    @ColumnInfo(name = "country")
    val country: String,
    @ColumnInfo(name = "state")
    val state: String
)