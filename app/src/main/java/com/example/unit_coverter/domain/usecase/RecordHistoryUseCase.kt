package com.example.unit_coverter.domain.usecase

import com.example.unit_coverter.domain.model.ConversionRecord
import com.example.unit_coverter.domain.model.ConversionResult
import com.example.unit_coverter.domain.repository.HistoryRepository
import javax.inject.Inject

class RecordHistoryUseCase @Inject constructor(
    private val historyRepository: HistoryRepository,
) {
    suspend operator fun invoke(result: ConversionResult) {
        historyRepository.record(
            ConversionRecord(
                inputValue = result.inputValue,
                resultValue = result.outputValue,
                fromUnitId = result.fromUnit.id,
                toUnitId = result.toUnit.id,
                categoryId = result.category.id,
                timestampMs = System.currentTimeMillis(),
            )
        )
    }
}
