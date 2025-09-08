package com.example.spotter.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [SpotData::class],
    version = 1,
    exportSchema = false
)
abstract class SpotDB: RoomDatabase() {
    abstract fun spotDao(): SpotDao
}