package com.example.unit_coverter.core.math

import com.example.unit_coverter.core.math.ScientificEvaluator.AngleMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class ScientificEvaluatorTest {

    private fun evalDeg(expr: String) = ScientificEvaluator.evaluate(expr, AngleMode.DEGREE)
    private fun evalRad(expr: String) = ScientificEvaluator.evaluate(expr, AngleMode.RADIAN)

    // ── Arithmetic & precedence ───────────────────────────────────────────────

    @Test fun basicArithmetic() {
        assertEquals(7.0, evalRad("1+2×3"), 1e-9)
        assertEquals(1.0, evalRad("(1+2)÷3"), 1e-9)
        assertEquals(-4.0, evalRad("-2^2"), 1e-9)     // unary minus binds looser than ^
        assertEquals(0.25, evalRad("2^-2"), 1e-9)
        assertEquals(512.0, evalRad("2^3^2"), 1e-9)   // right-associative: 2^(3^2)
    }

    @Test fun factorialAndPercent() {
        assertEquals(120.0, evalRad("5!"), 1e-9)
        assertEquals(0.5, evalRad("50%"), 1e-9)
        assertEquals(64.0, evalRad("2^3!"), 1e-9)     // 2^(3!) = 2^6
    }

    // ── Degree-mode trig: the reported bug ────────────────────────────────────

    @Test fun cos90DegreesIsExactlyZero() {
        assertEquals(0.0, evalDeg("cos(90)"), 0.0)    // exact, no epsilon
    }

    @Test fun quadrantalAnglesAreExact() {
        assertEquals(0.0, evalDeg("sin(0)"), 0.0)
        assertEquals(1.0, evalDeg("sin(90)"), 0.0)
        assertEquals(0.0, evalDeg("sin(180)"), 0.0)
        assertEquals(-1.0, evalDeg("sin(270)"), 0.0)
        assertEquals(1.0, evalDeg("cos(0)"), 0.0)
        assertEquals(-1.0, evalDeg("cos(180)"), 0.0)
        assertEquals(0.0, evalDeg("cos(450)"), 0.0)   // wraps to 90°
        assertEquals(0.0, evalDeg("tan(180)"), 0.0)
    }

    @Test fun commonDegreeValues() {
        assertEquals(0.5, evalDeg("sin(30)"), 1e-9)
        assertEquals(1.0, evalDeg("tan(45)"), 1e-9)
        assertEquals(0.5, evalDeg("cos(60)"), 1e-9)
    }

    @Test fun tanUndefinedAtNinetyDegreesThrows() {
        assertThrows(IllegalStateException::class.java) { evalDeg("tan(90)") }
    }

    // ── Radian-mode trig ──────────────────────────────────────────────────────

    @Test fun radianTrig() {
        assertEquals(0.0, evalRad("sin(0)"), 1e-9)
        assertEquals(1.0, evalRad("cos(0)"), 1e-9)
        // cos(pi/2) in radians carries tiny residue; near-zero, cleaned at display.
        assertTrue(kotlin.math.abs(evalRad("cos(π÷2)")) < 1e-9)
    }

    // ── Functions & constants ─────────────────────────────────────────────────

    @Test fun functionsAndConstants() {
        assertEquals(12.0, evalRad("√(144)"), 1e-9)
        assertEquals(3.0, evalRad("log(1000)"), 1e-9)
        assertEquals(1.0, evalRad("ln(e)"), 1e-9)
        assertEquals(10.0, evalRad("log2(1024)"), 1e-9)
        assertEquals(3.0, evalRad("cbrt(27)"), 1e-9)
        assertEquals(5.0, evalRad("abs(-5)"), 1e-9)
        assertEquals(Math.PI, evalRad("π"), 1e-9)
    }

    // ── Error handling ────────────────────────────────────────────────────────

    @Test fun invalidInputsThrow() {
        assertThrows(IllegalStateException::class.java) { evalRad("1÷0") }
        assertThrows(IllegalStateException::class.java) { evalRad("√(-1)") }   // NaN
        assertThrows(IllegalStateException::class.java) { evalRad("2+") }
        assertThrows(IllegalStateException::class.java) { evalRad("(1+2") }
        assertThrows(IllegalStateException::class.java) { evalRad("") }
    }
}
