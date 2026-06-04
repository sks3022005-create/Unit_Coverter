package com.example.unit_coverter.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.unit_coverter.data.local.entity.CustomUnitEntity
import com.example.unit_coverter.data.local.entity.FavoriteEntity
import com.example.unit_coverter.data.local.entity.HistoryEntryEntity

@Database(
    entities = [
        HistoryEntryEntity::class,
        FavoriteEntity::class,
        CustomUnitEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun customUnitDao(): CustomUnitDao
}
