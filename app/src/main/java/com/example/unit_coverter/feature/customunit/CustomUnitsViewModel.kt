package com.example.unit_coverter.feature.customunit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unit_coverter.billing.PremiumGate
import com.example.unit_coverter.domain.model.CustomUnitDef
import com.example.unit_coverter.domain.usecase.DeleteCustomUnitUseCase
import com.example.unit_coverter.domain.usecase.GetCustomUnitsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val FREE_UNIT_LIMIT = 3

@HiltViewModel
class CustomUnitsViewModel @Inject constructor(
    private val getCustomUnitsUseCase: GetCustomUnitsUseCase,
    private val deleteCustomUnitUseCase: DeleteCustomUnitUseCase,
    private val premiumGate: PremiumGate,
) : ViewModel() {

    data class UiState(
        val units: ImmutableList<CustomUnitDef> = persistentListOf(),
        val isUnlocked: Boolean = true,
        val canAddMore: Boolean = true,
        val isLoading: Boolean = true,
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                getCustomUnitsUseCase(),
                premiumGate.isUnlocked,
            ) { units, unlocked ->
                UiState(
                    units = units.toImmutableList(),
                    isUnlocked = unlocked,
                    canAddMore = unlocked || units.size < FREE_UNIT_LIMIT,
                    isLoading = false,
                )
            }.collect { _state.value = it }
        }
    }

    fun delete(id: String) {
        viewModelScope.launch { deleteCustomUnitUseCase(id) }
    }
}
