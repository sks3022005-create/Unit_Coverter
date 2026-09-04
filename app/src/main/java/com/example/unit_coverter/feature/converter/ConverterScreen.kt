package com.example.unit_coverter.feature.converter

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.unit_coverter.core.registry.UnitCategory
import com.example.unit_coverter.core.registry.UnitDef
import com.example.unit_coverter.ui.components.CategorySelector
import com.example.unit_coverter.ui.components.UnitPickerBottomSheet
import com.example.unit_coverter.ui.theme.CategoryPalette
import com.example.unit_coverter.ui.theme.categoryPalette
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConverterScreen(
    modifier: Modifier = Modifier,
    onNavigateToCooking: () -> Unit = {},
    onNavigateToCustomUnits: () -> Unit = {},
    onNavigateToSearch: () -> Unit = {},
    viewModel: ConverterViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.isLoading) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    var showFromPicker by rememberSaveable { mutableStateOf(false) }
    var showToPicker by rememberSaveable { mutableStateOf(false) }
    var menuExpanded by rememberSaveable { mutableStateOf(false) }
    val fromSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val toSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val clipboard = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val haptics = LocalHapticFeedback.current

    // The whole screen re-tints when the category changes. Animating each channel
    // means the transition reads as one continuous shift rather than a hard cut.
    val target = categoryPalette(state.selectedCategory?.id)
    val accent by animateColorAsState(target.accent, tween(420), label = "accent")
    val accentSecondary by animateColorAsState(target.accentSecondary, tween(420), label = "accent2")
    val container by animateColorAsState(target.container, tween(420), label = "container")
    val onContainer by animateColorAsState(target.onContainer, tween(420), label = "onContainer")
    val palette = CategoryPalette(accent, accentSecondary, container, onContainer)

    // Quarter-turn nudge on the swap control each time the units flip.
    var swapCount by rememberSaveable { mutableStateOf(0) }
    val swapRotation by animateFloatAsState(
        targetValue = swapCount * 180f,
        label = "swapRotation",
    )

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Convert", style = MaterialTheme.typography.titleLarge) },
                actions = {
                    IconButton(onClick = onNavigateToSearch) {
                        Icon(Icons.Default.Search, contentDescription = "Search units")
                    }
                    IconButton(onClick = onNavigateToCooking) {
                        Icon(Icons.Default.Kitchen, contentDescription = "Cooking converter")
                    }
                    IconButton(
                        onClick = { viewModel.onEvent(ConverterEvent.ToggleFavorite) },
                        enabled = state.fromUnit != null && state.toUnit != null,
                    ) {
                        Icon(
                            imageVector = if (state.isFavorite) {
                                Icons.Default.Favorite
                            } else {
                                Icons.Default.FavoriteBorder
                            },
                            contentDescription = if (state.isFavorite) {
                                "Remove from favorites"
                            } else {
                                "Add to favorites"
                            },
                            tint = if (state.isFavorite) {
                                palette.accent
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                        )
                    }
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More options")
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text("Custom units") },
                            onClick = {
                                menuExpanded = false
                                onNavigateToCustomUnits()
                            },
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(Modifier.height(8.dp))

            CategorySelector(
                categories = state.categories,
                selectedCategory = state.selectedCategory,
                onCategorySelected = { viewModel.onEvent(ConverterEvent.SelectCategory(it)) },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(20.dp))

            // ── Input ────────────────────────────────────────────────────────
            InputCard(
                unit = state.fromUnit,
                category = state.selectedCategory,
                inputText = state.inputText,
                errorMessage = state.errorMessage,
                onInputChanged = { viewModel.onEvent(ConverterEvent.InputChanged(it)) },
                onUnitPickerClicked = { showFromPicker = true },
                onClearClicked = { viewModel.onEvent(ConverterEvent.ClearInput) },
                palette = palette,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            )

            // ── Swap ─────────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                FilledIconButton(
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        swapCount++
                        viewModel.onEvent(ConverterEvent.SwapUnits)
                    },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = palette.container,
                        contentColor = palette.onContainer,
                    ),
                    modifier = Modifier
                        .size(48.dp)
                        .semantics { contentDescription = "Swap units" },
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapVert,
                        contentDescription = null,
                        modifier = Modifier.rotate(swapRotation),
                    )
                }
            }

            // ── Result ───────────────────────────────────────────────────────
            ResultCard(
                unit = state.toUnit,
                category = state.selectedCategory,
                resultText = state.resultText,
                hasError = state.errorMessage != null,
                palette = palette,
                onUnitPickerClicked = { showToPicker = true },
                onCopy = {
                    val value = state.resultText
                    if (value.isNotEmpty()) {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        clipboard.setText(AnnotatedString(value))
                        scope.launch {
                            snackbarHostState.showSnackbar("Copied $value")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            )

            Spacer(Modifier.height(32.dp))
        }
    }

    if (showFromPicker && state.selectedCategory != null) {
        UnitPickerBottomSheet(
            category = state.selectedCategory!!,
            selectedUnit = state.fromUnit,
            sheetState = fromSheetState,
            onUnitSelected = { viewModel.onEvent(ConverterEvent.SelectFromUnit(it)) },
            onDismiss = { showFromPicker = false },
        )
    }

    if (showToPicker && state.selectedCategory != null) {
        UnitPickerBottomSheet(
            category = state.selectedCategory!!,
            selectedUnit = state.toUnit,
            sheetState = toSheetState,
            onUnitSelected = { viewModel.onEvent(ConverterEvent.SelectToUnit(it)) },
            onDismiss = { showToPicker = false },
        )
    }
}

