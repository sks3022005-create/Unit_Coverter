package com.example.unit_coverter.core.registry.units

import com.example.unit_coverter.core.math.ConversionFormula.Linear
import com.example.unit_coverter.core.math.ConversionFormula.Reciprocal
import com.example.unit_coverter.core.math.MC
import com.example.unit_coverter.core.math.MILES_PER_L_FACTOR
import com.example.unit_coverter.core.math.MPG_UK_FACTOR
import com.example.unit_coverter.core.math.MPG_US_FACTOR
import com.example.unit_coverter.core.math.bd
import com.example.unit_coverter.core.registry.UnitCategory
import com.example.unit_coverter.core.registry.UnitDef

// Base unit: liters per 100 km (L/100km)
//
// L/100km and MPG are RECIPROCAL — a Reciprocal formula is used so that
// both toBase and fromBase apply the same factor/value inversion.
//
//   mpg_us = 235.2145… / (L/100km)   →  Reciprocal(235.2145…)
//   mpg_uk = 282.4809… / (L/100km)   →  Reciprocal(282.4809…)
//   km/L   = 100 / (L/100km)         →  Reciprocal(100)
//   miles/L = 62.1371… / (L/100km)  →  Reciprocal(62.1371…)
//   L/km   = L/100km / 100           →  Linear(100) [toBase: L/km × 100 = L/100km]
internal val FuelConsumptionCategory = UnitCategory(
    id = "fuel_consumption",
    displayName = "Fuel Consumption",
    baseUnitId = "l_per_100km",
    units = listOf(
        UnitDef("l_per_100km", "L/100km", "Liters per 100 km",
            listOf("l/100km", "lper100km", "liters per 100km", "l per 100 km"),
            Linear(bd("1"))),
        UnitDef("l_per_km", "L/km", "Liters per km",
            listOf("l/km", "liters per km"),
            Linear(bd("100"))),                 // toBase: L/km × 100 = L/100km
        UnitDef("km_per_l", "km/L", "Kilometers per Liter",
            listOf("km/l", "kilometers per liter", "km per l"),
            Reciprocal(bd("100"))),
        UnitDef("mpg_us", "mpg", "Miles per Gallon (US)",
            listOf("mpg", "mpg us", "miles per gallon", "miles per gallon us"),
            Reciprocal(MPG_US_FACTOR)),         // 100 × 3.785411784 / 1.609344 ≈ 235.2146
        UnitDef("mpg_uk", "mpg (UK)", "Miles per Gallon (Imperial)",
            listOf("mpg uk", "mpg imperial", "miles per gallon uk", "imperial mpg"),
            Reciprocal(MPG_UK_FACTOR)),         // 100 × 4.54609 / 1.609344 ≈ 282.4809
        UnitDef("miles_per_l", "mi/L", "Miles per Liter",
            listOf("mi/l", "miles per liter", "miles per litre"),
            Reciprocal(MILES_PER_L_FACTOR)),    // 100 / 1.609344 ≈ 62.1371
    ),
)
