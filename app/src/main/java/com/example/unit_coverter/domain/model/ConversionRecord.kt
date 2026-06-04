package com.example.unit_coverter.domain.model

import java.math.BigDecimal

data class ConversionRecord(
    val id: Long = 0,
    val inputValue: BigDecimal,
    val resultValue: BigDecimal,
    val fromUnitId: String,
    val toUnitId: String,
    val categoryId: String,
    val timestampMs: Long,
)
