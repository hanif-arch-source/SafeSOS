package com.example.safesos.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface IncidentDao {
    @Insert
    suspend fun insert(incident: IncidentEntity): Long

    @Query("SELECT * FROM incidents WHERE ownerId = :ownerId ORDER BY timestamp DESC")
    fun observeForUser(ownerId: Int): Flow<List<IncidentEntity>>
}
