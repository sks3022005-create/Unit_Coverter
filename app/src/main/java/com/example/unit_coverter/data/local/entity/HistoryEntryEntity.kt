package com.example.unit_coverter.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "history",
    indices = [Index(value = ["timestampMs"])],
)
data class HistoryEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val inputValue: String,      // BigDecimal.toPlainString() — never Double
    val resultValue: String,
    val fromUnitId: String,
    val toUnitId: String,
    val categoryId: String,
    val timestampMs: Long,
)
