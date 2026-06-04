package com.example.unit_coverter.core.registry.units

import com.example.unit_coverter.core.math.ConversionFormula.Linear
import com.example.unit_coverter.core.math.bd
import com.example.unit_coverter.core.registry.UnitCategory
import com.example.unit_coverter.core.registry.UnitDef

// Base unit: second
// Month uses Julian definition (365.25 days/year ÷ 12 = 30.4375 days × 86400 s).
// Year (Julian) = 365.25 × 86400 = 31,557,600 s.
// Year (Gregorian) = 365.2425 × 86400 = 31,556,952 s.
internal val TimeCategory = UnitCategory(
    id = "time",
    displayName = "Time",
    baseUnitId = "second",
    units = listOf(
        UnitDef("second", "s", "Second",
            listOf("s", "sec", "second", "seconds"),
            Linear(bd("1"))),
        UnitDef("millisecond", "ms", "Millisecond",
            listOf("ms", "millisecond", "milliseconds"),
            Linear(bd("0.001"))),
        UnitDef("microsecond", "µs", "Microsecond",
            listOf("us", "µs", "microsecond", "microseconds"),
            Linear(bd("0.000001"))),
        UnitDef("nanosecond", "ns", "Nanosecond",
            listOf("ns", "nanosecond", "nanoseconds"),
            Linear(bd("0.000000001"))),
        UnitDef("minute", "min", "Minute",
            listOf("min", "mins", "minute", "minutes"),
            Linear(bd("60"))),
        UnitDef("hour", "h", "Hour",
            listOf("h", "hr", "hrs", "hour", "hours"),
            Linear(bd("3600"))),
        UnitDef("day", "d", "Day",
            listOf("d", "day", "days"),
            Linear(bd("86400"))),
        UnitDef("week", "wk", "Week",
            listOf("wk", "week", "weeks"),
            Linear(bd("604800"))),
        UnitDef("fortnight", "fn", "Fortnight",
            listOf("fortnight", "fortnights"),
            Linear(bd("1209600"))),             // 14 days
        UnitDef("month_julian", "mo", "Month (Julian avg.)",
            listOf("mo", "month", "months"),
            Linear(bd("2629800"))),             // 365.25 × 86400 / 12
        UnitDef("year_julian", "yr", "Year (Julian)",
            listOf("yr", "year", "years", "julian year"),
            Linear(bd("31557600"))),            // 365.25 × 86400
        UnitDef("year_gregorian", "yr (greg)", "Year (Gregorian)",
            listOf("gregorian year", "calendar year"),
            Linear(bd("31556952"))),            // 365.2425 × 86400
        UnitDef("decade", "dec", "Decade",
            listOf("decade", "decades"),
            Linear(bd("315576000"))),           // 10 Julian years
        UnitDef("century", "c", "Century",
            listOf("century", "centuries"),
            Linear(bd("3155760000"))),          // 100 Julian years
    ),
)
