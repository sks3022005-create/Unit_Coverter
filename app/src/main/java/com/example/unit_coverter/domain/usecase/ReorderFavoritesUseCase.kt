package com.example.unit_coverter.domain.usecase

import com.example.unit_coverter.domain.model.Favorite
import com.example.unit_coverter.domain.repository.FavoriteRepository
import javax.inject.Inject

class ReorderFavoritesUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
) {
    /** Persists the new ordering. [favorites] should be the full list in the desired order. */
    suspend operator fun invoke(favorites: List<Favorite>) =
        favoriteRepository.reorder(favorites)
}
