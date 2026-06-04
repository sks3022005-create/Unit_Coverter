package com.example.unit_coverter.domain.repository

import com.example.unit_coverter.domain.model.CustomUnitDef
import kotlinx.coroutines.flow.Flow

interface CustomUnitRepository {
    fun getAll(): Flow<List<CustomUnitDef>>
    fun getByCategory(categoryId: String): Flow<List<CustomUnitDef>>
    suspend fun add(unit: CustomUnitDef)
    suspend fun update(unit: CustomUnitDef)
    suspend fun remove(id: String)
}
