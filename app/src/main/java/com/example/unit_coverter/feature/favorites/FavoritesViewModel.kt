package com.example.unit_coverter.feature.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unit_coverter.core.registry.UnitRegistry
import com.example.unit_coverter.domain.model.Favorite
import com.example.unit_coverter.domain.usecase.GetFavoritesUseCase
import com.example.unit_coverter.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FavoriteItem(
    val favorite: Favorite,
    val categoryName: String,
    val fromUnitName: String,
    val toUnitName: String,
)

data class FavoritesUiState(
    val items: ImmutableList<FavoriteItem> = persistentListOf(),
    val isLoading: Boolean = true,
)

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    getFavoritesUseCase: GetFavoritesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
) : ViewModel() {

    val uiState = getFavoritesUseCase()
        .map { favs ->
            FavoritesUiState(
                items = favs.map { it.toItem() }.toImmutableList(),
                isLoading = false,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FavoritesUiState(),
        )

    fun removeFavorite(favorite: Favorite) {
        viewModelScope.launch {
            toggleFavoriteUseCase(favorite.categoryId, favorite.fromUnitId, favorite.toUnitId)
        }
    }

    private fun Favorite.toItem(): FavoriteItem {
        val fromUnit = UnitRegistry.findById(fromUnitId)
        val toUnit = UnitRegistry.findById(toUnitId)
        val category = UnitRegistry.categoryById(categoryId)
        return FavoriteItem(
            favorite = this,
            categoryName = category?.displayName ?: categoryId,
            fromUnitName = fromUnit?.second?.displayName ?: fromUnitId,
            toUnitName = toUnit?.second?.displayName ?: toUnitId,
        )
    }
}
