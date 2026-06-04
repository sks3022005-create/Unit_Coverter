package com.example.unit_coverter.core.cooking

import com.example.unit_coverter.core.math.MC
import com.example.unit_coverter.core.registry.UnitDef
import com.example.unit_coverter.core.registry.UnitRegistry
import java.math.BigDecimal

/**
 * Converts between mass and volume units using an ingredient's density.
 *
 * Cooking units supported by the UI — IDs must exist in [UnitRegistry].
 */
object CookingConverter {

    // ── Available cooking units ───────────────────────────────────────────────

    val MASS_UNIT_IDS = listOf("gram", "kilogram", "ounce", "pound")
    val VOLUME_UNIT_IDS = listOf(
        "us_teaspoon", "us_tablespoon", "us_fluid_oz",
        "us_cup", "us_pint", "milliliter", "liter",
    )
    val ALL_UNIT_IDS = MASS_UNIT_IDS + VOLUME_UNIT_IDS

    // ── Conversion ────────────────────────────────────────────────────────────

    /**
     * Converts [value] in [fromUnit] to [toUnit] using [ingredient]'s density for
     * any cross-dimensional (mass ↔ volume) step.
     */
    fun convert(
        value: BigDecimal,
        fromUnit: UnitDef,
        toUnit: UnitDef,
        ingredient: Ingredient,
    ): Result<BigDecimal> = runCatching {
        val fromCatId = UnitRegistry.findById(fromUnit.id)?.first?.id
            ?: error("Unknown unit: ${fromUnit.id}")
        val toCatId = UnitRegistry.findById(toUnit.id)?.first?.id
            ?: error("Unknown unit: ${toUnit.id}")

        when {
            fromCatId == "mass" && toCatId == "volume" -> {
                // mass → grams → mL → toUnit
                val grams = toGrams(value, fromUnit)
                val ml = grams.divide(ingredient.densityGPerMl, MC)
                fromMl(ml, toUnit)
            }
            fromCatId == "volume" && toCatId == "mass" -> {
                // volume → mL → grams → toUnit
                val ml = toMl(value, fromUnit)
                val grams = ml.multiply(ingredient.densityGPerMl, MC)
                fromGrams(grams, toUnit)
            }
            else -> {
                // Same dimension — normal registry conversion
                UnitRegistry.convert(value, fromUnit, toUnit)
            }
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun toGrams(value: BigDecimal, from: UnitDef): BigDecimal {
        val gram = requireUnit("gram")
        return UnitRegistry.convert(value, from, gram)
    }

    private fun fromGrams(grams: BigDecimal, to: UnitDef): BigDecimal {
        val gram = requireUnit("gram")
        return UnitRegistry.convert(grams, gram, to)
    }

    private fun toMl(value: BigDecimal, from: UnitDef): BigDecimal {
        val ml = requireUnit("milliliter")
        return UnitRegistry.convert(value, from, ml)
    }

    private fun fromMl(ml: BigDecimal, to: UnitDef): BigDecimal {
        val mlUnit = requireUnit("milliliter")
        return UnitRegistry.convert(ml, mlUnit, to)
    }

    private fun requireUnit(id: String): UnitDef =
        UnitRegistry.findById(id)?.second ?: error("Required cooking unit not found: $id")
}
