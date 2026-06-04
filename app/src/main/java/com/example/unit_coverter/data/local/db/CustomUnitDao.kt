package com.example.unit_coverter.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.unit_coverter.data.local.entity.CustomUnitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomUnitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(unit: CustomUnitEntity)

    @Update
    suspend fun update(unit: CustomUnitEntity)

    @Query("DELETE FROM custom_units WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM custom_units ORDER BY categoryId ASC, displayName ASC")
    fun getAll(): Flow<List<CustomUnitEntity>>

    @Query("SELECT * FROM custom_units WHERE categoryId = :categoryId ORDER BY displayName ASC")
    fun getByCategory(categoryId: String): Flow<List<CustomUnitEntity>>
}
