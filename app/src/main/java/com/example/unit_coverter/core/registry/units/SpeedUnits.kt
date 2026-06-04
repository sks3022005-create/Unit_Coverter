package com.example.unit_coverter.core.registry.units

import com.example.unit_coverter.core.math.ConversionFormula.Linear
import com.example.unit_coverter.core.math.MC
import com.example.unit_coverter.core.math.bd
import com.example.unit_coverter.core.registry.UnitCategory
import com.example.unit_coverter.core.registry.UnitDef

// Base unit: meter per second
// Mach number (ISA sea level, 15 °C): ~340.29 m/s.
// This varies with altitude/temperature and is inherently approximate.
internal val SpeedCategory = UnitCategory(
    id = "speed",
    displayName = "Speed",
    baseUnitId = "meter_per_second",
    units = listOf(
        UnitDef("meter_per_second", "m/s", "Meter per Second",
            listOf("m/s", "mps", "meter per second", "meters per second"),
            Linear(bd("1"))),
        UnitDef("kilometer_per_hour", "km/h", "Kilometer per Hour",
            listOf("km/h", "kmh", "kph", "kilometer per hour", "kilometers per hour"),
            Linear(bd("5").divide(bd("18"), MC))),   // 1000/3600 = 5/18 (exact rational)
        UnitDef("mile_per_hour", "mph", "Mile per Hour",
            listOf("mph", "mile per hour", "miles per hour"),
            Linear(bd("0.44704"))),                  // 1609.344/3600 (exact)
        UnitDef("foot_per_second", "ft/s", "Foot per Second",
            listOf("ft/s", "fps", "foot per second", "feet per second"),
            Linear(bd("0.3048"))),
        UnitDef("knot", "kn", "Knot",
            listOf("kn", "kt", "knot", "knots", "nautical mile per hour"),
            Linear(bd("1852").divide(bd("3600"), MC))), // 1 nmi/h = 1852/3600 m/s (exact)
        UnitDef("mach", "Ma", "Mach (ISA, sea level)",
            listOf("ma", "mach", "mach number"),
            Linear(bd("340.29"))),                   // approx. at 15 °C, 1 atm
        UnitDef("speed_of_light", "c", "Speed of Light",
            listOf("c", "speed of light", "lightspeed"),
            Linear(bd("299792458"))),                // exact (SI definition)
    ),
)
