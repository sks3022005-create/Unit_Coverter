package com.example.unit_coverter.feature.currency

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unit_coverter.data.currency.CURRENCIES
import com.example.unit_coverter.data.currency.CurrencyInfo
import com.example.unit_coverter.data.currency.CurrencyRates
import com.example.unit_coverter.data.currency.CurrencyRepository
import com.example.unit_coverter.data.currency.currencyInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CurrencyUiState(
    val amountText: String = "1",
    val fromCode: String = "USD",
    val toCode: String = "INR",
    val resultText: String = "",
    val unitRateText: String = "",
    val rates: CurrencyRates? = null,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val lastUpdatedLabel: String = "",
    val isStale: Boolean = false,
) {
    val availableCurrencies: List<CurrencyInfo>
        get() {
            val known = rates?.usdRates?.keys ?: return CURRENCIES
            // Curated list first (so common currencies stay at the top), then any
            // extra codes the provider supports.
            val curated = CURRENCIES.filter { it.code in known }
            val extra = known.filterNot { code -> CURRENCIES.any { it.code == code } }
                .sorted()
                .map { currencyInfo(it) }
            return curated + extra
        }
}

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val repository: CurrencyRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(CurrencyUiState())
    val state: StateFlow<CurrencyUiState> = _state.asStateFlow()

    init {
        // Show whatever is cached immediately, then refresh in the background so
        // the screen is never blank while waiting on the network.
        viewModelScope.launch {
            repository.cachedRates.collect { cached ->
                if (cached != null) {
                    _state.update { it.copy(rates = cached) }
                    recompute()
                }
            }
        }
        refresh()
    }

    fun refresh() {
        if (_state.value.isRefreshing) return
        _state.update { it.copy(isRefreshing = true, errorMessage = null) }
        viewModelScope.launch {
            repository.refresh().fold(
                onSuccess = { rates ->
                    _state.update { it.copy(rates = rates, isRefreshing = false) }
                    recompute()
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(isRefreshing = false, errorMessage = error.message)
                    }
                },
            )
        }
    }

    fun onAmountChanged(text: String) {
        _state.update { it.copy(amountText = text) }
        recompute()
    }

    fun onFromSelected(code: String) {
        _state.update { it.copy(fromCode = code) }
        recompute()
    }

    fun onToSelected(code: String) {
        _state.update { it.copy(toCode = code) }
        recompute()
    }

    fun swap() {
        _state.update { it.copy(fromCode = it.toCode, toCode = it.fromCode) }
        recompute()
    }

    private fun recompute() {
        val s = _state.value
        val rates = s.rates ?: return
        val amount = s.amountText.trim().replace(",", "").toDoubleOrNull()

        val ageMillis = System.currentTimeMillis() - s.fetchedAtSafe()
        val hours = ageMillis / 3_600_000L
        val label = when {
            s.rates == null -> ""
            hours < 1 -> "Updated just now"
            hours < 24 -> "Updated ${hours}h ago"
            else -> "Updated ${hours / 24}d ago"
        }

        if (amount == null) {
            _state.update {
                it.copy(
                    resultText = "",
                    unitRateText = "",
                    lastUpdatedLabel = label,
                    isStale = hours >= 24,
                )
            }
            return
        }

        val converted = rates.convert(amount, s.fromCode, s.toCode)
        val unit = rates.unitRate(s.fromCode, s.toCode)

        _state.update {
            it.copy(
                resultText = converted?.let(::formatMoney).orEmpty(),
                unitRateText = unit?.let { r ->
                    "1 ${s.fromCode} = ${formatMoney(r)} ${s.toCode}"
                }.orEmpty(),
                lastUpdatedLabel = label,
                isStale = hours >= 24,
                errorMessage = null,
            )
        }
    }

    private fun CurrencyUiState.fetchedAtSafe(): Long =
        rates?.fetchedAtMillis ?: System.currentTimeMillis()

    /**
     * Money formatting: two decimals for ordinary amounts, more precision for
     * very small values (some currencies trade in tiny fractions of a dollar)
     * and thousands separators for readability.
     */
    private fun formatMoney(value: Double): String {
        val abs = kotlin.math.abs(value)
        val decimals = when {
            abs == 0.0 -> 2
            abs < 0.01 -> 6
            abs < 1 -> 4
            else -> 2
        }
        return String.format("%,.${decimals}f", value)
    }
}
