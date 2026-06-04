package com.example.unit_coverter.core.math

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

/** 50-digit precision used for all intermediate conversion arithmetic. */
internal val MC = MathContext(50, RoundingMode.HALF_EVEN)

/** Constructs a BigDecimal from a literal string — never from Double. */
internal fun bd(s: String): BigDecimal = BigDecimal(s)
