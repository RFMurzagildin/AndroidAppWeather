package com.app.weatherapp.db.migrations

import android.util.Log
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.app.weatherapp.db.InceptionDatabase

class Migration_1_2 : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        try {
            db.execSQL("ALTER TABLE 'cities' ADD COLUMN 'lat' TEXT NOT NULL DEFAULT ' '")
            db.execSQL("ALTER TABLE 'cities' ADD COLUMN 'lon' TEXT NOT NULL DEFAULT ' '")
            db.execSQL("ALTER TABLE 'cities' ADD COLUMN 'country' TEXT NOT NULL DEFAULT ' '")
            db.execSQL("ALTER TABLE 'cities' ADD COLUMN 'state' TEXT NOT NULL DEFAULT ' '")
        } catch (ex: Exception) {
            Log.e(InceptionDatabase.DB_LOG_KEY, "Error while 1_2 migration: ${ex.message}")
        }
    }
}