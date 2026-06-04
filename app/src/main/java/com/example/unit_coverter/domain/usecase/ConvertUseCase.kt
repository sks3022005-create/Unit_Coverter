package com.example.unit_coverter.domain.usecase

import com.example.unit_coverter.core.registry.UnitRegistry
import com.example.unit_coverter.domain.model.ConversionResult
import java.math.BigDecimal
import javax.inject.Inject

class ConvertUseCase @Inject constructor() {

    operator fun invoke(
        value: BigDecimal,
        fromUnitId: String,
        toUnitId: String,
    ): Result<ConversionResult> {
        val (fromCat, fromUnit) = UnitRegistry.findById(fromUnitId)
            ?: return Result.failure(IllegalArgumentException("Unknown unit: $fromUnitId"))
        val (toCat, toUnit) = UnitRegistry.findById(toUnitId)
            ?: return Result.failure(IllegalArgumentException("Unknown unit: $toUnitId"))

        if (fromCat.id != toCat.id) {
            return Result.failure(
                IllegalArgumentException(
                    "Cannot convert '${fromUnit.displayName}' (${fromCat.displayName}) " +
                    "to '${toUnit.displayName}' (${toCat.displayName})"
                )
            )
        }

        return runCatching {
            val output = UnitRegistry.convert(value, fromUnit, toUnit)
            ConversionResult(value, output, fromUnit, toUnit, fromCat)
        }
    }
}
