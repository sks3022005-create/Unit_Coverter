package com.example.unit_coverter.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unit_coverter.core.math.NumberFormatter
import com.example.unit_coverter.core.registry.UnitRegistry
import com.example.unit_coverter.domain.model.ConversionRecord
import com.example.unit_coverter.domain.usecase.ClearHistoryUseCase
import com.example.unit_coverter.domain.usecase.GetRecentHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HistoryItem(
    val record: ConversionRecord,
    val inputFormatted: String,
    val resultFormatted: String,
    val fromUnitName: String,
    val toUnitName: String,
    val categoryName: String,
)

data class HistoryUiState(
    val items: ImmutableList<HistoryItem> = persistentListOf(),
    val isLoading: Boolean = true,
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    getRecentHistoryUseCase: GetRecentHistoryUseCase,
    private val clearHistoryUseCase: ClearHistoryUseCase,
) : ViewModel() {

    val uiState = getRecentHistoryUseCase(limit = 200)
        .map { records ->
            HistoryUiState(
                items = records.map { it.toItem() }.toImmutableList(),
                isLoading = false,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HistoryUiState(),
        )

    fun clearAll() {
        viewModelScope.launch { clearHistoryUseCase() }
    }

    private fun ConversionRecord.toItem(): HistoryItem {
        val fromUnit = UnitRegistry.findById(fromUnitId)
        val toUnit = UnitRegistry.findById(toUnitId)
        val category = UnitRegistry.categoryById(categoryId)
        return HistoryItem(
            record = this,
            inputFormatted = NumberFormatter.format(inputValue),
            resultFormatted = NumberFormatter.format(resultValue),
            fromUnitName = fromUnit?.second?.displayName ?: fromUnitId,
            toUnitName = toUnit?.second?.displayName ?: toUnitId,
            categoryName = category?.displayName ?: categoryId,
        )
    }
}
