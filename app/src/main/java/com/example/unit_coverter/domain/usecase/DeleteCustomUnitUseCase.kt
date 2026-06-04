package com.example.unit_coverter.domain.usecase

import com.example.unit_coverter.domain.repository.CustomUnitRepository
import javax.inject.Inject

class DeleteCustomUnitUseCase @Inject constructor(
    private val customUnitRepository: CustomUnitRepository,
) {
    suspend operator fun invoke(id: String) = customUnitRepository.remove(id)
}
