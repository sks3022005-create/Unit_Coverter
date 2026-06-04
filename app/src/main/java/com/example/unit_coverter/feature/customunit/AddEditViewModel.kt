package com.example.unit_coverter.feature.customunit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unit_coverter.core.registry.UnitCategory
import com.example.unit_coverter.core.registry.UnitRegistry
import com.example.unit_coverter.domain.model.CustomUnitDef
import com.example.unit_coverter.domain.usecase.AddCustomUnitUseCase
import com.example.unit_coverter.domain.usecase.GetCustomUnitsUseCase
import com.example.unit_coverter.domain.usecase.UpdateCustomUnitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class AddEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getCustomUnitsUseCase: GetCustomUnitsUseCase,
    private val addCustomUnitUseCase: AddCustomUnitUseCase,
    private val updateCustomUnitUseCase: UpdateCustomUnitUseCase,
) : ViewModel() {

    private val unitId: String? = savedStateHandle["unitId"]
    val isEditMode: Boolean = unitId != null

    val categories: List<UnitCategory> = UnitRegistry.categories

    data class FormState(
        val displayName: String = "",
        val symbol: String = "",
        val selectedCategory: UnitCategory? = null,
        val factorString: String = "",
        val errorMessage: String? = null,
        val isSaving: Boolean = false,
        val savedSuccessfully: Boolean = false,
        val isLoading: Boolean = true,
    )

    private val _form = MutableStateFlow(FormState())
    val form: StateFlow<FormState> = _form.asStateFlow()

    private var editingUnit: CustomUnitDef? = null

    init {
        if (unitId != null) {
            viewModelScope.launch {
                val unit = getCustomUnitsUseCase().first().firstOrNull { it.id == unitId }
                editingUnit = unit
                _form.update { s ->
                    if (unit != null) {
                        s.copy(
                            displayName = unit.displayName,
                            symbol = unit.symbol,
                            selectedCategory = UnitRegistry.categoryById(unit.categoryId),
                            factorString = unit.factor.toPlainString(),
                            isLoading = false,
                        )
                    } else {
                        s.copy(isLoading = false)
                    }
                }
            }
        } else {
            _form.update { it.copy(isLoading = false) }
        }
    }

    fun onNameChanged(value: String) = _form.update { it.copy(displayName = value, errorMessage = null) }
    fun onSymbolChanged(value: String) = _form.update { it.copy(symbol = value, errorMessage = null) }
    fun onCategorySelected(cat: UnitCategory) = _form.update { it.copy(selectedCategory = cat, errorMessage = null) }
    fun onFactorChanged(value: String) = _form.update { it.copy(factorString = value, errorMessage = null) }

    fun save() {
        val f = _form.value
        val category = f.selectedCategory ?: run {
            _form.update { it.copy(errorMessage = "Select a category") }
            return
        }
        _form.update { it.copy(isSaving = true, errorMessage = null) }
        viewModelScope.launch {
            val result: Result<*> = if (isEditMode) {
                val existing = editingUnit
                if (existing == null) {
                    _form.update { it.copy(isSaving = false, errorMessage = "Unit not found") }
                    return@launch
                }
                val factor = runCatching { BigDecimal(f.factorString.trim()) }.getOrNull()
                if (factor == null || factor.signum() <= 0) {
                    _form.update { it.copy(isSaving = false, errorMessage = "Factor must be a positive number") }
                    return@launch
                }
                updateCustomUnitUseCase(
                    existing.copy(
                        displayName = f.displayName.trim(),
                        symbol = f.symbol.trim(),
                        factor = factor,
                        aliases = (existing.aliases + listOf(f.displayName.trim().lowercase())).distinct(),
                    )
                )
            } else {
                addCustomUnitUseCase(
                    displayName = f.displayName,
                    symbol = f.symbol,
                    categoryId = category.id,
                    baseUnitId = category.baseUnitId,
                    factorString = f.factorString,
                )
            }
            result.fold(
                onSuccess = { _form.update { s -> s.copy(isSaving = false, savedSuccessfully = true) } },
                onFailure = { err -> _form.update { s -> s.copy(isSaving = false, errorMessage = err.message) } },
            )
        }
    }
}
