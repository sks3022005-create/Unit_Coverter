package com.example.unit_coverter.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unit_coverter.domain.model.SearchResult
import com.example.unit_coverter.domain.usecase.SearchUnitsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val results: ImmutableList<SearchResult> = persistentListOf(),
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchUnitsUseCase: SearchUnitsUseCase,
) : ViewModel() {

    private val _query = MutableStateFlow("")

    val uiState = _query
        .debounce(250)
        .map { q -> SearchUiState(query = q, results = searchUnitsUseCase(q).toImmutableList()) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SearchUiState(),
        )

    fun onQueryChange(query: String) {
        _query.value = query
    }
}
