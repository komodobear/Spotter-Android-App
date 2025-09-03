package com.example.spotter.database

import android.content.Context
import androidx.room.Room

object Graph {
	lateinit var database: SpotDB

	val SpotRepository by lazy {
		SpotRepository(spotDao = database.spotDao())
	}

	fun provide(context: Context) {
		database = Room
			.databaseBuilder(context, SpotDB::class.java, "spot.db").build()
	}
}