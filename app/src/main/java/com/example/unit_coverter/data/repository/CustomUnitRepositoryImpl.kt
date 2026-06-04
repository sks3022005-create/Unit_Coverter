package com.example.unit_coverter.data.repository

import com.example.unit_coverter.data.local.db.CustomUnitDao
import com.example.unit_coverter.data.local.entity.CustomUnitEntity
import com.example.unit_coverter.domain.model.CustomUnitDef
import com.example.unit_coverter.domain.repository.CustomUnitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import javax.inject.Inject

class CustomUnitRepositoryImpl @Inject constructor(
    private val dao: CustomUnitDao,
) : CustomUnitRepository {

    override fun getAll(): Flow<List<CustomUnitDef>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override fun getByCategory(categoryId: String): Flow<List<CustomUnitDef>> =
        dao.getByCategory(categoryId).map { list -> list.map { it.toDomain() } }

    override suspend fun add(unit: CustomUnitDef) = dao.insert(unit.toEntity())

    override suspend fun update(unit: CustomUnitDef) = dao.update(unit.toEntity())

    override suspend fun remove(id: String) = dao.deleteById(id)
}

private fun CustomUnitEntity.toDomain() = CustomUnitDef(
    id = id,
    displayName = displayName,
    symbol = symbol,
    categoryId = categoryId,
    baseUnitId = baseUnitId,
    factor = BigDecimal(factorString),
    aliases = aliasesPiped.split("|").filter { it.isNotBlank() },
    createdAtMs = createdAtMs,
)

private fun CustomUnitDef.toEntity() = CustomUnitEntity(
    id = id,
    displayName = displayName,
    symbol = symbol,
    categoryId = categoryId,
    baseUnitId = baseUnitId,
    factorString = factor.toPlainString(),
    aliasesPiped = aliases.joinToString("|"),
    createdAtMs = createdAtMs,
)
