package com.example.unit_coverter.core.math

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

/**
 * Formats BigDecimal values for user-facing display.
 *
 * Rules:
 *   - Zero                                   → "0"
 *   - |x| ≥ 1E15, or 0 < |x| < 1E-6          → engineering notation (exponent ÷ 3)
 *   - Otherwise                              → plain decimal, trailing zeros stripped
 *
 * Display precision is 12 significant figures. This matters: at 10 figures a legitimate
 * 12-digit input such as 999999999999 was rounded up to 1E12 and then rendered in
 * scientific notation — the user saw a different number than they typed.
 */
object NumberFormatter {

    /** Significant figures retained for display. */
    const val DISPLAY_SIG_FIGS = 12

    private val DISPLAY_MC = MathContext(DISPLAY_SIG_FIGS, RoundingMode.HALF_EVEN)
    private val UPPER = BigDecimal("1E15")
    private val LOWER = BigDecimal("0.000001")

    fun format(value: BigDecimal): String = render(value.round(DISPLAY_MC))

    /** Formats with a specific number of significant figures (for UI overrides). */
    fun format(value: BigDecimal, sigFigs: Int): String {
        require(sigFigs > 0) { "sigFigs must be positive" }
        return render(value.round(MathContext(sigFigs, RoundingMode.HALF_EVEN)))
    }

    private fun render(rounded: BigDecimal): String {
        if (rounded.signum() == 0) return "0"

        val stripped = rounded.stripTrailingZeros()
        val abs = stripped.abs()

        return if (abs >= UPPER || abs < LOWER) {
            stripped.toEngineeringString()
        } else {
            stripped.toPlainString()
        }
    }
}
