package com.example.unit_coverter.feature.cooking

import com.example.unit_coverter.core.cooking.Ingredient
import com.example.unit_coverter.core.registry.UnitDef
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class CookingUnitItem(
    val unit: UnitDef,
    val isMass: Boolean,
)

data class CookingUiState(
    val ingredients: ImmutableList<Ingredient> = persistentListOf(),
    val selectedIngredient: Ingredient? = null,
    val massUnits: ImmutableList<CookingUnitItem> = persistentListOf(),
    val volumeUnits: ImmutableList<CookingUnitItem> = persistentListOf(),
    val fromUnit: CookingUnitItem? = null,
    val toUnit: CookingUnitItem? = null,
    val inputText: String = "",
    val resultText: String = "",
    val errorMessage: String? = null,
    val isLoading: Boolean = true,
)

sealed interface CookingEvent {
    data class SelectIngredient(val ingredient: Ingredient) : CookingEvent
    data class SelectFromUnit(val unit: CookingUnitItem) : CookingEvent
    data class SelectToUnit(val unit: CookingUnitItem) : CookingEvent
    data class InputChanged(val text: String) : CookingEvent
    data object SwapUnits : CookingEvent
    data object ClearInput : CookingEvent
}
