package com.example.unit_coverter.core.registry.units

import com.example.unit_coverter.core.math.ATM_PA
import com.example.unit_coverter.core.math.ConversionFormula.Linear
import com.example.unit_coverter.core.math.MC
import com.example.unit_coverter.core.math.PSI_PA
import com.example.unit_coverter.core.math.bd
import com.example.unit_coverter.core.registry.UnitCategory
import com.example.unit_coverter.core.registry.UnitDef

// Base unit: pascal
// Torr = 101325/760 Pa (exact rational).
// mmHg = Torr by convention (plan decision).
// inHg = 25.4 × Torr (consistent derivation).
private val TORR_PA = ATM_PA.divide(bd("760"), MC)
private val IN_HG_PA = TORR_PA.multiply(bd("25.4"), MC)

internal val PressureCategory = UnitCategory(
    id = "pressure",
    displayName = "Pressure",
    baseUnitId = "pascal",
    units = listOf(
        UnitDef("pascal", "Pa", "Pascal",
            listOf("pa", "pascal", "pascals"),
            Linear(bd("1"))),
        UnitDef("kilopascal", "kPa", "Kilopascal",
            listOf("kpa", "kilopascal", "kilopascals"),
            Linear(bd("1000"))),
        UnitDef("megapascal", "MPa", "Megapascal",
            listOf("mpa", "megapascal", "megapascals"),
            Linear(bd("1000000"))),
        UnitDef("hectopascal", "hPa", "Hectopascal",
            listOf("hpa", "hectopascal", "hectopascals"),
            Linear(bd("100"))),
        UnitDef("bar", "bar", "Bar",
            listOf("bar", "bars"),
            Linear(bd("100000"))),              // exact
        UnitDef("millibar", "mbar", "Millibar",
            listOf("mbar", "millibar", "millibars"),
            Linear(bd("100"))),
        UnitDef("atmosphere", "atm", "Atmosphere",
            listOf("atm", "atmosphere", "atmospheres", "standard atmosphere"),
            Linear(ATM_PA)),                    // 101325 Pa (exact)
        UnitDef("torr", "Torr", "Torr",
            listOf("torr", "torrs"),
            Linear(TORR_PA)),                   // 101325/760 Pa (exact rational)
        UnitDef("mmhg", "mmHg", "Millimeter of Mercury",
            listOf("mmhg", "mm hg", "mmhg", "millimeter of mercury"),
            Linear(TORR_PA)),                   // treated as Torr (plan decision)
        UnitDef("psi", "psi", "Pound per Square Inch",
            listOf("psi", "pound per square inch", "lbf/in2"),
            Linear(PSI_PA)),                    // lbf / in² (exact derivation)
        UnitDef("ksi", "ksi", "Kilopound per Square Inch",
            listOf("ksi", "kilopsi"),
            Linear(PSI_PA.multiply(bd("1000"), MC))),
        UnitDef("inhg", "inHg", "Inch of Mercury",
            listOf("inhg", "in hg", "inch of mercury", "inches of mercury"),
            Linear(IN_HG_PA)),                  // 25.4 × Torr
    ),
)
