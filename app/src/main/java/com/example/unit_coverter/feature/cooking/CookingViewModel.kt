package com.example.unit_coverter.feature.cooking

import androidx.lifecycle.ViewModel
import com.example.unit_coverter.core.cooking.CookingConverter
import com.example.unit_coverter.core.cooking.INGREDIENTS
import com.example.unit_coverter.core.cooking.Ingredient
import com.example.unit_coverter.core.math.ExpressionEvaluator
import com.example.unit_coverter.core.math.NumberFormatter
import com.example.unit_coverter.core.registry.UnitRegistry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CookingViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(CookingUiState())
    val state: StateFlow<CookingUiState> = _state.asStateFlow()

    init {
        val massUnits = CookingConverter.MASS_UNIT_IDS
            .mapNotNull { id -> UnitRegistry.findById(id)?.second?.let { CookingUnitItem(it, isMass = true) } }
            .toImmutableList()
        val volumeUnits = CookingConverter.VOLUME_UNIT_IDS
            .mapNotNull { id -> UnitRegistry.findById(id)?.second?.let { CookingUnitItem(it, isMass = false) } }
            .toImmutableList()

        val defaultIngredient = INGREDIENTS.first()
        val defaultFrom = massUnits.firstOrNull { it.unit.id == "gram" } ?: massUnits.first()
        val defaultTo = volumeUnits.firstOrNull { it.unit.id == "us_cup" } ?: volumeUnits.first()

        _state.update {
            it.copy(
                ingredients = INGREDIENTS.toImmutableList(),
                selectedIngredient = defaultIngredient,
                massUnits = massUnits,
                volumeUnits = volumeUnits,
                fromUnit = defaultFrom,
                toUnit = defaultTo,
                isLoading = false,
            )
        }
    }

    fun onEvent(event: CookingEvent) {
        when (event) {
            is CookingEvent.SelectIngredient -> {
                _state.update { it.copy(selectedIngredient = event.ingredient) }
                performConversionIfReady()
            }
            is CookingEvent.SelectFromUnit -> {
                _state.update { it.copy(fromUnit = event.unit) }
                performConversionIfReady()
            }
            is CookingEvent.SelectToUnit -> {
                _state.update { it.copy(toUnit = event.unit) }
                performConversionIfReady()
            }
            is CookingEvent.InputChanged -> {
                _state.update { it.copy(inputText = event.text, errorMessage = null) }
                performConversionIfReady()
            }
            is CookingEvent.SwapUnits -> {
                _state.update { s ->
                    s.copy(
                        fromUnit = s.toUnit,
                        toUnit = s.fromUnit,
                        inputText = if (s.resultText.isNotEmpty()) s.resultText else s.inputText,
                        resultText = "",
                    )
                }
                performConversionIfReady()
            }
            is CookingEvent.ClearInput -> {
                _state.update { it.copy(inputText = "", resultText = "", errorMessage = null) }
            }
        }
    }

    private fun performConversionIfReady() {
        val s = _state.value
        val from = s.fromUnit ?: return
        val to = s.toUnit ?: return
        val ingredient = s.selectedIngredient ?: return
        val raw = s.inputText.trim()
        if (raw.isEmpty()) {
            _state.update { it.copy(resultText = "", errorMessage = null) }
            return
        }
        val value = runCatching { ExpressionEvaluator.evaluate(raw) }.getOrElse {
            _state.update { it.copy(resultText = "", errorMessage = "Invalid expression") }
            return
        }
        CookingConverter.convert(value, from.unit, to.unit, ingredient).fold(
            onSuccess = { result ->
                _state.update { it.copy(resultText = NumberFormatter.format(result), errorMessage = null) }
            },
            onFailure = { ex ->
                _state.update { it.copy(resultText = "", errorMessage = ex.message) }
            },
        )
    }
}
