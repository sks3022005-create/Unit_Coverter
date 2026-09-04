package com.example.unit_coverter.core.math

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal

class ExpressionEvaluatorTest {

    private fun eval(s: String) = ExpressionEvaluator.evaluate(s)

    private fun assertEval(expr: String, expected: String) {
        assertEquals("$expr", 0, eval(expr).compareTo(BigDecimal(expected)))
    }

    @Test fun `plain number`() = assertEval("42", "42")
    @Test fun `addition`() = assertEval("2+3", "5")
    @Test fun `subtraction`() = assertEval("10-4", "6")
    @Test fun `multiplication`() = assertEval("6*7", "42")
    @Test fun `division`() = assertEval("10/4", "2.5")
    @Test fun `precedence`() = assertEval("2+3*4", "14")
    @Test fun `parentheses override precedence`() = assertEval("(2+3)*4", "20")
    @Test fun `unary minus`() = assertEval("-5+2", "-3")
    @Test fun `double negation`() = assertEval("--5", "5")
    @Test fun `exponent`() = assertEval("2^10", "1024")
    @Test fun `nested parentheses`() = assertEval("((1+2)*(3+4))", "21")
    @Test fun `whitespace is ignored`() = assertEval("  1 +   2 ", "3")
    @Test fun `scientific notation`() = assertEval("1.5e3", "1500")
    @Test fun `decimal arithmetic is exact not binary float`() = assertEval("0.1+0.2", "0.3")

    @Test
    fun `right associative exponent`() {
        // 2^3^2 must be 2^(3^2) = 512, not (2^3)^2 = 64
        assertEval("2^3^2", "512")
    }

    @Test
    fun `division by zero is an error not a crash`() {
        val r = runCatching { eval("1/0") }
        assertTrue("1/0 should fail cleanly", r.isFailure)
    }

    @Test
    fun `unbalanced parenthesis is rejected`() {
        assertTrue(runCatching { eval("(1+2") }.isFailure)
    }

    @Test
    fun `trailing operator is rejected`() {
        assertTrue(runCatching { eval("1+") }.isFailure)
    }

    @Test
    fun `garbage input is rejected`() {
        listOf("abc", "", "  ", "1 2", "*5", "1++", "()").forEach {
            assertTrue("'$it' should be rejected", runCatching { eval(it) }.isFailure)
        }
    }

    @Test
    fun `negative exponent works`() = assertEval("2^-2", "0.25")
}

class NumberFormatterTest {

    private fun fmt(s: String) = NumberFormatter.format(BigDecimal(s))

    @Test fun `zero`() = assertEquals("0", fmt("0"))
    @Test fun `negative zero prints as zero`() = assertEquals("0", fmt("-0.00"))
    @Test fun `integer keeps no decimal point`() = assertEquals("42", fmt("42.000"))
    @Test fun `trailing zeros stripped`() = assertEquals("2.5", fmt("2.5000"))
    @Test fun `negative value`() = assertEquals("-17.25", fmt("-17.25"))

    @Test
    fun `never emits raw scientific notation for everyday magnitudes`() {
        listOf("0.00001", "0.0001", "1234567890", "999999999999").forEach {
            val out = fmt(it)
            assertTrue("$it formatted as '$out' — should be plain", !out.contains("E"))
        }
    }

    @Test
    fun `very large numbers use notation rather than a wall of digits`() {
        val out = fmt("123456789012345678")
        assertTrue("expected notation, got $out", out.contains("E"))
    }

    @Test
    fun `rounds to twelve significant figures`() {
        val out = fmt("3.14159265358979323846")
        assertEquals("3.14159265359", out)
    }

    @Test
    fun `sig fig override`() {
        assertEquals("3.14", NumberFormatter.format(BigDecimal("3.14159265"), 3))
    }
}
