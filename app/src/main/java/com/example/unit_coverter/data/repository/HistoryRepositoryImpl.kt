package com.example.unit_coverter.data.repository

import com.example.unit_coverter.data.local.db.HistoryDao
import com.example.unit_coverter.data.local.entity.HistoryEntryEntity
import com.example.unit_coverter.domain.model.ConversionRecord
import com.example.unit_coverter.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor(
    private val dao: HistoryDao,
) : HistoryRepository {

    override fun getRecent(limit: Int): Flow<List<ConversionRecord>> =
        dao.getRecent(limit).map { list -> list.map { it.toDomain() } }

    override fun count(): Flow<Int> = dao.count()

    override suspend fun record(entry: ConversionRecord) = dao.insert(entry.toEntity())

    override suspend fun deleteAll() = dao.deleteAll()

    override suspend fun deleteOlderThan(beforeMs: Long) = dao.deleteOlderThan(beforeMs)
}

private fun HistoryEntryEntity.toDomain() = ConversionRecord(
    id = id,
    inputValue = BigDecimal(inputValue),
    resultValue = BigDecimal(resultValue),
    fromUnitId = fromUnitId,
    toUnitId = toUnitId,
    categoryId = categoryId,
    timestampMs = timestampMs,
)

private fun ConversionRecord.toEntity() = HistoryEntryEntity(
    id = id,
    inputValue = inputValue.toPlainString(),
    resultValue = resultValue.toPlainString(),
    fromUnitId = fromUnitId,
    toUnitId = toUnitId,
    categoryId = categoryId,
    timestampMs = timestampMs,
)
