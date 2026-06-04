package com.example.unit_coverter.core.registry.units

import com.example.unit_coverter.core.math.ConversionFormula.Linear
import com.example.unit_coverter.core.math.GRAVITY_STANDARD
import com.example.unit_coverter.core.math.LBF_N
import com.example.unit_coverter.core.math.MC
import com.example.unit_coverter.core.math.bd
import com.example.unit_coverter.core.registry.UnitCategory
import com.example.unit_coverter.core.registry.UnitDef

// Base unit: newton
internal val ForceCategory = UnitCategory(
    id = "force",
    displayName = "Force",
    baseUnitId = "newton",
    units = listOf(
        UnitDef("newton", "N", "Newton",
            listOf("n", "newton", "newtons"),
            Linear(bd("1"))),
        UnitDef("kilonewton", "kN", "Kilonewton",
            listOf("kn", "kilonewton", "kilonewtons"),
            Linear(bd("1000"))),
        UnitDef("meganewton", "MN", "Meganewton",
            listOf("mn", "meganewton", "meganewtons"),
            Linear(bd("1000000"))),
        UnitDef("dyne", "dyn", "Dyne",
            listOf("dyn", "dyne", "dynes"),
            Linear(bd("0.00001"))),              // 10⁻⁵ N (exact)
        UnitDef("pound_force", "lbf", "Pound-Force",
            listOf("lbf", "pound force", "pound-force", "lbs force"),
            Linear(LBF_N)),                     // 0.45359237 × 9.80665 N (exact)
        UnitDef("ounce_force", "ozf", "Ounce-Force",
            listOf("ozf", "ounce force", "ounce-force"),
            Linear(LBF_N.divide(bd("16"), MC))),
        UnitDef("kilogram_force", "kgf", "Kilogram-Force",
            listOf("kgf", "kilogram force", "kilogram-force", "kp", "kilopond"),
            Linear(GRAVITY_STANDARD)),          // 9.80665 N (exact)
        UnitDef("gram_force", "gf", "Gram-Force",
            listOf("gf", "gram force"),
            Linear(bd("0.00980665"))),
        UnitDef("short_ton_force", "tonf", "Short Ton-Force (US)",
            listOf("tonf", "ton force", "us ton force"),
            Linear(LBF_N.multiply(bd("2000"), MC))),
        UnitDef("metric_ton_force", "tf", "Metric Ton-Force",
            listOf("tf", "metric ton force", "tonne force"),
            Linear(bd("9806.65"))),             // 1000 kgf
    ),
)
