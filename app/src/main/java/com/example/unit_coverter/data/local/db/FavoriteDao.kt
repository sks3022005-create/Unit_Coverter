package com.example.unit_coverter.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.unit_coverter.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(favorite: FavoriteEntity): Long

    @Update
    suspend fun update(favorite: FavoriteEntity)

    @Query(
        "DELETE FROM favorites " +
        "WHERE categoryId = :categoryId AND fromUnitId = :fromId AND toUnitId = :toId"
    )
    suspend fun delete(categoryId: String, fromId: String, toId: String)

    @Query("SELECT * FROM favorites ORDER BY orderIndex ASC, createdAtMs DESC")
    fun getAll(): Flow<List<FavoriteEntity>>

    @Query(
        "SELECT EXISTS(SELECT 1 FROM favorites " +
        "WHERE categoryId = :categoryId AND fromUnitId = :fromId AND toUnitId = :toId)"
    )
    fun isFavorite(categoryId: String, fromId: String, toId: String): Flow<Boolean>
}
