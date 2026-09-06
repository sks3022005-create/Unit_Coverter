package com.example.unit_coverter.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.unit_coverter.core.registry.UnitCategory
import com.example.unit_coverter.ui.theme.categoryPalette
import kotlinx.collections.immutable.ImmutableList

private val RailWidth = 84.dp

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
        if (selectedIndex >= 0) {
            val viewport = listState.layoutInfo.viewportSize.height
            val item = listState.layoutInfo.visibleItemsInfo
                .firstOrNull { it.index == selectedIndex }?.size ?: 0
            val offset = if (viewport > 0 && item > 0) -(viewport - item) / 2 else 0
            listState.animateScrollToItem(selectedIndex, offset)
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier
            .width(RailWidth)
            .fillMaxHeight()
            .semantics { contentDescription = "Unit categories" },
        verticalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        items(categories, key = { it.id }) { category ->
            val isSelected = category.id == selectedCategory?.id
            val palette = categoryPalette(category.id)

            val labelColor by animateColorAsState(
                targetValue = if (isSelected) palette.onContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                label = "categoryRailLabel",
            )
            val iconColor by animateColorAsState(
                targetValue = if (isSelected) palette.onContainer else palette.accent,
                label = "categoryRailIcon",
            )
            val containerColor by animateColorAsState(
                targetValue = if (isSelected) palette.container else MaterialTheme.colorScheme.surfaceContainerHigh,
                label = "categoryRailBg",
            )
            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.04f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow,
                ),
                label = "categoryRailScale",
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(scale)
                    .clip(RoundedCornerShape(12.dp))
                    .background(containerColor)
                    .clickable { onCategorySelected(category) }
                    .semantics {
                        role = Role.Tab
                        selected = isSelected
                        contentDescription = category.displayName
                    }
                    .padding(vertical = 8.dp, horizontal = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = categoryIcon(category.id),
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp),
                )
                Text(
                    text = shortCategoryLabel(category.displayName),
                    style = MaterialTheme.typography.labelSmall,
                    color = labelColor,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}

/** Keep rail labels short so a 84dp column still reads. */
internal fun shortCategoryLabel(displayName: String): String = when (displayName) {
    "Mass / Weight" -> "Mass"
    "Fuel Consumption" -> "Fuel"
    "Data Storage" -> "Data"
    "Temperature" -> "Temp"
    else -> displayName
}
