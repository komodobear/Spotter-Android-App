package com.example.spotter.location

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {
	@Provides
	@Singleton
	fun provideRetrofit(): Retrofit =
		Retrofit.Builder()
			.baseUrl("https://maps.googleapis.com/")
			.addConverterFactory(GsonConverterFactory.create())
			.build()

	@Provides
	@Singleton
	fun provideGeoCodingApiService(retrofit: Retrofit): GeoCodingApiService =
		retrofit.create(GeoCodingApiService::class.java)
}