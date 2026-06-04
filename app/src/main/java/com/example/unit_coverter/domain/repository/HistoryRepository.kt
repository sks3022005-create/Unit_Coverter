package com.example.unit_coverter.domain.repository

import com.example.unit_coverter.domain.model.ConversionRecord
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun getRecent(limit: Int = 100): Flow<List<ConversionRecord>>
    fun count(): Flow<Int>
    suspend fun record(entry: ConversionRecord)
    suspend fun deleteAll()
    suspend fun deleteOlderThan(beforeMs: Long)
}
