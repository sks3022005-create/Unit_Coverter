package com.example.unit_coverter.domain.usecase

import com.example.unit_coverter.domain.model.Favorite
import com.example.unit_coverter.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoritesUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
) {
    operator fun invoke(): Flow<List<Favorite>> = favoriteRepository.getAll()
}
