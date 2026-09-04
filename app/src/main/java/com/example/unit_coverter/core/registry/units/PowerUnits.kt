package com.example.unit_coverter.core.registry.units

import com.example.unit_coverter.core.math.ConversionFormula.Linear
import com.example.unit_coverter.core.math.FT_LBF_J
import com.example.unit_coverter.core.math.HP_MECH_W
import com.example.unit_coverter.core.math.HP_METRIC_W
import com.example.unit_coverter.core.math.MC
import com.example.unit_coverter.core.math.bd
import com.example.unit_coverter.core.registry.UnitCategory
import com.example.unit_coverter.core.registry.UnitDef

// Base unit: watt
// HP(mech) = 550 ft·lbf/s ≠ HP(metric) = 75 kgf·m/s — listed distinctly.
// HP(electrical) = 746 W exactly.
private val BTU_IT_PER_HOUR = bd("1055.05585262").divide(bd("3600"), MC)
private val FT_LBF_PER_MIN = FT_LBF_J.divide(bd("60"), MC)

internal val PowerCategory = UnitCategory(
    id = "power",
    displayName = "Power",
    baseUnitId = "watt",
    units = listOf(
        UnitDef("watt", "W", "Watt",
            listOf("w", "watt", "watts"),
            Linear(bd("1"))),
        UnitDef("kilowatt", "kW", "Kilowatt",
            listOf("kw", "kilowatt", "kilowatts"),
            Linear(bd("1000"))),
        UnitDef("megawatt", "MW", "Megawatt",
            listOf("mw", "megawatt", "megawatts"),
            Linear(bd("1000000"))),
        UnitDef("gigawatt", "GW", "Gigawatt",
            listOf("gw", "gigawatt", "gigawatts"),
            Linear(bd("1000000000"))),
        UnitDef("hp_mech", "hp", "Horsepower (Mechanical)",
            listOf("hp", "horsepower", "mechanical hp", "us hp", "hp mech"),
            Linear(HP_MECH_W)),                 // 550 ft·lbf/s ≈ 745.69987 W
        UnitDef("hp_metric", "PS", "Horsepower (Metric)",
            listOf("ps", "metric hp", "cv", "pferdestärke", "pk", "hp metric"),
            Linear(HP_METRIC_W)),               // 75 kgf·m/s = 735.49875 W (exact)
        UnitDef("hp_elec", "hp(E)", "Horsepower (Electrical)",
            listOf("hp electric", "hp elec", "electrical hp"),
            Linear(bd("746"))),                 // exact
        UnitDef("btu_per_hour", "BTU/h", "BTU per Hour",
            listOf("btu/h", "btu per hour", "btuh"),
            Linear(BTU_IT_PER_HOUR)),
        UnitDef("calorie_per_second", "cal/s", "Calorie per Second",
            listOf("cal/s", "calorie per second"),
            Linear(bd("4.184"))),
        UnitDef("ft_lbf_per_min", "ft·lbf/min", "Foot-Pound per Minute",
            listOf("ft lbf/min", "ft·lbf/min", "foot-pound per minute"),
            Linear(FT_LBF_PER_MIN)),
    ),
)
