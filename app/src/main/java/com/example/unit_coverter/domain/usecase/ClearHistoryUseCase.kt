package com.example.unit_coverter.domain.usecase

import com.example.unit_coverter.domain.repository.HistoryRepository
import javax.inject.Inject

class ClearHistoryUseCase @Inject constructor(
    private val historyRepository: HistoryRepository,
) {
    suspend operator fun invoke() = historyRepository.deleteAll()
}
