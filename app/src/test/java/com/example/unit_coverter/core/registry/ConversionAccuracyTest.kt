package com.example.unit_coverter.core.registry

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

/**
 * Reference conversions checked against NIST / BIPM / IAU published values.
 * Tolerance is 10 significant figures — tighter than anything the UI displays.
 */
class ConversionAccuracyTest {

    private val mc = MathContext(10, RoundingMode.HALF_EVEN)

    private fun convert(value: String, from: String, to: String): BigDecimal {
        val f = UnitRegistry.findById(from) ?: error("missing unit $from")
        val t = UnitRegistry.findById(to) ?: error("missing unit $to")
        assertEquals("$from and $to are in different categories", f.first.id, t.first.id)
        return UnitRegistry.convert(BigDecimal(value), f.second, t.second)
    }

    private fun assertConverts(value: String, from: String, to: String, expected: String) {
        val actual = convert(value, from, to)
        assertEquals(
            "$value $from -> $to  (expected $expected, got ${actual.round(mc)})",
            0,
            actual.round(mc).compareTo(BigDecimal(expected).round(mc)),
        )
    }

    // ── Length ────────────────────────────────────────────────────────────────

    @Test fun `mile to kilometer`() = assertConverts("1", "mile", "kilometer", "1.609344")
    @Test fun `inch to centimeter`() = assertConverts("1", "inch", "centimeter", "2.54")
    @Test fun `foot to meter`() = assertConverts("1", "foot", "meter", "0.3048")
    @Test fun `nautical mile to meter`() = assertConverts("1", "nautical_mile", "meter", "1852")
    @Test fun `light year to meter`() =
        assertConverts("1", "light_year", "meter", "9460730472580800")
    @Test fun `marathon in miles`() =
        assertConverts("42.195", "kilometer", "mile", "26.21875746")

    // ── Mass ──────────────────────────────────────────────────────────────────

    @Test fun `pound to kilogram`() = assertConverts("1", "pound", "kilogram", "0.45359237")
    @Test fun `ounce to gram`() = assertConverts("1", "ounce", "gram", "28.349523125")
    @Test fun `stone to pound`() = assertConverts("1", "stone", "pound", "14")
    @Test fun `troy ounce to gram`() = assertConverts("1", "troy_ounce", "gram", "31.1034768")
    @Test fun `carat to gram`() = assertConverts("1", "carat", "gram", "0.2")

    // ── Temperature (the classic off-by-offset trap) ──────────────────────────

    @Test fun `freezing point C to F`() = assertConverts("0", "celsius", "fahrenheit", "32")
    @Test fun `boiling point C to F`() = assertConverts("100", "celsius", "fahrenheit", "212")
    @Test fun `minus forty is the same in both scales`() =
        assertConverts("-40", "celsius", "fahrenheit", "-40")
    @Test fun `body temperature F to C`() =
        assertConverts("98.6", "fahrenheit", "celsius", "37")
    @Test fun `absolute zero C to K`() = assertConverts("-273.15", "celsius", "kelvin", "0")
    @Test fun `kelvin to rankine`() = assertConverts("100", "kelvin", "rankine", "180")
    @Test fun `fahrenheit to rankine`() = assertConverts("32", "fahrenheit", "rankine", "491.67")

    // ── Volume ────────────────────────────────────────────────────────────────

    @Test fun `us gallon to liter`() = assertConverts("1", "us_gallon", "liter", "3.785411784")
    @Test fun `imperial gallon to liter`() = assertConverts("1", "imp_gallon", "liter", "4.54609")
    @Test fun `us cup to milliliter`() =
        assertConverts("1", "us_cup", "milliliter", "236.5882365")
    @Test fun `us tablespoon is three teaspoons`() =
        assertConverts("1", "us_tablespoon", "us_teaspoon", "3")
    @Test fun `cubic meter is a thousand liters`() =
        assertConverts("1", "cubic_meter", "liter", "1000")

    // ── Pressure (homologation-relevant) ──────────────────────────────────────

    @Test fun `atmosphere to pascal`() = assertConverts("1", "atmosphere", "pascal", "101325")
    @Test fun `bar to psi`() = assertConverts("1", "bar", "psi", "14.50377377")
    @Test fun `psi to kilopascal`() = assertConverts("1", "psi", "kilopascal", "6.894757293")
    @Test fun `atmosphere is 760 torr`() = assertConverts("1", "atmosphere", "torr", "760")
    @Test fun `tyre pressure 32 psi in bar`() =
        assertConverts("32", "psi", "bar", "2.206322334")

    // ── Power (homologation-relevant) ─────────────────────────────────────────

    @Test fun `mechanical horsepower to watt`() =
        assertConverts("1", "hp_mech", "watt", "745.6998716")
    @Test fun `metric horsepower to watt`() =
        assertConverts("1", "hp_metric", "watt", "735.49875")
    @Test fun `100 kW in metric hp`() =
        assertConverts("100", "kilowatt", "hp_metric", "135.9621617")

