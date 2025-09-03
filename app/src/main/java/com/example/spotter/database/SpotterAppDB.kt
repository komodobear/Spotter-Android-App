package com.example.spotter.database

import android.app.Application

class SpotterAppDB: Application() {
	override fun onCreate() {
		super.onCreate()
		Graph.provide(this)
	}
}