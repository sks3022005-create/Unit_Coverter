package com.example.unit_coverter.core.registry.units

import com.example.unit_coverter.core.math.ConversionFormula.Linear
import com.example.unit_coverter.core.math.bd
import com.example.unit_coverter.core.registry.UnitCategory
import com.example.unit_coverter.core.registry.UnitDef

// Base unit: square meter
// All factors are squares of the corresponding length factors.
internal val AreaCategory = UnitCategory(
    id = "area",
    displayName = "Area",
    baseUnitId = "sq_meter",
    units = listOf(
        UnitDef("sq_meter", "m²", "Square Meter",
            listOf("m2", "m²", "square meter", "square meters", "sq m"),
            Linear(bd("1"))),
        UnitDef("sq_kilometer", "km²", "Square Kilometer",
            listOf("km2", "km²", "square kilometer", "square km", "sq km"),
            Linear(bd("1000000"))),
        UnitDef("sq_centimeter", "cm²", "Square Centimeter",
            listOf("cm2", "cm²", "square centimeter", "sq cm"),
            Linear(bd("0.0001"))),
        UnitDef("sq_millimeter", "mm²", "Square Millimeter",
            listOf("mm2", "mm²", "square millimeter", "sq mm"),
            Linear(bd("0.000001"))),
        UnitDef("hectare", "ha", "Hectare",
            listOf("ha", "hectare", "hectares"),
            Linear(bd("10000"))),               // 100 m × 100 m
        UnitDef("are", "a", "Are",
            listOf("are", "ares"),
            Linear(bd("100"))),                 // 10 m × 10 m
        UnitDef("acre", "ac", "Acre",
            listOf("ac", "acre", "acres"),
            Linear(bd("4046.8564224"))),         // 43560 ft² (exact)
        UnitDef("sq_mile", "mi²", "Square Mile",
            listOf("mi2", "mi²", "square mile", "square miles", "sq mi"),
            Linear(bd("2589988.110336"))),       // 1609.344² m² (exact)
        UnitDef("sq_yard", "yd²", "Square Yard",
            listOf("yd2", "yd²", "square yard", "square yards", "sq yd"),
            Linear(bd("0.83612736"))),           // 0.9144² m² (exact)
        UnitDef("sq_foot", "ft²", "Square Foot",
            listOf("ft2", "ft²", "square foot", "square feet", "sq ft"),
            Linear(bd("0.09290304"))),           // 0.3048² m² (exact)
        UnitDef("sq_inch", "in²", "Square Inch",
            listOf("in2", "in²", "square inch", "square inches", "sq in"),
            Linear(bd("0.00064516"))),           // 0.0254² m² (exact)
    ),
)
