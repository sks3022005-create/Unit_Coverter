package com.example.unit_coverter.feature.calculator

import androidx.lifecycle.ViewModel
import com.example.unit_coverter.core.math.NumberFormatter
import com.example.unit_coverter.core.math.ScientificEvaluator
import com.example.unit_coverter.core.math.ScientificEvaluator.AngleMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.math.BigDecimal
import java.math.MathContext
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class CalculatorViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(CalculatorUiState())
    val state: StateFlow<CalculatorUiState> = _state.asStateFlow()

    fun onEvent(event: CalculatorEvent) {
        when (event) {
            is CalculatorEvent.Input -> onInput(event.token)
            CalculatorEvent.Evaluate -> onEvaluate()
            CalculatorEvent.Backspace -> onBackspace()
            CalculatorEvent.Clear -> _state.update {
                CalculatorUiState(angleMode = it.angleMode, isSecondFunction = it.isSecondFunction)
            }
            CalculatorEvent.ToggleAngleMode -> _state.update { s ->
                val next = if (s.angleMode == AngleMode.RADIAN) AngleMode.DEGREE else AngleMode.RADIAN
                recompute(s.copy(angleMode = next))
            }
            CalculatorEvent.ToggleSecondFunction -> _state.update {
                it.copy(isSecondFunction = !it.isSecondFunction)
            }
        }
    }

    private fun onInput(token: String) {
        _state.update { s ->
            val base = when {
                // After '=', an operator continues from the result; anything else starts fresh.
                s.isResultFinal && token in CONTINUATION_TOKENS -> s.result
                s.isResultFinal -> ""
                s.isError -> ""
                else -> s.expression
            }
            recompute(
                s.copy(
                    expression = base + token,
                    isResultFinal = false,
                    isError = false,
                ),
            )
        }
    }

    private fun onBackspace() {
        _state.update { s ->
            if (s.expression.isEmpty()) return@update s
            recompute(
                s.copy(
                    expression = s.expression.dropLast(1),
                    isResultFinal = false,
                    isError = false,
                ),
            )
        }
    }

    private fun onEvaluate() {
        _state.update { s ->
            if (s.expression.isBlank()) return@update s
            runCatching { ScientificEvaluator.evaluate(s.expression, s.angleMode) }.fold(
                onSuccess = { value ->
                    s.copy(result = format(value), isResultFinal = true, isError = false)
                },
                onFailure = {
                    s.copy(result = "Error", isResultFinal = false, isError = true)
                },
            )
        }
    }

    /**
     * Recomputes the live preview (or final result, if [state] is finalized) from the
     * current expression without changing what the user typed.
     */
    private fun recompute(state: CalculatorUiState): CalculatorUiState {
        if (state.expression.isBlank()) {
            return state.copy(result = "", isError = false)
        }
        val value = runCatching {
            ScientificEvaluator.evaluate(state.expression, state.angleMode)
        }.getOrNull()
        return if (value != null) {
            state.copy(result = format(value), isError = false)
        } else {
            // Incomplete/invalid mid-typing: hide the preview rather than shout an error.
            state.copy(result = if (state.isResultFinal) state.result else "", isError = false)
        }
    }

    private fun format(value: Double): String {
        // Collapse floating-point residue (e.g. cos in radian mode, or 0.1+0.2-0.3)
        // to a clean zero. The threshold sits far below any value realistically typed
        // on the keypad but comfortably above trig/cancellation noise (~1E-15).
        val cleaned = if (abs(value) < NEAR_ZERO) 0.0 else value
        return NumberFormatter.format(BigDecimal(cleaned).round(DISPLAY_MC))
    }

    private companion object {
        val DISPLAY_MC = MathContext(12)
        const val NEAR_ZERO = 1e-12
        val CONTINUATION_TOKENS = setOf("+", "-", "×", "÷", "^", "!", "%")
    }
}
