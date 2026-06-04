package com.example.unit_coverter.domain.model

import com.example.unit_coverter.core.registry.UnitCategory
import com.example.unit_coverter.core.registry.UnitDef
import java.math.BigDecimal

data class ConversionResult(
    val inputValue: BigDecimal,
    val outputValue: BigDecimal,
    val fromUnit: UnitDef,
    val toUnit: UnitDef,
    val category: UnitCategory,
)