/** The unit selector shown at the top of each card. */
@Composable
private fun UnitSelectorButton(
    unit: UnitDef?,
    category: UnitCategory?,
    onClick: () -> Unit,
    contentColor: Color,
) {
    TextButton(onClick = onClick, enabled = category != null) {
        Text(
            text = unit?.displayName ?: "Select unit",
            style = MaterialTheme.typography.titleSmall,
            color = contentColor,
        )
        if (!unit?.symbol.isNullOrEmpty()) {
            Spacer(Modifier.width(4.dp))
            Text(
                text = "(${unit!!.symbol})",
                style = MaterialTheme.typography.bodySmall,
                color = contentColor.copy(alpha = 0.7f),
            )
        }
        Spacer(Modifier.width(2.dp))
        Icon(
            imageVector = Icons.Default.UnfoldMore,
            contentDescription = null,
            tint = contentColor.copy(alpha = 0.7f),
            modifier = Modifier.size(18.dp),
        )
    }
}

@Composable
private fun InputCard(
    unit: UnitDef?,
    category: UnitCategory?,
    inputText: String,
    errorMessage: String?,
    onInputChanged: (String) -> Unit,
    onUnitPickerClicked: () -> Unit,
    onClearClicked: () -> Unit,
    palette: CategoryPalette,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "FROM",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                UnitSelectorButton(
                    unit = unit,
                    category = category,
                    onClick = onUnitPickerClicked,
                    contentColor = palette.accent,
                )
            }

            OutlinedTextField(
                value = inputText,
                onValueChange = onInputChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "Input value" },
                textStyle = MaterialTheme.typography.headlineSmall,
                placeholder = {
                    Text(
                        text = "0",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    )
                },
                isError = errorMessage != null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done,
                ),
                trailingIcon = {
                    if (inputText.isNotEmpty()) {
                        IconButton(onClick = onClearClicked) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear input",
                            )
                        }
                    }
                },
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = palette.accent,
                    cursorColor = palette.accent,
                ),
            )

            // Error text is animated in so the layout doesn't jump on every keystroke.
            AnimatedVisibility(
                visible = errorMessage != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
            ) {
                Row(
                    modifier = Modifier.padding(top = 8.dp, start = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = errorMessage.orEmpty(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }

            Spacer(Modifier.height(4.dp))
            Text(
                text = "Try  5 ft 11 in to cm  ·  2^10  ·  32°F",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                modifier = Modifier.padding(start = 4.dp),
            )
        }
    }
}

/**
 * The answer. This is the reason the app exists, so it gets the strongest
 * surface, the largest type and a one-tap copy action — rather than being a
 * read-only text field that looks like something the user failed to edit.
 */
@Composable
private fun ResultCard(
    unit: UnitDef?,
    category: UnitCategory?,
    resultText: String,
    hasError: Boolean,
    palette: CategoryPalette,
    onUnitPickerClicked: () -> Unit,
    onCopy: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val hasResult = resultText.isNotEmpty() && !hasError

    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Box(modifier = Modifier.background(palette.softGradient, MaterialTheme.shapes.large)) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "TO",
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.onContainer.copy(alpha = 0.75f),
                )
                UnitSelectorButton(
                    unit = unit,
                    category = category,
                    onClick = onUnitPickerClicked,
                    contentColor = palette.onContainer,
                )
            }

            Spacer(Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    AnimatedContent(
                        targetState = if (hasResult) resultText else "—",
                        transitionSpec = {
                            (slideInVertically { it / 3 } + fadeIn()) togetherWith
                                (slideOutVertically { -it / 3 } + fadeOut())
                        },
                        label = "resultValue",
                    ) { value ->
                        Text(
                            text = value,
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.SansSerif,
                            color = if (hasResult) {
                                palette.onContainer
                            } else {
                                palette.onContainer.copy(alpha = 0.4f)
                            },
                            maxLines = 2,
                            modifier = Modifier
                                .fillMaxWidth()
                                .semantics { contentDescription = "Result value" },
                        )
                    }
                }

                AnimatedVisibility(visible = hasResult, enter = fadeIn(), exit = fadeOut()) {
                    IconButton(onClick = onCopy) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy result",
                            tint = palette.onContainer,
                        )
                    }
                }
            }

            if (!unit?.symbol.isNullOrEmpty() && hasResult) {
                Text(
                    text = unit!!.symbol,
                    style = MaterialTheme.typography.titleMedium,
                    color = palette.onContainer.copy(alpha = 0.8f),
                )
            }

            Spacer(Modifier.height(4.dp))
        }
        }
    }
}
