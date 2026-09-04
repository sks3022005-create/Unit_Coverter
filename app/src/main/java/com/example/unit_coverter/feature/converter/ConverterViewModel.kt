package com.example.unit_coverter.feature.converter

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unit_coverter.core.math.ExpressionEvaluator
import com.example.unit_coverter.core.math.NumberFormatter
import com.example.unit_coverter.core.nlp.NlpParser
import com.example.unit_coverter.core.nlp.NlpResult
import com.example.unit_coverter.core.registry.UnitCategory
import com.example.unit_coverter.core.registry.UnitDef
import com.example.unit_coverter.core.registry.UnitRegistry
import com.example.unit_coverter.data.local.prefs.UserPrefsDataStore
import com.example.unit_coverter.domain.usecase.ConvertUseCase
import com.example.unit_coverter.domain.usecase.GetFavoritesUseCase
import com.example.unit_coverter.domain.usecase.RecordHistoryUseCase
import com.example.unit_coverter.domain.usecase.ToggleFavoriteUseCase
import com.example.unit_coverter.widget.WidgetUpdater
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class ConverterViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val convertUseCase: ConvertUseCase,
    private val recordHistoryUseCase: RecordHistoryUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    getFavoritesUseCase: GetFavoritesUseCase,
    private val userPrefsDataStore: UserPrefsDataStore,
    private val widgetUpdater: WidgetUpdater,
) : ViewModel() {

    private val _state = MutableStateFlow(ConverterUiState())

    private val favoritesFlow = getFavoritesUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val state: StateFlow<ConverterUiState> = combine(_state, favoritesFlow) { s, favs ->
        val isFav = favs.any {
            it.categoryId == s.selectedCategory?.id &&
            it.fromUnitId == s.fromUnit?.id &&
            it.toUnitId == s.toUnit?.id
        }
        s.copy(
            favorites = favs.toImmutableList(),
            isFavorite = isFav,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ConverterUiState())

    private var historyDebounceJob: Job? = null

    init {
        val categories = UnitRegistry.categories.toImmutableList()
        _state.update { it.copy(categories = categories, isLoading = false) }

        // Nav args are stored in SavedStateHandle by Navigation Compose 2.8+ type-safe routes.
        val explicitCatId: String? = savedStateHandle["categoryId"]
        val explicitFromId: String? = savedStateHandle["fromUnitId"]
        val explicitToId: String? = savedStateHandle["toUnitId"]

        if (explicitCatId != null) {
            val cat = UnitRegistry.categoryById(explicitCatId) ?: categories.firstOrNull()
            if (cat != null) {
                val from = explicitFromId?.let { id -> cat.units.firstOrNull { it.id == id } }
                val to = explicitToId?.let { id -> cat.units.firstOrNull { it.id == id } }
                applyCategory(cat, overrideFrom = from, overrideTo = to)
            }
        } else {
            viewModelScope.launch {
                val defaultCatId = userPrefsDataStore.defaultCategoryId.first()
                val cat = UnitRegistry.categoryById(defaultCatId)
                    ?: UnitRegistry.categories.firstOrNull()
                if (cat != null) applyCategory(cat)
            }
        }
    }

    fun onEvent(event: ConverterEvent) {
        when (event) {
            is ConverterEvent.SelectCategory -> applyCategory(event.category)
            is ConverterEvent.SelectFromUnit -> {
                _state.update { it.copy(fromUnit = event.unit) }
                performConversionIfReady()
            }
            is ConverterEvent.SelectToUnit -> {
                _state.update { it.copy(toUnit = event.unit) }
                performConversionIfReady()
            }
            is ConverterEvent.InputChanged -> {
                _state.update { it.copy(inputText = event.text, errorMessage = null, nlpValue = null) }
                val nlp = NlpParser.parse(event.text)
                if (nlp != null) applyNlpResult(nlp) else performConversionIfReady()
                scheduleHistoryRecord()
            }
            is ConverterEvent.SwapUnits -> {
                _state.update { s ->
                    s.copy(
                        fromUnit = s.toUnit,
                        toUnit = s.fromUnit,
                        inputText = if (s.resultText.isNotEmpty()) s.resultText else s.inputText,
                        resultText = "",
                        nlpValue = null,
                    )
                }
                performConversionIfReady()
            }
            is ConverterEvent.ToggleFavorite -> {
                val s = _state.value
                val cat = s.selectedCategory ?: return
                val from = s.fromUnit ?: return
                val to = s.toUnit ?: return
                viewModelScope.launch {
                    toggleFavoriteUseCase(cat.id, from.id, to.id)
                }
            }
            is ConverterEvent.ClearInput -> {
                _state.update { it.copy(inputText = "", resultText = "", errorMessage = null, nlpValue = null) }
            }

            // ── In-app keypad ────────────────────────────────────────────────
            // Each key edits the raw text then reconverts immediately, so the
            // result tracks the entry with no "equals" step.
            is ConverterEvent.AppendDigit -> editInput { it + event.digit }

            is ConverterEvent.AppendDecimal -> editInput { current ->
                // One decimal point only; a leading point becomes "0.".
                val body = current.removePrefix("-")
                when {
                    body.contains('.') -> current
                    body.isEmpty() -> if (current.startsWith("-")) "-0." else "0."
                    else -> "$current."
                }
            }

            is ConverterEvent.Backspace -> editInput { it.dropLast(1) }

            is ConverterEvent.ToggleSign -> editInput { current ->
                if (current.startsWith("-")) current.removePrefix("-") else "-$current"
            }
        }
    }

    /**
     * Applies [transform] to the current input and reconverts.
     *
     * Routed through the same NLP/expression path as typing so keypad entry and
     * text entry can never diverge.
     */
    private fun editInput(transform: (String) -> String) {
        val updated = transform(_state.value.inputText)
        _state.update { it.copy(inputText = updated, errorMessage = null, nlpValue = null) }
        val nlp = NlpParser.parse(updated)
        if (nlp != null) applyNlpResult(nlp) else performConversionIfReady()
        scheduleHistoryRecord()
    }

    private fun applyCategory(
        category: UnitCategory,
        overrideFrom: UnitDef? = null,
        overrideTo: UnitDef? = null,
    ) {
        val units = category.units
        val from = overrideFrom ?: units.firstOrNull()
        val to = overrideTo ?: if (overrideFrom != null) {
            units.firstOrNull { it.id != overrideFrom.id }
        } else {
            units.getOrNull(1)
        }
        _state.update {
            it.copy(
                selectedCategory = category,
                fromUnit = from,
                toUnit = to,
                resultText = "",
                errorMessage = null,
            )
        }
        performConversionIfReady()
    }

    private fun applyNlpResult(result: NlpResult) {
        val cat = UnitRegistry.categoryById(result.categoryId) ?: run {
            performConversionIfReady(); return
        }
        val fromUnit = cat.units.firstOrNull { it.id == result.fromUnitId } ?: run {
            performConversionIfReady(); return
        }
        val current = _state.value
        val toUnit = result.toUnitId?.let { id -> cat.units.firstOrNull { it.id == id } }
            ?: if (current.selectedCategory?.id == cat.id) current.toUnit
            else cat.units.firstOrNull { it.id != fromUnit.id }

        _state.update { s ->
            s.copy(
                selectedCategory = cat,
                fromUnit = fromUnit,
                toUnit = toUnit,
                nlpValue = result.numericValue,
                errorMessage = null,
            )
        }
        performConversionIfReady()
    }

    private fun performConversionIfReady() {
        val s = _state.value
        val from = s.fromUnit ?: return
        val to = s.toUnit ?: return

        // Use NLP-resolved value when available; otherwise evaluate the expression.
        val inputValue: BigDecimal = if (s.nlpValue != null) {
            s.nlpValue
        } else {
            val raw = s.inputText.trim()
            if (raw.isEmpty()) {
                _state.update { it.copy(resultText = "", errorMessage = null) }
                return
            }
            runCatching { ExpressionEvaluator.evaluate(raw) }.getOrElse {
                _state.update { it.copy(resultText = "", errorMessage = "Invalid expression") }
                return
            }
        }

        convertUseCase(inputValue, from.id, to.id).fold(
            onSuccess = { cr ->
                val formatted = NumberFormatter.format(cr.outputValue)
                _state.update { it.copy(resultText = formatted, errorMessage = null) }
                viewModelScope.launch {
                    widgetUpdater.update(
                        fromValue = NumberFormatter.format(inputValue),
                        fromUnit = from.symbol.ifBlank { from.displayName },
                        toValue = formatted,
                        toUnit = to.symbol.ifBlank { to.displayName },
                    )
                }
            },
            onFailure = { ex ->
                _state.update { it.copy(resultText = "", errorMessage = ex.message) }
            },
        )
    }

    private fun scheduleHistoryRecord() {
        historyDebounceJob?.cancel()
        historyDebounceJob = viewModelScope.launch {
            delay(1_500)
            val s = _state.value
            val from = s.fromUnit ?: return@launch
            val to = s.toUnit ?: return@launch
            val inputValue = s.nlpValue ?: run {
                val raw = s.inputText.trim().ifEmpty { return@launch }
                runCatching { ExpressionEvaluator.evaluate(raw) }.getOrNull() ?: return@launch
            }
            val result = convertUseCase(inputValue, from.id, to.id).getOrNull() ?: return@launch
            recordHistoryUseCase(result)
        }
    }
}
