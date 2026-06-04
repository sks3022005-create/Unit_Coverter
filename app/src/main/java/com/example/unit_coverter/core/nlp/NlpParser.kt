package com.example.unit_coverter.core.nlp

import com.example.unit_coverter.core.math.MC
import com.example.unit_coverter.core.registry.UnitCategory
import com.example.unit_coverter.core.registry.UnitRegistry
import java.math.BigDecimal

/**
 * Regex-based natural-language parser for unit conversion inputs.
 *
 * Supported patterns:
 *   "5 feet"                 → single value + unit
 *   "5 feet to cm"           → with explicit target unit
 *   "5 ft 11 in"             → compound (summed to base unit)
 *   "5 ft 11 in to cm"       → compound with target
 *   "32f to c" / "32°F"      → fused number+unit / degree symbol
 *   "5'11\""                 → feet+inches height notation
 *   "half a cup"             → fraction word
 *
 * Separator: "to", "into", "→", "->". "in" is NOT a separator (it matches inches).
 *
 * Custom-unit changes invalidate the alias cache via [invalidateCache].
 */
object NlpParser {

    @Volatile private var cachedIndex: Map<String, String>? = null

    /** Call when custom units change so the alias index is rebuilt on next parse. */
    fun invalidateCache() {
        cachedIndex = null
    }

    private val unitAliasIndex: Map<String, String>
        get() = cachedIndex ?: buildAliasIndex().also { cachedIndex = it }

    fun parse(input: String): NlpResult? {
        val norm = preprocess(input.trim())
        if (norm.isBlank()) return null

        val (fromPart, toPart) = splitAtSeparator(norm)

        val fromPairs = extractValueUnitPairs(fromPart) ?: return null
        if (fromPairs.isEmpty()) return null

        // All from-side units must belong to the same category.
        val cat = fromPairs
            .mapNotNull { (_, uid) -> UnitRegistry.findById(uid)?.first }
            .distinct()
            .singleOrNull() ?: return null

        val (finalValue, finalUnitId) = when {
            fromPairs.size == 1 -> fromPairs[0].first to fromPairs[0].second
            else -> {
                // Compound: sum each component to the category's base unit.
                var sum = BigDecimal.ZERO
                for ((v, uid) in fromPairs) {
                    val unit = cat.units.firstOrNull { it.id == uid } ?: return null
                    sum = sum.add(unit.formula.toBase(v), MC)
                }
                sum to cat.baseUnitId
            }
        }

        val toUnitId = toPart?.let { extractSingleUnit(it) }

        return NlpResult(
            numericValue = finalValue,
            fromUnitId = finalUnitId,
            categoryId = cat.id,
            toUnitId = toUnitId,
            originalInput = input.trim(),
        )
    }

    // -----------------------------------------------------------------------
    // Preprocessing
    // -----------------------------------------------------------------------

    private fun preprocess(s: String): String = s
        .replace(Regex("(\\d)'(\\d+)\""), "$1 feet $2 inches") // 5'11"
        .replace(Regex("(\\d)'"), "$1 feet")                    // 5' alone
        .replace(Regex("(\\d)\""), "$1 inches")                 // 11" alone
        .replace("°", " ")                                       // 32°F → "32 F"

    // -----------------------------------------------------------------------
    // Splitting on separator
    // -----------------------------------------------------------------------

    private val SEPARATOR_RE = Regex("""(?i)\s+(?:to|into|→|->)\s+""")

    private fun splitAtSeparator(norm: String): Pair<String, String?> {
        val m = SEPARATOR_RE.find(norm) ?: return norm to null
        return norm.substring(0, m.range.first) to norm.substring(m.range.last + 1)
    }

    // -----------------------------------------------------------------------
    // Token-level parsing
    // -----------------------------------------------------------------------

    private fun extractValueUnitPairs(text: String): List<Pair<BigDecimal, String>>? {
        val tokens = text.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
        val pairs = mutableListOf<Pair<BigDecimal, String>>()
        var pending: BigDecimal? = null

        for (token in tokens) {
            val t = token.lowercase()

            // Pure fillers
            if (t == "of" || t == "and") continue

            // "a/an" — implicit 1 if no pending number, else skip as filler
            if (t == "a" || t == "an") {
                if (pending == null) pending = BigDecimal.ONE
                continue
            }

            // Fused number+unit: "5ft", "1.5kg", "32F"
            val fused = trySplitFused(token)
            if (fused != null) {
                if (pending != null) return null // dangling number before fused token
                pairs += fused
                continue
            }

            // Pure number or fraction word
            val num = parseNumberToken(token)
            if (num != null) {
                if (pending != null) return null // two consecutive numbers
                pending = num
                continue
            }

            // Unit word (must follow a number)
            val unitId = matchUnit(token)
            if (unitId != null) {
                val n = pending ?: return null // unit without preceding number
                pairs += n to unitId
                pending = null
                continue
            }

            return null // unrecognized token
        }

        if (pending != null) return null // dangling number with no unit
        return pairs.ifEmpty { null }
    }

    private fun extractSingleUnit(text: String): String? =
        text.trim().split(Regex("\\s+")).firstNotNullOfOrNull { matchUnit(it) }

    // Handles "5ft", "1.5kg", "32F" — number immediately followed by unit letters.
    private fun trySplitFused(token: String): Pair<BigDecimal, String>? {
        val m = Regex("^([+-]?[\\d,]+(?:\\.\\d+)?)(°?[a-zA-Z][a-zA-Z²³/.]*)$")
            .find(token) ?: return null
        val num = parseNumberToken(m.groupValues[1]) ?: return null
        val unitId = matchUnit(m.groupValues[2]) ?: return null
        return num to unitId
    }

    private fun matchUnit(token: String): String? {
        val t = token.lowercase()
        val idx = unitAliasIndex
        idx[t]?.let { return it }
        // Strip trailing 's' for simple plurals not already indexed
        if (t.length > 3) idx[t.dropLast(1)]?.let { return it }
        return null
    }

    private fun parseNumberToken(token: String): BigDecimal? {
        fractionWord(token.lowercase())?.let { return it }
        return runCatching { BigDecimal(token.replace(",", "")) }.getOrNull()
    }

    private fun fractionWord(t: String): BigDecimal? = when (t) {
        "half"   -> BigDecimal("0.5")
        "quarter"-> BigDecimal("0.25")
        "third"  -> BigDecimal.ONE.divide(BigDecimal("3"), MC)
        "one"    -> BigDecimal.ONE
        "two"    -> BigDecimal("2")
        "three"  -> BigDecimal("3")
        "four"   -> BigDecimal("4")
        "five"   -> BigDecimal("5")
        "six"    -> BigDecimal("6")
        "seven"  -> BigDecimal("7")
        "eight"  -> BigDecimal("8")
        "nine"   -> BigDecimal("9")
        "ten"    -> BigDecimal("10")
        "dozen"  -> BigDecimal("12")
        else     -> null
    }

    // -----------------------------------------------------------------------
    // Alias index
    // -----------------------------------------------------------------------

    private fun buildAliasIndex(): Map<String, String> {
        val map = mutableMapOf<String, String>()
        for ((_, unit) in UnitRegistry.allUnits) {
            fun add(key: String) { if (key.isNotBlank()) map.putIfAbsent(key.lowercase(), unit.id) }
            add(unit.id)
            add(unit.symbol)
            add(unit.displayName)
            unit.aliases.forEach { add(it) }
        }
        return map
    }
}
