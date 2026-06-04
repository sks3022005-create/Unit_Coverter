package com.example.unit_coverter.domain.usecase

import com.example.unit_coverter.domain.model.Favorite
import com.example.unit_coverter.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
) {
    suspend operator fun invoke(
        categoryId: String,
        fromUnitId: String,
        toUnitId: String,
    ) {
        val isFav = favoriteRepository
            .isFavorite(categoryId, fromUnitId, toUnitId)
            .first()

        if (isFav) {
            favoriteRepository.remove(categoryId, fromUnitId, toUnitId)
        } else {
            favoriteRepository.add(
                Favorite(
                    categoryId = categoryId,
                    fromUnitId = fromUnitId,
                    toUnitId = toUnitId,
                    createdAtMs = System.currentTimeMillis(),
                )
            )
        }
    }
}
