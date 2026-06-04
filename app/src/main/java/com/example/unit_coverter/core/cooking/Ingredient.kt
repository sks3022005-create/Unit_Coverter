package com.example.unit_coverter.core.cooking

import java.math.BigDecimal

/**
 * A cooking ingredient with a known density used for volume ↔ mass conversions.
 * [densityGPerMl] is sourced from USDA/culinary references (g/mL at room temperature).
 */
data class Ingredient(
    val id: String,
    val displayName: String,
    val densityGPerMl: BigDecimal,
    val aliases: List<String> = emptyList(),
)
