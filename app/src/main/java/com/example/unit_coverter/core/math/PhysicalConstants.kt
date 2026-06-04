package com.example.unit_coverter.core.math

import java.math.BigDecimal

// ---------------------------------------------------------------------------
// Exact SI / international definitions — all sourced from NIST / BIPM
// ---------------------------------------------------------------------------

internal val GRAVITY_STANDARD: BigDecimal = bd("9.80665")           // m/s² (exact)
internal val POUND_KG: BigDecimal = bd("0.45359237")                 // kg (exact, 1959 intl.)
internal val INCH_M: BigDecimal = bd("0.0254")                       // m (exact)
internal val FOOT_M: BigDecimal = bd("0.3048")                       // m (exact)
internal val MILE_M: BigDecimal = bd("1609.344")                     // m (exact)
internal val NMI_M: BigDecimal = bd("1852")                          // m (exact)
internal val US_GALLON_L: BigDecimal = bd("3.785411784")             // L (exact: 231 in³)
internal val IMP_GALLON_L: BigDecimal = bd("4.54609")                // L (exact, 1985)
internal val ATM_PA: BigDecimal = bd("101325")                       // Pa (exact)
internal val MILE_KM: BigDecimal = bd("1.609344")                    // km (exact)

// ---------------------------------------------------------------------------
// Derived — computed once at startup from the exact definitions above
// ---------------------------------------------------------------------------

/** 1 pound-force = 0.45359237 kg × 9.80665 m/s² (exact) */
internal val LBF_N: BigDecimal = POUND_KG.multiply(GRAVITY_STANDARD, MC)

/** 1 psi = lbf / in² */
internal val PSI_PA: BigDecimal = LBF_N.divide(INCH_M.multiply(INCH_M, MC), MC)

/** 1 ft·lbf (foot-pound of energy) */
internal val FT_LBF_J: BigDecimal = LBF_N.multiply(FOOT_M, MC)

/** 1 mechanical HP = 550 ft·lbf/s */
internal val HP_MECH_W: BigDecimal = bd("550").multiply(FT_LBF_J, MC)

/** 1 metric HP = 75 kgf·m/s = 75 × 9.80665 W */
internal val HP_METRIC_W: BigDecimal = bd("75").multiply(GRAVITY_STANDARD, MC)

/** Reciprocal factor for US MPG ↔ L/100km: 100 × gallons_per_liter_inverse × km_per_mile */
internal val MPG_US_FACTOR: BigDecimal =
    bd("100").multiply(US_GALLON_L, MC).divide(MILE_KM, MC)

/** Reciprocal factor for UK MPG ↔ L/100km */
internal val MPG_UK_FACTOR: BigDecimal =
    bd("100").multiply(IMP_GALLON_L, MC).divide(MILE_KM, MC)

/** Reciprocal factor for miles/L ↔ L/100km */
internal val MILES_PER_L_FACTOR: BigDecimal = bd("100").divide(MILE_KM, MC)

/** π to 50 decimal places */
internal val PI: BigDecimal = bd("3.14159265358979323846264338327950288419716939937510")
