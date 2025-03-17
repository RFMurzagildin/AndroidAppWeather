package com.app.weatherapp.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cities")
data class CitiesEntity (
    @PrimaryKey
    val id: String,
    @ColumnInfo
    val cityName: String
)