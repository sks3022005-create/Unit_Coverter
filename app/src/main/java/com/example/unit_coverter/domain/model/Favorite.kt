package com.example.unit_coverter.domain.model

data class Favorite(
    val id: Long = 0,
    val categoryId: String,
    val fromUnitId: String,
    val toUnitId: String,
    val orderIndex: Int = 0,
    val createdAtMs: Long,
)
