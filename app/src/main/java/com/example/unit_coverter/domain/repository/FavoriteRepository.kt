package com.example.unit_coverter.domain.repository

import com.example.unit_coverter.domain.model.Favorite
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun getAll(): Flow<List<Favorite>>
    fun isFavorite(categoryId: String, fromUnitId: String, toUnitId: String): Flow<Boolean>
    suspend fun add(favorite: Favorite)
    suspend fun remove(categoryId: String, fromUnitId: String, toUnitId: String)
    suspend fun reorder(favorites: List<Favorite>)
}