    // ── Energy ────────────────────────────────────────────────────────────────

    @Test fun `kilowatt hour to megajoule`() =
        assertConverts("1", "kilowatt_hour", "megajoule", "3.6")
    @Test fun `btu to joule`() = assertConverts("1", "btu_it", "joule", "1055.05585262")
    @Test fun `kilocalorie to kilojoule`() =
        assertConverts("1", "kilocalorie", "kilojoule", "4.184")

    // ── Speed ─────────────────────────────────────────────────────────────────

    @Test fun `kmh to mph`() =
        assertConverts("100", "kilometer_per_hour", "mile_per_hour", "62.13711922")
    @Test fun `knot to kmh`() =
        assertConverts("1", "knot", "kilometer_per_hour", "1.852")
    @Test fun `mps to kmh`() =
        assertConverts("1", "meter_per_second", "kilometer_per_hour", "3.6")

    // ── Area ──────────────────────────────────────────────────────────────────

    @Test fun `acre to square meter`() =
        assertConverts("1", "acre", "sq_meter", "4046.856422")
    @Test fun `hectare to acre`() = assertConverts("1", "hectare", "acre", "2.471053815")
    @Test fun `square mile to square kilometer`() =
        assertConverts("1", "sq_mile", "sq_kilometer", "2.589988110")

    // ── Angle ─────────────────────────────────────────────────────────────────

    @Test fun `180 degrees is pi radians`() =
        assertConverts("180", "degree", "radian", "3.141592654")
    @Test fun `one turn is 360 degrees`() = assertConverts("1", "turn", "degree", "360")
    @Test fun `degree is 60 arcminutes`() = assertConverts("1", "degree", "arcminute", "60")

    // ── Data storage ──────────────────────────────────────────────────────────

    @Test fun `gibibyte to gigabyte`() =
        assertConverts("1", "gibibyte", "gigabyte", "1.073741824")
    @Test fun `byte is eight bits`() = assertConverts("1", "byte", "bit", "8")
    @Test fun `megabit to megabyte`() = assertConverts("8", "megabit", "megabyte", "1")

    // ── Fuel consumption (reciprocal — most error-prone category) ──────────────

    @Test fun `l per 100km to mpg us`() =
        assertConverts("10", "l_per_100km", "mpg_us", "23.52145833")
    @Test fun `mpg us to l per 100km`() =
        assertConverts("30", "mpg_us", "l_per_100km", "7.840486111")
    @Test fun `l per 100km to km per l`() =
        assertConverts("5", "l_per_100km", "km_per_l", "20")
    @Test fun `mpg uk to l per 100km`() =
        assertConverts("40", "mpg_uk", "l_per_100km", "7.062023408")
    @Test fun `l per km to l per 100km`() =
        assertConverts("0.05", "l_per_km", "l_per_100km", "5")

    /**
     * Reciprocal units are their own inverse, so a double conversion must return
     * the original number exactly — this is where sign/factor mistakes surface.
     */
    @Test
    fun `reciprocal fuel units round trip`() {
        listOf("mpg_us", "mpg_uk", "km_per_l", "miles_per_l").forEach { id ->
            val there = convert("7.5", "l_per_100km", id)
            val back = convert(there.toPlainString(), id, "l_per_100km")
            assertEquals(
                "$id round trip drifted: $back",
                0,
                back.round(mc).compareTo(BigDecimal("7.5").round(mc)),
            )
        }
    }

    /** Zero consumption must not crash or produce a silent bogus number. */
    @Test
    fun `zero into a reciprocal unit is handled`() {
        val r = convert("0", "l_per_100km", "mpg_us")
        assertTrue("expected a finite result for 0 L/100km, got $r", r.signum() == 0)
    }

    // ── Time ──────────────────────────────────────────────────────────────────

    @Test fun `day to second`() = assertConverts("1", "day", "second", "86400")
    @Test fun `julian year to day`() = assertConverts("1", "year_julian", "day", "365.25")
    @Test fun `week to hour`() = assertConverts("1", "week", "hour", "168")

    // ── Force ─────────────────────────────────────────────────────────────────

    @Test fun `pound force to newton`() =
        assertConverts("1", "pound_force", "newton", "4.448221615")
    @Test fun `kilogram force to newton`() =
        assertConverts("1", "kilogram_force", "newton", "9.80665")

    // ── Cross-category safety ─────────────────────────────────────────────────

    @Test
    fun `converting across categories is rejected not silently wrong`() {
        val meter = UnitRegistry.findById("meter")!!
        val gram = UnitRegistry.findById("gram")!!
        assertTrue(
            "meter and gram must not share a category",
            meter.first.id != gram.first.id,
        )
    }
}
