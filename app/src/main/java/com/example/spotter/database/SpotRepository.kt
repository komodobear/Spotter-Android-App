package com.example.spotter.database

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpotRepository @Inject constructor(
    private val spotDao: SpotDao
){
    suspend fun addSpot(spot: SpotData) = spotDao.addSpot(spot)

    suspend fun updateSpot(spot: SpotData) = spotDao.updateSpot(spot)

    suspend fun deleteSpot(spot: SpotData) = spotDao.deleteSpot(spot)

    fun getAllSpots(): Flow<List<SpotData>> = spotDao.getAllSpots()

    fun getSpotById(id: Long): Flow<SpotData> = spotDao.getSpotById(id)
}