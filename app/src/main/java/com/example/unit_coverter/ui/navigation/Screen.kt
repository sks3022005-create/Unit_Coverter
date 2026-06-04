package com.example.unit_coverter.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {

    @Serializable data object Converter : Screen()

    @Serializable data object Search : Screen()

    @Serializable data object History : Screen()

    @Serializable data object Favorites : Screen()

    @Serializable data object Settings : Screen()

    @Serializable data object Cooking : Screen()

    /** Deep-link into a specific category (and optional unit pair) from search / favorites / widget. */
    @Serializable data class CategoryConverter(
        val categoryId: String,
        val fromUnitId: String? = null,
        val toUnitId: String? = null,
    ) : Screen()

    @Serializable data object CustomUnits : Screen()

    /** [unitId] is null for add mode, non-null for edit mode. */
    @Serializable data class AddEditCustomUnit(val unitId: String? = null) : Screen()
}