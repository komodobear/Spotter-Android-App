package com.example.spotter.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="spot-table")
data class SpotData(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "desc")
    val description: String,
    @ColumnInfo(name = "address")
    val address: String? = null,
    @ColumnInfo(name = "lat")
    val latitude: Double? = null,
    @ColumnInfo(name = "lng")
    val longitude: Double? = null,
    @ColumnInfo(name = "img")
    val imagePath: String? = null
)
