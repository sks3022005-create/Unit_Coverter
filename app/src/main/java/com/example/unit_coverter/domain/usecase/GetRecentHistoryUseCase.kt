package com.example.unit_coverter.domain.usecase

import com.example.unit_coverter.domain.model.ConversionRecord
import com.example.unit_coverter.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecentHistoryUseCase @Inject constructor(
    private val historyRepository: HistoryRepository,
) {
    operator fun invoke(limit: Int = 100): Flow<List<ConversionRecord>> =
        historyRepository.getRecent(limit)
}
