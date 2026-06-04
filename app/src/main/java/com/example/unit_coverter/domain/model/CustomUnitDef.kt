package com.example.unit_coverter.domain.model

import java.math.BigDecimal

data class CustomUnitDef(
    val id: String,
    val displayName: String,
    val symbol: String,
    val categoryId: String,
    val baseUnitId: String,
    val factor: BigDecimal,
    val aliases: List<String>,
    val createdAtMs: Long,
)
