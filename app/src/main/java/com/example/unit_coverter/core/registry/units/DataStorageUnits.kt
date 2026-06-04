package com.example.unit_coverter.core.registry.units

import com.example.unit_coverter.core.math.ConversionFormula.Linear
import com.example.unit_coverter.core.math.bd
import com.example.unit_coverter.core.registry.UnitCategory
import com.example.unit_coverter.core.registry.UnitDef

// Base unit: byte
// SI (decimal) prefixes: 1 KB = 1000 B, 1 MB = 1,000,000 B …
// IEC (binary) prefixes: 1 KiB = 1024 B, 1 MiB = 1,048,576 B …
// Both systems are included and labeled to avoid ambiguity.
internal val DataStorageCategory = UnitCategory(
    id = "data_storage",
    displayName = "Data Storage",
    baseUnitId = "byte",
    units = listOf(
        UnitDef("bit", "bit", "Bit",
            listOf("bit", "bits", "b"),
            Linear(bd("0.125"))),               // 1/8 byte
        UnitDef("byte", "B", "Byte",
            listOf("b", "byte", "bytes"),
            Linear(bd("1"))),
        // --- SI (decimal) ---
        UnitDef("kilobyte", "KB", "Kilobyte",
            listOf("kb", "kilobyte", "kilobytes"),
            Linear(bd("1000"))),
        UnitDef("megabyte", "MB", "Megabyte",
            listOf("mb", "megabyte", "megabytes"),
            Linear(bd("1000000"))),
        UnitDef("gigabyte", "GB", "Gigabyte",
            listOf("gb", "gigabyte", "gigabytes"),
            Linear(bd("1000000000"))),
        UnitDef("terabyte", "TB", "Terabyte",
            listOf("tb", "terabyte", "terabytes"),
            Linear(bd("1000000000000"))),
        UnitDef("petabyte", "PB", "Petabyte",
            listOf("pb", "petabyte", "petabytes"),
            Linear(bd("1000000000000000"))),
        // --- IEC (binary) ---
        UnitDef("kibibyte", "KiB", "Kibibyte",
            listOf("kib", "kibibyte", "kibibytes"),
            Linear(bd("1024"))),
        UnitDef("mebibyte", "MiB", "Mebibyte",
            listOf("mib", "mebibyte", "mebibytes"),
            Linear(bd("1048576"))),
        UnitDef("gibibyte", "GiB", "Gibibyte",
            listOf("gib", "gibibyte", "gibibytes"),
            Linear(bd("1073741824"))),
        UnitDef("tebibyte", "TiB", "Tebibyte",
            listOf("tib", "tebibyte", "tebibytes"),
            Linear(bd("1099511627776"))),
        UnitDef("pebibyte", "PiB", "Pebibyte",
            listOf("pib", "pebibyte", "pebibytes"),
            Linear(bd("1125899906842624"))),
        // --- network (bits, SI) ---
        UnitDef("kilobit", "Kbit", "Kilobit",
            listOf("kbit", "kilobit", "kilobits", "kb"),
            Linear(bd("125"))),                 // 1000 bits
        UnitDef("megabit", "Mbit", "Megabit",
            listOf("mbit", "megabit", "megabits"),
            Linear(bd("125000"))),
        UnitDef("gigabit", "Gbit", "Gigabit",
            listOf("gbit", "gigabit", "gigabits"),
            Linear(bd("125000000"))),
        UnitDef("terabit", "Tbit", "Terabit",
            listOf("tbit", "terabit", "terabits"),
            Linear(bd("125000000000"))),
    ),
)
