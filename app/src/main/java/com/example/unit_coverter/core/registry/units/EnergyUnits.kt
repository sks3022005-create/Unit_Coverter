package com.example.unit_coverter.core.registry.units

import com.example.unit_coverter.core.math.ConversionFormula.Linear
import com.example.unit_coverter.core.math.FT_LBF_J
import com.example.unit_coverter.core.math.MC
import com.example.unit_coverter.core.math.bd
import com.example.unit_coverter.core.registry.UnitCategory
import com.example.unit_coverter.core.registry.UnitDef

// Base unit: joule
// Calorie (thermochemical) = 4.184 J (exact by definition, IUPAC).
// BTU(IT) = 1055.05585262 J; BTU(th) = 1054.35026444 J — both listed, labeled.
// Electronvolt = 1.602176634e-19 J (exact, 2019 SI redefinition).
internal val EnergyCategory = UnitCategory(
    id = "energy",
    displayName = "Energy",
    baseUnitId = "joule",
    units = listOf(
        UnitDef("joule", "J", "Joule",
            listOf("j", "joule", "joules"),
            Linear(bd("1"))),
        UnitDef("kilojoule", "kJ", "Kilojoule",
            listOf("kj", "kilojoule", "kilojoules"),
            Linear(bd("1000"))),
        UnitDef("megajoule", "MJ", "Megajoule",
            listOf("mj", "megajoule", "megajoules"),
            Linear(bd("1000000"))),
        UnitDef("gigajoule", "GJ", "Gigajoule",
            listOf("gj", "gigajoule", "gigajoules"),
            Linear(bd("1000000000"))),
        UnitDef("calorie", "cal", "Calorie (thermochemical)",
            listOf("cal", "calorie", "calories", "cal_th"),
            Linear(bd("4.184"))),               // exact, IUPAC
        UnitDef("kilocalorie", "kcal", "Kilocalorie",
            listOf("kcal", "kilocalorie", "kilocalories", "food calorie", "large calorie"),
            Linear(bd("4184"))),
        UnitDef("calorie_it", "cal_IT", "Calorie (International Table)",
            listOf("cal it", "calorie it", "it calorie"),
            Linear(bd("4.1868"))),
        UnitDef("btu_it", "BTU", "British Thermal Unit (IT)",
            listOf("btu", "btu_it", "british thermal unit", "btu it"),
            Linear(bd("1055.05585262"))),
        UnitDef("btu_th", "BTU (th)", "British Thermal Unit (thermochemical)",
            listOf("btu th", "btu_th", "british thermal unit th"),
            Linear(bd("1054.35026444"))),
        UnitDef("watt_hour", "Wh", "Watt-Hour",
            listOf("wh", "watt hour", "watt-hour", "watt hours"),
            Linear(bd("3600"))),
        UnitDef("kilowatt_hour", "kWh", "Kilowatt-Hour",
            listOf("kwh", "kilowatt hour", "kilowatt-hour"),
            Linear(bd("3600000"))),
        UnitDef("megawatt_hour", "MWh", "Megawatt-Hour",
            listOf("mwh", "megawatt hour", "megawatt-hour"),
            Linear(bd("3600000000"))),
        UnitDef("electronvolt", "eV", "Electronvolt",
            listOf("ev", "electronvolt", "electron volt", "electron-volt"),
            Linear(bd("0.0000000000000000001602176634"))), // 1.602176634e-19 J (exact)
        UnitDef("ft_lbf", "ft·lbf", "Foot-Pound",
            listOf("ft lbf", "ft·lbf", "foot pound", "foot-pound", "foot-pounds"),
            Linear(FT_LBF_J)),
        UnitDef("erg", "erg", "Erg",
            listOf("erg", "ergs"),
            Linear(bd("0.0000001"))),            // 10⁻⁷ J (exact)
        UnitDef("therm_us", "thm", "Therm (US)",
            listOf("thm", "therm", "therms", "us therm"),
            Linear(bd("105505585.262"))),        // 100,000 × BTU(IT)
    ),
)
