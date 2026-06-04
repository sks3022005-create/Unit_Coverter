package com.example.unit_coverter.core.nlp

import java.math.BigDecimal

/**
 * The result of a successful natural-language parse.
 *
 * [numericValue] is already expressed in [fromUnitId]'s units.
 * For compound inputs like "5 ft 11 in", [fromUnitId] is the category's base unit
 * and [numericValue] is the pre-summed base value.
 */
data class NlpResult(
    val numericValue: BigDecimal,
    val fromUnitId: String,
    val categoryId: String,
    val toUnitId: String? = null,
    val originalInput: String = "",
)
