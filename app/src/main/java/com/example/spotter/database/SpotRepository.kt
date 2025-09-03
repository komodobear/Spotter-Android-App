package com.example.spotter.database

import kotlinx.coroutines.flow.Flow

class SpotRepository(private val spotDao: SpotDao) {

    suspend fun addKot(kot: SpotData){
        spotDao.addSpot(kot)
    }

    suspend fun updateKot(kot: SpotData){
        spotDao.updateSpot(kot)
    }

    suspend fun deleteKot(kot: SpotData){
        spotDao.deleteSpot(kot)
    }

    fun getAllKotek(): Flow<List<SpotData>> = spotDao.getAllSpots()

    fun getKotekByID(id: Long): Flow<SpotData>{
        return spotDao.getSpotById(id)
    }

}