package com.example.unit_coverter.domain.usecase

import com.example.unit_coverter.domain.model.CustomUnitDef
import com.example.unit_coverter.domain.repository.CustomUnitRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCustomUnitsUseCase @Inject constructor(
    private val customUnitRepository: CustomUnitRepository,
) {
    operator fun invoke(): Flow<List<CustomUnitDef>> = customUnitRepository.getAll()

    fun byCategory(categoryId: String): Flow<List<CustomUnitDef>> =
        customUnitRepository.getByCategory(categoryId)
}
