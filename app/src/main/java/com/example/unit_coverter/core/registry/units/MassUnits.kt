package com.example.unit_coverter.core.registry.units

import com.example.unit_coverter.core.math.ConversionFormula.Linear
import com.example.unit_coverter.core.math.bd
import com.example.unit_coverter.core.registry.UnitCategory
import com.example.unit_coverter.core.registry.UnitDef

// Base unit: kilogram
internal val MassCategory = UnitCategory(
    id = "mass",
    displayName = "Mass / Weight",
    baseUnitId = "kilogram",
    units = listOf(
        UnitDef("kilogram", "kg", "Kilogram",
            listOf("kg", "kilogram", "kilograms", "kilogramme"),
            Linear(bd("1"))),
        UnitDef("gram", "g", "Gram",
            listOf("g", "gram", "grams", "gramme"),
            Linear(bd("0.001"))),
        UnitDef("milligram", "mg", "Milligram",
            listOf("mg", "milligram", "milligrams"),
            Linear(bd("0.000001"))),
        UnitDef("microgram", "µg", "Microgram",
            listOf("ug", "µg", "microgram", "micrograms"),
            Linear(bd("0.000000001"))),
        UnitDef("tonne", "t", "Tonne (Metric Ton)",
            listOf("t", "tonne", "tonnes", "metric ton", "metric tons"),
            Linear(bd("1000"))),
        UnitDef("pound", "lb", "Pound",
            listOf("lb", "lbs", "pound", "pounds", "lb avoirdupois"),
            Linear(bd("0.45359237"))),          // exact
        UnitDef("ounce", "oz", "Ounce",
            listOf("oz", "ounce", "ounces"),
            Linear(bd("0.028349523125"))),       // = 0.45359237 / 16 (exact)
        UnitDef("stone", "st", "Stone",
            listOf("st", "stone", "stones"),
            Linear(bd("6.35029318"))),           // = 14 × 0.45359237
        UnitDef("short_ton", "ton", "Short Ton (US)",
            listOf("ton", "tons", "short ton", "us ton", "short tons"),
            Linear(bd("907.18474"))),            // = 2000 lb
        UnitDef("long_ton", "long ton", "Long Ton (UK)",
            listOf("long ton", "long tons", "imperial ton", "uk ton"),
            Linear(bd("1016.0469088"))),         // = 2240 lb
        UnitDef("carat", "ct", "Carat",
            listOf("ct", "carat", "carats", "karat"),
            Linear(bd("0.0002"))),               // exact: 200 mg
        UnitDef("grain", "gr", "Grain",
            listOf("gr", "grain", "grains"),
            Linear(bd("0.00006479891"))),        // = 1/7000 lb (exact)
        UnitDef("troy_ounce", "ozt", "Troy Ounce",
            listOf("ozt", "troy ounce", "troy ounces", "oz t"),
            Linear(bd("0.0311034768"))),         // = 480 grains
    ),
)
