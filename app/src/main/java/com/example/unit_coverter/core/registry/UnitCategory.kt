package com.example.unit_coverter.core.registry

/**
 * A named group of [UnitDef]s that all measure the same physical quantity.
 *
 * Adding a new category requires only adding a new instance to [UnitRegistry.categories] —
 * no changes to the conversion engine or UI scaffolding.
 *
 * @param id         Stable machine identifier (used in Room, deeplinks, widget prefs).
 * @param displayName Human-readable name shown in the category list.
 * @param baseUnitId  The [UnitDef.id] of the pivot unit for this category.
 *                    All [UnitDef.formula]s convert to/from this base.
 */
data class UnitCategory(
    val id: String,
    val displayName: String,
    val baseUnitId: String,
    val units: List<UnitDef>,
    val isPremium: Boolean = false,
)
