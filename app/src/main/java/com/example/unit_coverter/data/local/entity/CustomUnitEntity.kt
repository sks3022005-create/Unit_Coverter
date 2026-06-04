package com.example.unit_coverter.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "custom_units")
data class CustomUnitEntity(
    @PrimaryKey val id: String,
    val displayName: String,
    val symbol: String,
    val categoryId: String,
    val baseUnitId: String,
    val factorString: String,    // BigDecimal.toPlainString()
    val aliasesPiped: String,    // "|"-delimited list (avoids JSON dependency in data layer)
    val createdAtMs: Long,
)
