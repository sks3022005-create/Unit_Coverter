package com.example.unit_coverter.domain.usecase

import com.example.unit_coverter.core.registry.UnitCategory
import com.example.unit_coverter.core.registry.UnitDef
import com.example.unit_coverter.core.registry.UnitRegistry
import com.example.unit_coverter.domain.model.SearchResult
import javax.inject.Inject

class SearchUnitsUseCase @Inject constructor() {

    operator fun invoke(query: String): List<SearchResult> {
        if (query.isBlank()) return emptyList()
        val q = query.trim().lowercase()
        return UnitRegistry.allUnits
            .mapNotNull { (cat, unit) ->
                val s = score(q, cat, unit)
                if (s > 0) SearchResult(cat, unit, s) else null
            }
            .sortedWith(compareByDescending<SearchResult> { it.score }
                .thenBy { it.category.displayName }
                .thenBy { it.unit.displayName })
    }

    private fun score(q: String, cat: UnitCategory, unit: UnitDef): Int {
        var best = 0

        fun check(s: String) {
            val sl = s.lowercase()
            best = when {
                sl == q -> maxOf(best, 100)
                sl.startsWith(q) -> maxOf(best, 70)
                sl.contains(q) -> maxOf(best, 40)
                else -> best
            }
        }

        check(unit.displayName)
        check(unit.symbol)
        unit.aliases.forEach { check(it) }
        if (cat.displayName.lowercase().contains(q)) best = maxOf(best, 20)

        return best
    }
}
