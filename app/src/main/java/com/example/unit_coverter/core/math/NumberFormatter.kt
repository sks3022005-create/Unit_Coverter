package com.example.unit_coverter.core.math

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

/**
 * Formats BigDecimal values for user-facing display.
 *
 * Rules:
 *   - Zero           → "0"
 *   - |x| ≥ 1E12 or (|x| > 0 and |x| < 1E-6) → engineering notation (exponent ÷ 3)
 *   - Otherwise      → plain decimal, trailing zeros stripped
 *
 * Max 10 significant figures. The ViewModel may override for specific categories.
 */
object NumberFormatter {

    private val DISPLAY_MC = MathContext(10, RoundingMode.HALF_EVEN)
    private val UPPER = BigDecimal("1E12")
    private val LOWER = BigDecimal("0.000001")

    fun format(value: BigDecimal): String {
        if (value.signum() == 0) return "0"

        val rounded = value.round(DISPLAY_MC).stripTrailingZeros()
        val abs = rounded.abs()

        return when {
            abs.compareTo(UPPER) >= 0 ||
                    (abs.compareTo(BigDecimal.ZERO) > 0 && abs.compareTo(LOWER) < 0) ->
                rounded.toEngineeringString()

            else -> rounded.toPlainString()
        }
    }

    /** Formats with a specific number of significant figures (for UI overrides). */
    fun format(value: BigDecimal, sigFigs: Int): String {
        if (value.signum() == 0) return "0"
        val ctx = MathContext(sigFigs, RoundingMode.HALF_EVEN)
        return format(value.round(ctx))
    }
}
