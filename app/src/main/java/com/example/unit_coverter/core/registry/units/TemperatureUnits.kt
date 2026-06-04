package com.example.unit_coverter.core.registry.units

import com.example.unit_coverter.core.math.ConversionFormula.Affine
import com.example.unit_coverter.core.math.ConversionFormula.Linear
import com.example.unit_coverter.core.math.MC
import com.example.unit_coverter.core.math.bd
import com.example.unit_coverter.core.registry.UnitCategory
import com.example.unit_coverter.core.registry.UnitDef
import java.math.BigDecimal

// Base unit: kelvin
// Affine formula: toBase(value) = value × factor + offset
//   Celsius:     K = C × 1     + 273.15          → factor=1,   offset=273.15
//   Fahrenheit:  K = F × 5/9   + 45967/180        → factor=5/9, offset≈255.3722
//   Rankine:     K = R × 5/9   + 0                → factor=5/9, offset=0

private val FIVE_NINTHS = bd("5").divide(bd("9"), MC)
private val F_OFFSET = bd("45967").divide(bd("180"), MC) // = 273.15 − (32 × 5/9) exact

internal val TemperatureCategory = UnitCategory(
    id = "temperature",
    displayName = "Temperature",
    baseUnitId = "kelvin",
    units = listOf(
        UnitDef("kelvin", "K", "Kelvin",
            listOf("k", "kelvin", "kelvins"),
            Linear(BigDecimal.ONE)),
        UnitDef("celsius", "°C", "Celsius",
            listOf("c", "°c", "celsius", "centigrade"),
            Affine(BigDecimal.ONE, bd("273.15"))),
        UnitDef("fahrenheit", "°F", "Fahrenheit",
            listOf("f", "°f", "fahrenheit"),
            Affine(FIVE_NINTHS, F_OFFSET)),
        UnitDef("rankine", "°R", "Rankine",
            listOf("r", "°r", "rankine"),
            Affine(FIVE_NINTHS, BigDecimal.ZERO)),
    ),
)
