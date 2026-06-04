package com.example.unit_coverter.domain.usecase

import com.example.unit_coverter.core.registry.UnitRegistry
import com.example.unit_coverter.domain.model.CustomUnitDef
import com.example.unit_coverter.domain.repository.CustomUnitRepository
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject

class AddCustomUnitUseCase @Inject constructor(
    private val customUnitRepository: CustomUnitRepository,
) {
    suspend operator fun invoke(
        displayName: String,
        symbol: String,
        categoryId: String,
        baseUnitId: String,
        factorString: String,
        aliases: List<String> = emptyList(),
    ): Result<CustomUnitDef> {
        if (displayName.isBlank()) {
            return Result.failure(IllegalArgumentException("Unit name must not be blank"))
        }
        if (symbol.isBlank()) {
            return Result.failure(IllegalArgumentException("Symbol must not be blank"))
        }

        UnitRegistry.categoryById(categoryId)
            ?: return Result.failure(IllegalArgumentException("Unknown category: $categoryId"))

        UnitRegistry.findById(baseUnitId)
            ?: return Result.failure(IllegalArgumentException("Unknown base unit: $baseUnitId"))

        val factor = runCatching { BigDecimal(factorString.trim()) }
            .getOrElse { return Result.failure(IllegalArgumentException("Invalid factor: $factorString")) }

        if (factor.signum() <= 0) {
            return Result.failure(IllegalArgumentException("Factor must be a positive number"))
        }

        val unit = CustomUnitDef(
            id = "custom_${UUID.randomUUID()}",
            displayName = displayName.trim(),
            symbol = symbol.trim(),
            categoryId = categoryId,
            baseUnitId = baseUnitId,
            factor = factor,
            aliases = (aliases + listOf(displayName.trim().lowercase())).distinct(),
            createdAtMs = System.currentTimeMillis(),
        )

        return runCatching {
            customUnitRepository.add(unit)
            unit
        }
    }
}
