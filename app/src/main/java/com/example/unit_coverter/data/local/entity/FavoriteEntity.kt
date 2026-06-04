package com.example.unit_coverter.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "favorites",
    indices = [Index(value = ["categoryId", "fromUnitId", "toUnitId"], unique = true)],
)
data class FavoriteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val categoryId: String,
    val fromUnitId: String,
    val toUnitId: String,
    val orderIndex: Int = 0,
    val createdAtMs: Long,
)
