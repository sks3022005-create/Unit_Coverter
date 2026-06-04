package com.example.unit_coverter.domain.usecase

import com.example.unit_coverter.core.math.ConversionFormula
import com.example.unit_coverter.core.nlp.NlpParser
import com.example.unit_coverter.core.registry.UnitDef
import com.example.unit_coverter.core.registry.UnitRegistry
import com.example.unit_coverter.domain.model.CustomUnitDef
import com.example.unit_coverter.domain.repository.CustomUnitRepository
import javax.inject.Inject

/**
 * Collects all custom units from Room and keeps [UnitRegistry] in sync.
 *
 * Should be launched as a long-lived coroutine from [UnitConverterApp.onCreate].
 * Each emission from the DB replaces the full custom-unit overlay atomically.
 */
class SyncCustomUnitsUseCase @Inject constructor(
    private val customUnitRepository: CustomUnitRepository,
) {
    suspend operator fun invoke() {
        customUnitRepository.getAll().collect { units ->
            val byCategory = units
                .groupBy { it.categoryId }
                .mapValues { (_, defs) -> defs.map { it.toUnitDef() } }
            UnitRegistry.setCustomUnits(byCategory)
            NlpParser.invalidateCache()
        }
    }

    private fun CustomUnitDef.toUnitDef() = UnitDef(
        id = id,
        symbol = symbol,
        displayName = displayName,
        aliases = aliases,
        formula = ConversionFormula.Linear(factor),
    )
}
