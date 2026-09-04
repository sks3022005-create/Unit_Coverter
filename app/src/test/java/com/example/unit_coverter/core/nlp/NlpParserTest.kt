package com.example.unit_coverter.core.nlp

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

class NlpParserTest {

    private val mc = MathContext(10, RoundingMode.HALF_EVEN)

    private fun parse(s: String) = NlpParser.parse(s)

    private fun assertParses(
        input: String,
        value: String,
        fromUnitId: String,
        toUnitId: String? = null,
    ) {
        val r = parse(input) ?: run {
            throw AssertionError("'$input' failed to parse")
        }
        assertEquals("$input: value", 0, r.numericValue.round(mc).compareTo(BigDecimal(value).round(mc)))
        assertEquals("$input: from unit", fromUnitId, r.fromUnitId)
        if (toUnitId != null) assertEquals("$input: to unit", toUnitId, r.toUnitId)
    }

    // ── Basic forms ───────────────────────────────────────────────────────────

    @Test fun `value and unit`() = assertParses("5 feet", "5", "foot")
    @Test fun `value unit to unit`() = assertParses("5 feet to cm", "5", "foot", "centimeter")
    @Test fun `fused number and unit`() = assertParses("5ft", "5", "foot")
    @Test fun `decimal value`() = assertParses("1.5 kg", "1.5", "kilogram")
    @Test fun `thousands separator`() = assertParses("1,500 m", "1500", "meter")
    @Test fun `into separator`() = assertParses("10 km into miles", "10", "kilometer", "mile")
    @Test fun `arrow separator`() = assertParses("10 km -> miles", "10", "kilometer", "mile")

    // ── Temperature quirks ────────────────────────────────────────────────────

    @Test fun `degree symbol`() = assertParses("32°F", "32", "fahrenheit")
    @Test fun `fused temperature to target`() =
        assertParses("32f to c", "32", "fahrenheit", "celsius")
    @Test fun `negative temperature`() = assertParses("-40 c", "-40", "celsius")

    // ── Compound / height notation ────────────────────────────────────────────

    @Test
    fun `feet and inches compound sums to base`() {
        val r = parse("5 ft 11 in")
        assertNotNull("compound failed to parse", r)
        // 5 ft + 11 in = 1.8034 m
        assertEquals(
            0,
            r!!.numericValue.round(mc).compareTo(BigDecimal("1.8034").round(mc)),
        )
        assertEquals("meter", r.fromUnitId)
    }

    @Test
    fun `height notation with quotes`() {
        val r = parse("5'11\"")
        assertNotNull("5'11\" failed to parse", r)
        assertEquals(
            0,
            r!!.numericValue.round(mc).compareTo(BigDecimal("1.8034").round(mc)),
        )
    }

    @Test
    fun `compound with target unit`() {
        val r = parse("5 ft 11 in to cm")
        assertNotNull(r)
        assertEquals("centimeter", r!!.toUnitId)
    }

    // ── Word numbers ──────────────────────────────────────────────────────────

    @Test fun `half a cup`() = assertParses("half a cup", "0.5", "us_cup")
    @Test fun `a single unit implies one`() = assertParses("a mile", "1", "mile")
    @Test fun `word number`() = assertParses("three liters", "3", "liter")

    // ── The 'in' trap: 'in' is inches, never a separator ──────────────────────

    @Test
    fun `in is treated as inches not a separator`() {
        val r = parse("12 in")
        assertNotNull(r)
        assertEquals("inch", r!!.fromUnitId)
    }

    // ── Rejection cases: must return null, never a wrong guess ────────────────

    @Test
    fun `nonsense returns null`() {
        listOf("", "   ", "hello world", "5", "feet", "5 blorks", "to cm", "5 5 ft")
            .forEach { assertNull("'$it' should not parse", parse(it)) }
    }

    @Test
    fun `mixed category compound is rejected`() {
        // 5 ft 3 kg is not a meaningful quantity
        assertNull(parse("5 ft 3 kg"))
    }

    @Test
    fun `plural forms resolve`() {
        listOf("kilometers", "miles", "grams", "liters", "hours").forEach {
            assertNotNull("'5 $it' should parse", parse("5 $it"))
        }
    }

    @Test
    fun `case is ignored`() {
        assertNotNull(parse("5 FEET TO CM"))
        assertNotNull(parse("5 Feet To Cm"))
    }

    @Test
    fun `parser never throws on arbitrary input`() {
        val fuzz = listOf(
            "!!!", "5 ft to", "^^^", "1e999 m", "----", "5''", "°°°",
            "5 ft 5 ft 5 ft", ",,,", "5,,,5 m", "0/0", "1 1 1 1",
        )
        fuzz.forEach { s ->
            val r = runCatching { parse(s) }
            assertTrue("parser threw on '$s': ${r.exceptionOrNull()}", r.isSuccess)
        }
    }

    @Test
    fun `parsed result reports the correct category`() {
        assertEquals("length", parse("5 feet")!!.categoryId)
        assertEquals("mass", parse("5 kg")!!.categoryId)
        assertEquals("temperature", parse("5 celsius")!!.categoryId)
    }
}
