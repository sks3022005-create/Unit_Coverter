package com.example.unit_coverter.core.registry

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

/**
 * Structural invariants that must hold for every category shipped in the app.
 * These catch copy-paste errors in the unit tables long before a user sees them.
 */
class UnitRegistryIntegrityTest {

    private val cmp = MathContext(12, RoundingMode.HALF_EVEN)

    @Test
    fun `every category has at least two units`() {
        UnitRegistry.categories.forEach { cat ->
            assertTrue("${cat.id} has < 2 units", cat.units.size >= 2)
        }
    }

    @Test
    fun `unit ids are globally unique`() {
        val dupes = UnitRegistry.allUnits
            .groupBy { it.second.id }
            .filterValues { it.size > 1 }
            .keys
        assertTrue("Duplicate unit ids: $dupes", dupes.isEmpty())
    }

    @Test
    fun `category ids are unique`() {
        val dupes = UnitRegistry.categories.groupBy { it.id }.filterValues { it.size > 1 }.keys
        assertTrue("Duplicate category ids: $dupes", dupes.isEmpty())
    }

    @Test
    fun `declared base unit exists and is the identity`() {
        UnitRegistry.categories.forEach { cat ->
            val base = cat.units.firstOrNull { it.id == cat.baseUnitId }
                ?: fail("${cat.id}: baseUnitId '${cat.baseUnitId}' not found in units").let { return@forEach }
            val one = BigDecimal.ONE
            val there = base.formula.toBase(one)
            assertEquals(
                "${cat.id}: base unit ${base.id} must map 1 -> 1",
                0,
                there.round(cmp).compareTo(one),
            )
        }
    }

    @Test
    fun `round trip through base is lossless to 12 significant figures`() {
        val samples = listOf("1", "0.001", "37.5", "12345.6789")
        UnitRegistry.categories.forEach { cat ->
            cat.units.forEach { unit ->
                samples.forEach { s ->
                    val v = BigDecimal(s)
                    val back = unit.formula.fromBase(unit.formula.toBase(v))
                    assertEquals(
                        "${cat.id}/${unit.id}: round trip failed for $s (got $back)",
                        0,
                        back.round(cmp).compareTo(v.round(cmp)),
                    )
                }
            }
        }
    }

    @Test
    fun `no unit has a blank id symbol or display name`() {
        UnitRegistry.allUnits.forEach { (cat, u) ->
            assertTrue("${cat.id}: blank id", u.id.isNotBlank())
            assertTrue("${cat.id}/${u.id}: blank symbol", u.symbol.isNotBlank())
            assertTrue("${cat.id}/${u.id}: blank displayName", u.displayName.isNotBlank())
        }
    }

    @Test
    fun `aliases are lowercase and non blank`() {
        UnitRegistry.allUnits.forEach { (cat, u) ->
            u.aliases.forEach { a ->
                assertTrue("${cat.id}/${u.id}: blank alias", a.isNotBlank())
                assertEquals("${cat.id}/${u.id}: alias '$a' must be lowercase", a.lowercase(), a)
            }
        }
    }

    /**
     * An alias that maps to two different units inside the SAME category is always a bug —
     * the parser would silently pick whichever unit was declared first.
     */
    @Test
    fun `no alias collides within a category`() {
        UnitRegistry.categories.forEach { cat ->
            val seen = mutableMapOf<String, String>()
            cat.units.forEach { u ->
                (u.aliases + u.symbol.lowercase() + u.id).forEach { key ->
                    val prev = seen.put(key, u.id)
                    if (prev != null && prev != u.id) {
                        fail("${cat.id}: alias '$key' maps to both '$prev' and '${u.id}'")
                    }
                }
            }
        }
    }

    /**
     * Cross-category alias collisions are tolerated only when they are explicitly
     * documented here, because the parser resolves them first-match-wins.
     */
    @Test
    fun `cross category alias collisions are all known and accepted`() {
        val accepted = setOf(
            // symbol/alias        winner is documented in AmbiguousAliases
            "c", "b", "kb", "nm", "a", "t", "mm", "ma", "cal", "pt", "min", "gal",
            "oz", "s", "m", "in", "g", "k", "f", "r", "l", "n", "w", "j", "pa",
            "mb", "kn", "d", "h", "au", "ha", "ac", "hp", "ps", "grad", "rad",
        )
        val byAlias = mutableMapOf<String, MutableSet<String>>()
        UnitRegistry.allUnits.forEach { (cat, u) ->
            (u.aliases + u.symbol.lowercase()).forEach { key ->
                byAlias.getOrPut(key) { mutableSetOf() }.add("${cat.id}/${u.id}")
            }
        }
        val unexpected = byAlias
            .filterValues { it.size > 1 }
            .filterKeys { it !in accepted }
        assertTrue(
            "Undocumented cross-category alias collisions: $unexpected",
            unexpected.isEmpty(),
        )
    }
}
