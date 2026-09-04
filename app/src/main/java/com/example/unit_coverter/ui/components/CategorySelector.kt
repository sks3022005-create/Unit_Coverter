package com.example.unit_coverter.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.example.unit_coverter.core.registry.UnitCategory
import com.example.unit_coverter.ui.theme.categoryPalette
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

    // Centre the selected chip instead of jamming it against the left edge, so the
    // neighbouring categories stay discoverable.
    LaunchedEffect(selectedIndex) {
        if (selectedIndex >= 0) {
            val viewport = listState.layoutInfo.viewportSize.width
            val item = listState.layoutInfo.visibleItemsInfo
                .firstOrNull { it.index == selectedIndex }?.size ?: 0
            val offset = if (viewport > 0 && item > 0) -(viewport - item) / 2 else 0
            listState.animateScrollToItem(selectedIndex, offset)
        }
    }

    LazyRow(
        state = listState,
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        items(categories, key = { it.id }) { category ->
            val selected = category.id == selectedCategory?.id
            val palette = categoryPalette(category.id)

            // Selected chip wears its category's colour; unselected chips keep a
            // tinted icon so the row still reads as colourful at a glance.
            val labelColor by animateColorAsState(
                targetValue = if (selected) palette.onContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                label = "categoryChipLabel",
            )
            val iconColor by animateColorAsState(
                targetValue = if (selected) palette.onContainer else palette.accent,
                label = "categoryChipIcon",
            )
            val containerColor by animateColorAsState(
                targetValue = if (selected) palette.container else MaterialTheme.colorScheme.surfaceContainerHigh,
                label = "categoryChipBg",
            )
            val scale by animateFloatAsState(
                targetValue = if (selected) 1.06f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow,
                ),
                label = "categoryChipScale",
            )

            FilterChip(
                selected = selected,
                onClick = { onCategorySelected(category) },
                modifier = Modifier.scale(scale),
                label = {
                    Text(
                        text = category.displayName,
                        style = MaterialTheme.typography.labelLarge,
                        color = labelColor,
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = categoryIcon(category.id),
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(FilterChipDefaults.IconSize),
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = containerColor,
                    selectedContainerColor = containerColor,
                ),
                border = null,
                shape = MaterialTheme.shapes.small,
            )
        }
    }
}
