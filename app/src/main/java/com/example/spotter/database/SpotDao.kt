package com.example.spotter.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SpotDao {

	@Insert(onConflict = OnConflictStrategy.IGNORE)
	abstract suspend fun addSpot(spot: SpotData)

	@Delete
	abstract suspend fun deleteSpot(spot: SpotData)

	@Update
	abstract suspend fun updateSpot(spot: SpotData)

	@Query("Select * from `spot-table`")
	abstract fun getAllSpots(): Flow<List<SpotData>>

	@Query("Select * from `spot-table` where id=:id")
	abstract fun getSpotById(id: Long): Flow<SpotData>

}