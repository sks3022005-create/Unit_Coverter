package com.example.unit_coverter.core.registry.units

import com.example.unit_coverter.core.math.ConversionFormula.Linear
import com.example.unit_coverter.core.math.bd
import com.example.unit_coverter.core.registry.UnitCategory
import com.example.unit_coverter.core.registry.UnitDef

// Base unit: liter
internal val VolumeCategory = UnitCategory(
    id = "volume",
    displayName = "Volume",
    baseUnitId = "liter",
    units = listOf(
        UnitDef("liter", "L", "Liter",
            listOf("l", "liter", "liters", "litre", "litres"),
            Linear(bd("1"))),
        UnitDef("milliliter", "mL", "Milliliter",
            listOf("ml", "milliliter", "milliliters", "millilitre"),
            Linear(bd("0.001"))),
        UnitDef("cubic_meter", "m³", "Cubic Meter",
            listOf("m3", "m³", "cubic meter", "cubic meters", "cubic metre"),
            Linear(bd("1000"))),
        UnitDef("cubic_centimeter", "cm³", "Cubic Centimeter",
            listOf("cm3", "cm³", "cubic centimeter", "cubic centimeters", "cc"),
            Linear(bd("0.001"))),
        UnitDef("cubic_inch", "in³", "Cubic Inch",
            listOf("in3", "in³", "cubic inch", "cubic inches"),
            Linear(bd("0.016387064"))),          // 0.0254³ × 1000 (exact)
        UnitDef("cubic_foot", "ft³", "Cubic Foot",
            listOf("ft3", "ft³", "cubic foot", "cubic feet"),
            Linear(bd("28.316846592"))),         // 0.3048³ × 1000 (exact)
        UnitDef("cubic_yard", "yd³", "Cubic Yard",
            listOf("yd3", "yd³", "cubic yard", "cubic yards"),
            Linear(bd("764.554857984"))),        // 0.9144³ × 1000 (exact)
        // --- US liquid ---
        UnitDef("us_gallon", "gal", "Gallon (US)",
            listOf("gal", "gallon", "gallons", "us gallon", "us gallons"),
            Linear(bd("3.785411784"))),          // exact: 231 in³
        UnitDef("us_quart", "qt", "Quart (US)",
            listOf("qt", "quart", "quarts", "us quart"),
            Linear(bd("0.946352946"))),
        UnitDef("us_pint", "pt", "Pint (US)",
            listOf("pt", "pint", "pints", "us pint"),
            Linear(bd("0.473176473"))),
        UnitDef("us_cup", "cup", "Cup (US)",
            listOf("cup", "cups", "us cup"),
            Linear(bd("0.2365882365"))),
        UnitDef("us_fluid_oz", "fl oz", "Fluid Ounce (US)",
            listOf("fl oz", "floz", "fluid ounce", "fluid ounces", "us fl oz"),
            Linear(bd("0.0295735295625"))),      // = US gal / 128 (exact)
        UnitDef("us_tablespoon", "tbsp", "Tablespoon (US)",
            listOf("tbsp", "tablespoon", "tablespoons", "tbs", "us tablespoon"),
            Linear(bd("0.01478676478125"))),     // = US gal / 256
        UnitDef("us_teaspoon", "tsp", "Teaspoon (US)",
            listOf("tsp", "teaspoon", "teaspoons", "us teaspoon"),
            Linear(bd("0.00492892159375"))),     // = US gal / 768
        // --- Imperial ---
        UnitDef("imp_gallon", "imp gal", "Gallon (Imperial)",
            listOf("imp gal", "imperial gallon", "imperial gallons", "uk gallon"),
            Linear(bd("4.54609"))),              // exact, 1985
        UnitDef("imp_quart", "imp qt", "Quart (Imperial)",
            listOf("imp qt", "imperial quart", "uk quart"),
            Linear(bd("1.1365225"))),
        UnitDef("imp_pint", "imp pt", "Pint (Imperial)",
            listOf("imp pt", "imperial pint", "uk pint"),
            Linear(bd("0.56826125"))),
        UnitDef("imp_fluid_oz", "imp fl oz", "Fluid Ounce (Imperial)",
            listOf("imp fl oz", "imperial fl oz", "uk fl oz"),
            Linear(bd("0.0284130625"))),         // = Imp gal / 160 (exact)
    ),
)
