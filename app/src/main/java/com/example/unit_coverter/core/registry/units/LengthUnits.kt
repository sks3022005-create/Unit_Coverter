package com.example.unit_coverter.core.registry.units

import com.example.unit_coverter.core.math.ConversionFormula.Linear
import com.example.unit_coverter.core.math.bd
import com.example.unit_coverter.core.registry.UnitCategory
import com.example.unit_coverter.core.registry.UnitDef

// Base unit: meter
internal val LengthCategory = UnitCategory(
    id = "length",
    displayName = "Length",
    baseUnitId = "meter",
    units = listOf(
        UnitDef("meter", "m", "Meter",
            listOf("m", "meter", "meters", "metre", "metres"),
            Linear(bd("1"))),
        UnitDef("kilometer", "km", "Kilometer",
            listOf("km", "kilometer", "kilometers", "kilometre", "kilometres", "klick", "klicks"),
            Linear(bd("1000"))),
        UnitDef("centimeter", "cm", "Centimeter",
            listOf("cm", "centimeter", "centimeters", "centimetre", "centimetres"),
            Linear(bd("0.01"))),
        UnitDef("millimeter", "mm", "Millimeter",
            listOf("mm", "millimeter", "millimeters", "millimetre", "millimetres"),
            Linear(bd("0.001"))),
        UnitDef("micrometer", "µm", "Micrometer",
            listOf("um", "µm", "micrometer", "micrometers", "micrometre", "micron", "microns"),
            Linear(bd("0.000001"))),
        UnitDef("nanometer", "nm", "Nanometer",
            listOf("nm", "nanometer", "nanometers", "nanometre"),
            Linear(bd("0.000000001"))),
        UnitDef("mile", "mi", "Mile",
            listOf("mi", "mile", "miles", "statute mile"),
            Linear(bd("1609.344"))),
        UnitDef("yard", "yd", "Yard",
            listOf("yd", "yard", "yards"),
            Linear(bd("0.9144"))),
        UnitDef("foot", "ft", "Foot",
            listOf("ft", "foot", "feet", "'"),
            Linear(bd("0.3048"))),
        UnitDef("inch", "in", "Inch",
            listOf("in", "inch", "inches", "\""),
            Linear(bd("0.0254"))),
        UnitDef("nautical_mile", "nmi", "Nautical Mile",
            listOf("nmi", "nm", "nautical mile", "nautical miles", "knot mile"),
            Linear(bd("1852"))),
        // --- astronomical ---
        UnitDef("light_year", "ly", "Light-Year",
            listOf("ly", "light year", "light years", "light-year"),
            Linear(bd("9460730472580800"))),   // exact: 1 ly = 9,460,730,472,580,800 m (IAU)
        UnitDef("astronomical_unit", "AU", "Astronomical Unit",
            listOf("au", "astronomical unit", "astronomical units"),
            Linear(bd("149597870700"))),        // exact: IAU 2012
        // --- traditional ---
        UnitDef("furlong", "fur", "Furlong",
            listOf("furlong", "furlongs"),
            Linear(bd("201.168"))),             // 660 ft
        UnitDef("chain", "ch", "Chain",
            listOf("chain", "chains"),
            Linear(bd("20.1168"))),             // 66 ft
        UnitDef("fathom", "ftm", "Fathom",
            listOf("fathom", "fathoms"),
            Linear(bd("1.8288"))),              // 6 ft
    ),
)
