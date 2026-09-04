package com.example.unit_coverter.feature.converter

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Pure-logic mirror of the keypad edit rules in [ConverterViewModel.editInput].
 *
 * The ViewModel itself needs Hilt and Android plumbing to instantiate, so the
 * transformations are duplicated here as pure functions and asserted directly.
 * If the rules in the ViewModel change, these must change with them — they exist
 * to pin down the edge cases that are easy to break: a second decimal point, a
 * leading ".", and sign toggling on an empty field.
 */
class KeypadInputRulesTest {

    // ── The rules under test, matching ConverterViewModel ─────────────────────

    private fun appendDigit(current: String, digit: Char) = current + digit

    private fun appendDecimal(current: String): String {
        val body = current.removePrefix("-")
        return when {
            body.contains('.') -> current
            body.isEmpty() -> if (current.startsWith("-")) "-0." else "0."
            else -> "$current."
        }
    }

    private fun backspace(current: String) = current.dropLast(1)

    private fun toggleSign(current: String) =
        if (current.startsWith("-")) current.removePrefix("-") else "-$current"

    // ── Digits ───────────────────────────────────────────────────────────────

    @Test
    fun `digits append in order`() {
        var s = ""
        "123".forEach { s = appendDigit(s, it) }
        assertEquals("123", s)
    }

    @Test
    fun `leading zeros are preserved as typed`() {
        // "007" is a legitimate thing to type; the formatter handles display.
        var s = ""
        "007".forEach { s = appendDigit(s, it) }
        assertEquals("007", s)
    }

    // ── Decimal point ────────────────────────────────────────────────────────

    @Test
    fun `decimal on empty input yields a leading zero`() {
        assertEquals("0.", appendDecimal(""))
    }

    @Test
    fun `decimal appends to a whole number`() {
        assertEquals("42.", appendDecimal("42"))
    }

    @Test
    fun `a second decimal point is rejected`() {
        assertEquals("3.14", appendDecimal("3.14"))
        assertEquals("0.", appendDecimal("0."))
    }

    @Test
    fun `decimal on a bare minus yields minus zero point`() {
        assertEquals("-0.", appendDecimal("-"))
    }

    @Test
    fun `decimal works on a negative number`() {
        assertEquals("-5.", appendDecimal("-5"))
    }

    @Test
    fun `second decimal rejected on a negative number`() {
        assertEquals("-5.5", appendDecimal("-5.5"))
    }

    // ── Backspace ────────────────────────────────────────────────────────────

    @Test
    fun `backspace removes the last character`() {
        assertEquals("12", backspace("123"))
        assertEquals("3.", backspace("3.1"))
    }

    @Test
    fun `backspace on empty stays empty and does not crash`() {
        assertEquals("", backspace(""))
    }

    @Test
    fun `backspace can empty the field completely`() {
        var s = "7"
        s = backspace(s)
        assertEquals("", s)
    }

    // ── Sign ─────────────────────────────────────────────────────────────────

    @Test
    fun `toggle sign adds and removes a minus`() {
        assertEquals("-5", toggleSign("5"))
        assertEquals("5", toggleSign("-5"))
    }

    @Test
    fun `toggle sign twice is the identity`() {
        listOf("1", "3.14", "0", "").forEach {
            assertEquals(it, toggleSign(toggleSign(it)))
        }
    }

    @Test
    fun `toggle sign on empty produces a lone minus`() {
        assertEquals("-", toggleSign(""))
    }

    @Test
    fun `toggle sign preserves decimals`() {
        assertEquals("-12.75", toggleSign("12.75"))
    }

    // ── Sequences a user would actually type ─────────────────────────────────

    @Test
    fun `typing a decimal value produces a parseable number`() {
        var s = ""
        s = appendDigit(s, '1')
        s = appendDigit(s, '2')
        s = appendDecimal(s)
        s = appendDigit(s, '5')
        assertEquals("12.5", s)
        assertEquals(12.5, s.toDouble(), 1e-9)
    }

    @Test
    fun `typing then correcting yields the corrected value`() {
        var s = ""
        "199".forEach { s = appendDigit(s, it) }
        s = backspace(s)
        s = appendDigit(s, '8')
        assertEquals("198", s)
    }

    @Test
    fun `a negative decimal round trips through Double`() {
        var s = ""
        s = appendDigit(s, '4')
        s = appendDecimal(s)
        s = appendDigit(s, '2')
        s = toggleSign(s)
        assertEquals("-4.2", s)
        assertEquals(-4.2, s.toDouble(), 1e-9)
    }

    @Test
    fun `every reachable state is either empty a sign or parseable`() {
        // Fuzz the rules; the field must never reach a state that would throw
        // when the ViewModel hands it to the expression evaluator.
        val rng = java.util.Random(20260904)
        var s = ""
        repeat(4000) {
            s = when (rng.nextInt(4)) {
                0 -> appendDigit(s, ('0' + rng.nextInt(10)))
                1 -> appendDecimal(s)
                2 -> backspace(s)
                else -> toggleSign(s)
            }
            val trailing = s.isEmpty() || s == "-" || s.endsWith(".")
            if (!trailing) {
                // Anything else must be a valid number.
                assertEquals(
                    "unparseable keypad state: '$s'",
                    true,
                    s.toBigDecimalOrNull() != null,
                )
            }
        }
    }

    private fun String.toBigDecimalOrNull(): java.math.BigDecimal? =
        runCatching { java.math.BigDecimal(this) }.getOrNull()
}

class InputHintTest {

    /** Mirrors [inputHintFor] in ConverterScreen. */
    private fun hint(categoryId: String?): String = when (categoryId) {
        "length" -> "Enter any number or decimal  ·  or type 5 ft 11 in"
        else -> "Enter any number or decimal"
    }

    @Test
    fun `length advertises compound entry`() {
        assertEquals("Enter any number or decimal  ·  or type 5 ft 11 in", hint("length"))
    }

    @Test
    fun `categories without compound support do not mention feet and inches`() {
        listOf("energy", "power", "force", "time", "speed", "mass", "data_storage").forEach {
            val h = hint(it)
            assertEquals("Enter any number or decimal", h)
            assert(!h.contains("ft")) { "$it should not advertise 'ft'" }
        }
    }

    @Test
    fun `unknown category falls back to the plain hint`() {
        assertEquals("Enter any number or decimal", hint(null))
        assertEquals("Enter any number or decimal", hint("custom_thing"))
    }
}
