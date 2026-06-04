package com.example.unit_coverter.core.registry

import com.example.unit_coverter.core.registry.units.AngleCategory
import com.example.unit_coverter.core.registry.units.AreaCategory
import com.example.unit_coverter.core.registry.units.DataStorageCategory
import com.example.unit_coverter.core.registry.units.EnergyCategory
import com.example.unit_coverter.core.registry.units.ForceCategory
import com.example.unit_coverter.core.registry.units.FuelConsumptionCategory
import com.example.unit_coverter.core.registry.units.LengthCategory
import com.example.unit_coverter.core.registry.units.MassCategory
import com.example.unit_coverter.core.registry.units.PowerCategory
import com.example.unit_coverter.core.registry.units.PressureCategory
import com.example.unit_coverter.core.registry.units.SpeedCategory
import com.example.unit_coverter.core.registry.units.TemperatureCategory
import com.example.unit_coverter.core.registry.units.TimeCategory
import com.example.unit_coverter.core.registry.units.VolumeCategory
import java.math.BigDecimal

/**
 * Single source of truth for all unit categories.
 *
 * Adding a new built-in category = one line in [builtIn]. Zero engine/UI changes.
 * Custom user units (Module 9) are merged per-category via [setCustomUnits].
 */
object UnitRegistry {

    private val builtIn: List<UnitCategory> = listOf(
        LengthCategory,
        MassCategory,
        VolumeCategory,
        TemperatureCategory,
        AreaCategory,
        PressureCategory,
        EnergyCategory,
        PowerCategory,
        ForceCategory,
        TimeCategory,
        SpeedCategory,
        AngleCategory,
        FuelConsumptionCategory,
        DataStorageCategory,
    )

    // Custom units keyed by categoryId — replaced atomically on each DB sync.
    // @Volatile ensures JVM visibility without a full lock; reference replacement is atomic.
    @Volatile
    private var customUnitsByCategory: Map<String, List<UnitDef>> = emptyMap()

    // O(1) lookup for built-in units (computed once).
    private val builtInById: Map<String, Pair<UnitCategory, UnitDef>> by lazy {
        builtIn.flatMap { cat -> cat.units.map { it.id to (cat to it) } }.toMap()
    }

    // -----------------------------------------------------------------------
    // Public API
    // -----------------------------------------------------------------------

    /** All categories with custom units merged in. */
    val categories: List<UnitCategory>
        get() {
            val custom = customUnitsByCategory
            return if (custom.isEmpty()) builtIn else {
                builtIn.map { cat ->
                    val extras = custom[cat.id]
                    if (extras.isNullOrEmpty()) cat else cat.copy(units = cat.units + extras)
                }
            }
        }

    /** All (category, unit) pairs — recomputed on each call, used for search. */
    val allUnits: List<Pair<UnitCategory, UnitDef>>
        get() = categories.flatMap { cat -> cat.units.map { cat to it } }

    fun findById(id: String): Pair<UnitCategory, UnitDef>? {
        val builtin = builtInById[id]
        if (builtin != null) return builtin
        // Search merged custom units
        for ((categoryId, units) in customUnitsByCategory) {
            val unit = units.firstOrNull { it.id == id } ?: continue
            val category = categoryById(categoryId) ?: continue
            return category to unit
        }
        return null
    }

    fun categoryById(id: String): UnitCategory? =
        categories.firstOrNull { it.id == id }

    // -----------------------------------------------------------------------
    // Conversion engine
    // -----------------------------------------------------------------------

    /**
     * Converts [value] from [from] to [to] via the category's base unit.
     * All arithmetic uses BigDecimal with 50-digit HALF_EVEN precision.
     */
    fun convert(value: BigDecimal, from: UnitDef, to: UnitDef): BigDecimal =
        to.formula.fromBase(from.formula.toBase(value))

    // -----------------------------------------------------------------------
    // Custom unit hook (called by SyncCustomUnitsUseCase)
    // -----------------------------------------------------------------------

    /**
     * Replaces the custom-unit overlay atomically.
     * Pass an empty map to clear all custom units.
     */
    internal fun setCustomUnits(unitsByCategory: Map<String, List<UnitDef>>) {
        customUnitsByCategory = HashMap(unitsByCategory)
    }
}
