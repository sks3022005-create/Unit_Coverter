package com.example.unit_coverter.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.unit_coverter.data.local.entity.HistoryEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: HistoryEntryEntity)

    @Query("SELECT * FROM history ORDER BY timestampMs DESC LIMIT :limit")
    fun getRecent(limit: Int): Flow<List<HistoryEntryEntity>>

    @Query("SELECT COUNT(*) FROM history")
    fun count(): Flow<Int>

    @Query("DELETE FROM history WHERE timestampMs < :beforeMs")
    suspend fun deleteOlderThan(beforeMs: Long)

    @Query("DELETE FROM history")
    suspend fun deleteAll()
}
