package com.example.spotter.database

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SpotDBModule {
	@Provides
	@Singleton
	fun provideDB(@ApplicationContext context: Context): SpotDB =
		Room.databaseBuilder(context, SpotDB::class.java, "spot.db").build()

	@Provides
	fun provideDao(db: SpotDB): SpotDao = db.spotDao()
}