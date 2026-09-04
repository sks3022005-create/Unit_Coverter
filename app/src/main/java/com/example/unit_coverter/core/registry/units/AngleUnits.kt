package com.example.unit_coverter.core.registry.units

import com.example.unit_coverter.core.math.ConversionFormula.Linear
import com.example.unit_coverter.core.math.MC
import com.example.unit_coverter.core.math.PI
import com.example.unit_coverter.core.math.bd
import com.example.unit_coverter.core.registry.UnitCategory
import com.example.unit_coverter.core.registry.UnitDef

// Base unit: radian
// π is pre-computed to 50 decimal places; all angle factors are exact rationals × π.
private val DEG = PI.divide(bd("180"), MC)
private val GRAD = PI.divide(bd("200"), MC)
private val TWO_PI = PI.multiply(bd("2"), MC)

internal val AngleCategory = UnitCategory(
    id = "angle",
    displayName = "Angle",
    baseUnitId = "radian",
    units = listOf(
        UnitDef("radian", "rad", "Radian",
            listOf("rad", "radian", "radians"),
            Linear(bd("1"))),
        UnitDef("degree", "°", "Degree",
            listOf("°", "deg", "degree", "degrees"),
            Linear(DEG)),
        UnitDef("gradian", "grad", "Gradian",
            listOf("grad", "gradian", "gradians", "gon", "gons"),
            Linear(GRAD)),
        UnitDef("arcminute", "′", "Arcminute",
            listOf("arcmin", "arcminute", "arcminutes", "arc minute", "minute of arc"),
            Linear(DEG.divide(bd("60"), MC))),
        UnitDef("arcsecond", "″", "Arcsecond",
            listOf("arcsec", "arcsecond", "arcseconds", "arc second", "second of arc"),
            Linear(DEG.divide(bd("3600"), MC))),
        UnitDef("turn", "τ", "Turn (Revolution)",
            listOf("turn", "turns", "revolution", "revolutions", "rev", "cycle"),
            Linear(TWO_PI)),
        UnitDef("milliradian", "mrad", "Milliradian",
            listOf("mrad", "milliradian", "milliradians"),
            Linear(bd("0.001"))),
    ),
)
