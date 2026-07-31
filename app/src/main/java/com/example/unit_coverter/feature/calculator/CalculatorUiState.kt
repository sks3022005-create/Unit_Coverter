package com.example.unit_coverter.feature.calculator

import com.example.unit_coverter.core.math.ScientificEvaluator.AngleMode

data class CalculatorUiState(
    /** The expression the user is building, using display glyphs (×, ÷, π, √). */
    val expression: String = "",
    /** Live preview while typing, or the final value after '='. Empty when nothing to show. */
    val result: String = "",
    /** True once the result is finalized by pressing '='. */
    val isResultFinal: Boolean = false,
    /** True when the last evaluation failed (result holds the message). */
    val isError: Boolean = false,
    val angleMode: AngleMode = AngleMode.RADIAN,
    /** Toggles the keypad between primary functions and their inverses. */
    val isSecondFunction: Boolean = false,
)

sealed interface CalculatorEvent {
    /** Append a token (digit, operator, function stub like "sin(", constant, etc.). */
    data class Input(val token: String) : CalculatorEvent
    data object Evaluate : CalculatorEvent
    data object Clear : CalculatorEvent
    data object Backspace : CalculatorEvent
    data object ToggleAngleMode : CalculatorEvent
    data object ToggleSecondFunction : CalculatorEvent
}
