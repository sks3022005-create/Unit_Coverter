package com.example.unit_coverter.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.unit_coverter.core.registry.UnitCategory
import kotlinx.collections.immutable.ImmutableList

@Composable
fun CategorySelector(
    categories: ImmutableList<UnitCategory>,
    selectedCategory: UnitCategory?,
    onCategorySelected: (UnitCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()

    val selectedIndex = categories.indexOfFirst { it.id == selectedCategory?.id }

    LaunchedEffect(selectedIndex) {
        if (selectedIndex >= 0) listState.animateScrollToItem(selectedIndex)
    }

    LazyRow(
        state = listState,
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        items(categories, key = { it.id }) { category ->
            FilterChip(
                selected = category.id == selectedCategory?.id,
                onClick = { onCategorySelected(category) },
                label = { Text(category.displayName) },
            )
        }
    }
}
