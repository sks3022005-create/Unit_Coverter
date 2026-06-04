package com.example.unit_coverter.core.registry

import com.example.unit_coverter.core.math.ConversionFormula

/**
 * A single measurable unit within a category.
 *
 * @param id       Stable machine identifier — never changes once shipped (used in DB, deeplinks).
 * @param symbol   Display symbol shown in the UI (e.g. "km", "°C", "fl oz").
 * @param displayName Human-readable singular name.
 * @param aliases  All strings the NLP parser and search will match against (lowercase).
 * @param formula  How this unit converts to/from the category's base unit.
 */
data class UnitDef(
    val id: String,
    val symbol: String,
    val displayName: String,
    val aliases: List<String>,
    val formula: ConversionFormula,
    val isPremium: Boolean = false,
)
