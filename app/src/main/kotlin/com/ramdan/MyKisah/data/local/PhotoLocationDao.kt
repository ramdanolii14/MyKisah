package com.ramdan.MyKisah.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoLocationDao {

    @Query("SELECT * FROM photo_locations ORDER BY timestamp ASC")
    fun getAllChronological(): Flow<List<PhotoLocationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(locations: List<PhotoLocationEntity>)

    @Query("DELETE FROM photo_locations")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM photo_locations")
    suspend fun count(): Int
}
