package com.example.unit_coverter.domain.usecase

import com.example.unit_coverter.domain.model.CustomUnitDef
import com.example.unit_coverter.domain.repository.CustomUnitRepository
import javax.inject.Inject

class UpdateCustomUnitUseCase @Inject constructor(
    private val customUnitRepository: CustomUnitRepository,
) {
    suspend operator fun invoke(unit: CustomUnitDef): Result<Unit> {
        if (unit.displayName.isBlank()) {
            return Result.failure(IllegalArgumentException("Unit name must not be blank"))
        }
        if (unit.factor.signum() <= 0) {
            return Result.failure(IllegalArgumentException("Factor must be positive"))
        }
        return runCatching { customUnitRepository.update(unit) }
    }
}
