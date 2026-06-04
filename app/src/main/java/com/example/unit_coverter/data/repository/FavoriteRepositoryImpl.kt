package com.example.unit_coverter.data.repository

import com.example.unit_coverter.data.local.db.FavoriteDao
import com.example.unit_coverter.data.local.entity.FavoriteEntity
import com.example.unit_coverter.domain.model.Favorite
import com.example.unit_coverter.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(
    private val dao: FavoriteDao,
) : FavoriteRepository {

    override fun getAll(): Flow<List<Favorite>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override fun isFavorite(
        categoryId: String,
        fromUnitId: String,
        toUnitId: String,
    ): Flow<Boolean> = dao.isFavorite(categoryId, fromUnitId, toUnitId)

    override suspend fun add(favorite: Favorite) {
        dao.insert(favorite.toEntity())
    }

    override suspend fun remove(categoryId: String, fromUnitId: String, toUnitId: String) {
        dao.delete(categoryId, fromUnitId, toUnitId)
    }

    override suspend fun reorder(favorites: List<Favorite>) {
        favorites.forEachIndexed { index, fav ->
            dao.update(fav.copy(orderIndex = index).toEntity())
        }
    }
}

private fun FavoriteEntity.toDomain() = Favorite(
    id = id,
    categoryId = categoryId,
    fromUnitId = fromUnitId,
    toUnitId = toUnitId,
    orderIndex = orderIndex,
    createdAtMs = createdAtMs,
)

private fun Favorite.toEntity() = FavoriteEntity(
    id = id,
    categoryId = categoryId,
    fromUnitId = fromUnitId,
    toUnitId = toUnitId,
    orderIndex = orderIndex,
    createdAtMs = createdAtMs,
)
