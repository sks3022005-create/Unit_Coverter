package com.example.unit_coverter.core.cooking

import com.example.unit_coverter.core.registry.UnitRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

class CookingConverterTest {

    private val mc = MathContext(8, RoundingMode.HALF_EVEN)
    private fun unit(id: String) = UnitRegistry.findById(id)!!.second
    private fun ingredient(id: String) = INGREDIENTS.first { it.id == id }

    private fun convert(v: String, from: String, to: String, ing: String) =
        CookingConverter.convert(BigDecimal(v), unit(from), unit(to), ingredient(ing))

    @Test
    fun `every cooking unit id exists in the registry`() {
        CookingConverter.ALL_UNIT_IDS.forEach { id ->
            assertTrue("cooking unit '$id' missing from registry", UnitRegistry.findById(id) != null)
        }
    }

    @Test
    fun `mass unit ids really are mass and volume ids really are volume`() {
        CookingConverter.MASS_UNIT_IDS.forEach {
            assertEquals("$it should be mass", "mass", UnitRegistry.findById(it)!!.first.id)
        }
        CookingConverter.VOLUME_UNIT_IDS.forEach {
            assertEquals("$it should be volume", "volume", UnitRegistry.findById(it)!!.first.id)
        }
    }

    @Test
    fun `ingredient ids are unique`() {
        val dupes = INGREDIENTS.groupBy { it.id }.filterValues { it.size > 1 }.keys
        assertTrue("duplicate ingredient ids: $dupes", dupes.isEmpty())
    }

    @Test
    fun `every ingredient has a positive density`() {
        INGREDIENTS.forEach {
            assertTrue("${it.id} has non-positive density", it.densityGPerMl.signum() > 0)
        }
    }

    @Test
    fun `one ml of water is one gram`() {
        val r = convert("1", "milliliter", "gram", "water").getOrThrow()
        assertEquals(0, r.round(mc).compareTo(BigDecimal.ONE.round(mc)))
    }

    @Test
    fun `one us cup of water is 236 point 59 grams`() {
        val r = convert("1", "us_cup", "gram", "water").getOrThrow()
        assertEquals(0, r.round(mc).compareTo(BigDecimal("236.58824").round(mc)))
    }

    @Test
    fun `one cup of all purpose flour is about 125 grams`() {
        val r = convert("1", "us_cup", "gram", "flour_ap").getOrThrow()
        // 236.5882365 mL x 0.528 g/mL = 124.92... g
        assertTrue("expected ~125 g, got $r", r.toDouble() in 120.0..130.0)
    }

    @Test
    fun `mass to volume is the inverse of volume to mass`() {
        listOf("water", "flour_ap", "honey", "butter", "sugar_white").forEach { ing ->
            val grams = convert("2", "us_cup", "gram", ing).getOrThrow()
            val back = convert(grams.toPlainString(), "gram", "us_cup", ing).getOrThrow()
            assertEquals(
                "$ing: round trip drifted (got $back)",
                0,
                back.round(mc).compareTo(BigDecimal("2").round(mc)),
            )
        }
    }

    @Test
    fun `same dimension conversion ignores density`() {
        val withWater = convert("1", "us_cup", "milliliter", "water").getOrThrow()
        val withHoney = convert("1", "us_cup", "milliliter", "honey").getOrThrow()
        assertEquals("density must not affect volume->volume", 0, withWater.compareTo(withHoney))
    }

    @Test
    fun `denser ingredient weighs more per cup`() {
        val honey = convert("1", "us_cup", "gram", "honey").getOrThrow()
        val flour = convert("1", "us_cup", "gram", "flour_ap").getOrThrow()
        assertTrue("honey ($honey g) should outweigh flour ($flour g)", honey > flour)
    }

    @Test
    fun `every ingredient converts without error in both directions`() {
        INGREDIENTS.forEach { ing ->
            val a = CookingConverter.convert(BigDecimal("1"), unit("us_cup"), unit("gram"), ing)
            val b = CookingConverter.convert(BigDecimal("100"), unit("gram"), unit("us_cup"), ing)
            assertTrue("${ing.id} vol->mass failed: ${a.exceptionOrNull()}", a.isSuccess)
            assertTrue("${ing.id} mass->vol failed: ${b.exceptionOrNull()}", b.isSuccess)
        }
    }

    @Test
    fun `zero converts to zero without error`() {
        val r = convert("0", "us_cup", "gram", "water")
        assertTrue(r.isSuccess)
        assertEquals(0, r.getOrThrow().signum())
    }
}
