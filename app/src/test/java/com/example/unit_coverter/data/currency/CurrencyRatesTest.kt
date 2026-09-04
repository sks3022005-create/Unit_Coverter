package com.example.unit_coverter.data.currency

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Currency math is derived from USD-based rates, so every non-USD pair is a
 * two-step calculation. These lock down the arithmetic and the failure modes —
 * a wrong cross rate is the kind of bug a user would only notice by losing money.
 */
class CurrencyRatesTest {

    // Realistic figures from the live API (2026-09), used as fixtures.
    private val rates = CurrencyRates(
        usdRates = mapOf(
            "USD" to 1.0,
            "INR" to 94.540498,
            "EUR" to 0.860629,
            "GBP" to 0.739834,
            "JPY" to 156.019704,
            "AED" to 3.6725,
        ),
        fetchedAtMillis = 1_788_480_151_000L,
    )

    private fun assertClose(expected: Double, actual: Double?, tol: Double = 1e-6) {
        assertNotNull("expected a result, got null", actual)
        assertEquals(expected, actual!!, tol)
    }

    @Test
    fun `usd to inr uses the raw rate`() {
        assertClose(94.540498, rates.convert(1.0, "USD", "INR"))
    }

    @Test
    fun `inr back to usd is the inverse`() {
        assertClose(1.0, rates.convert(94.540498, "INR", "USD"))
    }

    @Test
    fun `cross rate goes through usd`() {
        // 1 EUR -> USD -> INR  ==  94.540498 / 0.860629
        assertClose(94.540498 / 0.860629, rates.convert(1.0, "EUR", "INR"))
    }

    @Test
    fun `cross rate is symmetric`() {
        val there = rates.convert(250.0, "GBP", "JPY")!!
        val back = rates.convert(there, "JPY", "GBP")!!
        assertEquals(250.0, back, 1e-9)
    }

    @Test
    fun `same currency is an identity even for odd amounts`() {
        listOf(0.0, 1.0, 12345.678, -42.5).forEach {
            assertEquals(it, rates.convert(it, "EUR", "EUR")!!, 0.0)
        }
    }

    @Test
    fun `zero converts to zero`() {
        assertClose(0.0, rates.convert(0.0, "USD", "INR"))
    }

    @Test
    fun `negative amounts are preserved`() {
        assertClose(-94.540498, rates.convert(-1.0, "USD", "INR"))
    }

    @Test
    fun `unknown currency codes return null rather than a wrong number`() {
        assertNull(rates.convert(1.0, "USD", "XYZ"))
        assertNull(rates.convert(1.0, "XYZ", "USD"))
        assertNull(rates.convert(1.0, "AAA", "BBB"))
    }

    @Test
    fun `a zero rate cannot produce infinity`() {
        val broken = CurrencyRates(mapOf("USD" to 1.0, "BAD" to 0.0), 0L)
        val result = broken.convert(10.0, "BAD", "USD")
        assertNull("a zero source rate must fail cleanly, not divide by zero", result)
    }

    @Test
    fun `unit rate matches converting exactly one`() {
        assertEquals(rates.convert(1.0, "USD", "INR"), rates.unitRate("USD", "INR"))
    }

    @Test
    fun `available codes are sorted and complete`() {
        val codes = rates.availableCodes
        assertEquals(codes.sorted(), codes)
        assertTrue(codes.containsAll(listOf("USD", "INR", "EUR", "GBP", "JPY", "AED")))
    }

    @Test
    fun `large amounts stay precise enough for money`() {
        // 1,000,000 USD in INR — must not drift into floating-point noise.
        assertClose(94_540_498.0, rates.convert(1_000_000.0, "USD", "INR"), 0.01)
    }
}

class CurrencyMetadataTest {

    @Test
    fun `currency codes are unique`() {
        val dupes = CURRENCIES.groupBy { it.code }.filterValues { it.size > 1 }.keys
        assertTrue("duplicate currency codes: $dupes", dupes.isEmpty())
    }

    @Test
    fun `every currency has a code name and symbol`() {
        CURRENCIES.forEach {
            assertTrue("blank code", it.code.isNotBlank())
            assertTrue("${it.code}: blank name", it.displayName.isNotBlank())
            assertTrue("${it.code}: blank symbol", it.symbol.isNotBlank())
            assertEquals("${it.code} should be a 3-letter ISO code", 3, it.code.length)
            assertEquals("${it.code} should be uppercase", it.code.uppercase(), it.code)
        }
    }

    @Test
    fun `common currencies are present`() {
        val codes = CURRENCIES.map { it.code }
        listOf("USD", "EUR", "INR", "GBP", "JPY", "AED").forEach {
            assertTrue("$it missing from the picker list", it in codes)
        }
    }

    @Test
    fun `unknown code falls back to a usable entry instead of crashing`() {
        val info = currencyInfo("ZZZ")
        assertEquals("ZZZ", info.code)
        assertTrue(info.displayName.isNotBlank())
    }

    @Test
    fun `known code resolves to full metadata`() {
        val inr = currencyInfo("INR")
        assertEquals("Indian Rupee", inr.displayName)
        assertEquals("₹", inr.symbol)
    }
}

class ExchangeRateResponseTest {

    private val json = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }

    @Test
    fun `parses a successful payload`() {
        val body = """
            {"result":"success","base_code":"USD",
             "time_last_update_unix":1788480151,
             "rates":{"USD":1,"INR":94.540498,"EUR":0.860629}}
        """.trimIndent()
        val parsed = json.decodeFromString<ExchangeRateResponse>(body)
        assertTrue(parsed.isSuccess)
        assertEquals("USD", parsed.baseCode)
        assertEquals(94.540498, parsed.rates["INR"]!!, 1e-9)
    }

    @Test
    fun `unknown fields do not break parsing`() {
        val body = """
            {"result":"success","base_code":"USD","some_new_field":"whatever",
             "rates":{"USD":1,"INR":94.5}}
        """.trimIndent()
        val parsed = json.decodeFromString<ExchangeRateResponse>(body)
        assertTrue(parsed.isSuccess)
    }

    @Test
    fun `an error payload is not treated as success`() {
        val body = """{"result":"error","error-type":"unsupported-code","rates":{}}"""
        val parsed = json.decodeFromString<ExchangeRateResponse>(body)
        assertTrue("error payload must not report success", !parsed.isSuccess)
    }

    @Test
    fun `empty rates are not success even when result says success`() {
        val body = """{"result":"success","base_code":"USD","rates":{}}"""
        val parsed = json.decodeFromString<ExchangeRateResponse>(body)
        assertTrue("empty rates are unusable", !parsed.isSuccess)
    }
}
