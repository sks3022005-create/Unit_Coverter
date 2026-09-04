package com.example.unit_coverter.feature.converter

import com.example.unit_coverter.core.registry.UnitCategory
import com.example.unit_coverter.core.registry.UnitDef
import com.example.unit_coverter.domain.model.Favorite
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class ConverterUiState(
    val categories: ImmutableList<UnitCategory> = persistentListOf(),
    val selectedCategory: UnitCategory? = null,
    val fromUnit: UnitDef? = null,
    val toUnit: UnitDef? = null,
    val inputText: String = "",
    val resultText: String = "",
    val errorMessage: String? = null,
    /** Non-null when the current [inputText] was successfully parsed by NlpParser. */
    val nlpValue: java.math.BigDecimal? = null,
    val isFavorite: Boolean = false,
    val favorites: ImmutableList<Favorite> = persistentListOf(),
    val isLoading: Boolean = true,
)

sealed interface ConverterEvent {
    data class SelectCategory(val category: UnitCategory) : ConverterEvent
    data class SelectFromUnit(val unit: UnitDef) : ConverterEvent
    data class SelectToUnit(val unit: UnitDef) : ConverterEvent
    data class InputChanged(val text: String) : ConverterEvent
    data object SwapUnits : ConverterEvent
    data object ToggleFavorite : ConverterEvent
    data object ClearInput : ConverterEvent

    /** Keypad: append a digit. */
    data class AppendDigit(val digit: Char) : ConverterEvent

    /** Keypad: append a decimal point (ignored if one is already present). */
    data object AppendDecimal : ConverterEvent

    /** Keypad: delete the last character. */
    data object Backspace : ConverterEvent

    /** Keypad: flip the sign of the current entry. */
    data object ToggleSign : ConverterEvent
}
