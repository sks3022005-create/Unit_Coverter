package com.example.unit_coverter.core.math

import java.math.BigDecimal

/**
 * Encodes how a unit converts to/from the base unit of its category.
 *
 * All arithmetic uses [MC] (50-digit HALF_EVEN) so division never silently drops precision.
 */
sealed class ConversionFormula {

    abstract fun toBase(value: BigDecimal): BigDecimal
    abstract fun fromBase(value: BigDecimal): BigDecimal

    /** value_base = value × factor  (length, mass, volume, …) */
    data class Linear(val factor: BigDecimal) : ConversionFormula() {
        override fun toBase(value: BigDecimal): BigDecimal = value.multiply(factor, MC)
        override fun fromBase(value: BigDecimal): BigDecimal = value.divide(factor, MC)
    }

    /** value_base = value × factor + offset  (temperature scales with offsets) */
    data class Affine(val factor: BigDecimal, val offset: BigDecimal) : ConversionFormula() {
        override fun toBase(value: BigDecimal): BigDecimal =
            value.multiply(factor, MC).add(offset)
        override fun fromBase(value: BigDecimal): BigDecimal =
            value.subtract(offset).divide(factor, MC)
    }

    /**
     * value_base = factor / value  (fuel consumption: L/100km ↔ MPG).
     * Both directions are identical because the relationship is its own inverse.
     */
    data class Reciprocal(val factor: BigDecimal) : ConversionFormula() {
        override fun toBase(value: BigDecimal): BigDecimal =
            if (value.signum() == 0) BigDecimal.ZERO else factor.divide(value, MC)
        override fun fromBase(value: BigDecimal): BigDecimal =
            if (value.signum() == 0) BigDecimal.ZERO else factor.divide(value, MC)
    }
}
