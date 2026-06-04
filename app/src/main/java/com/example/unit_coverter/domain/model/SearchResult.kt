package com.example.unit_coverter.domain.model

import com.example.unit_coverter.core.registry.UnitCategory
import com.example.unit_coverter.core.registry.UnitDef

data class SearchResult(
    val category: UnitCategory,
    val unit: UnitDef,
    /** 100 = exact match, 70 = prefix match, 40 = contains, 20 = category name match. */
    val score: Int,
)
