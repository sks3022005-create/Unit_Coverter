package com.example.unit_coverter.feature.converter

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.unit_coverter.core.registry.UnitCategory
import com.example.unit_coverter.core.registry.UnitDef
import com.example.unit_coverter.ui.components.CategorySelector
import com.example.unit_coverter.ui.components.UnitPickerBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConverterScreen(
    modifier: Modifier = Modifier,
    onNavigateToCooking: () -> Unit = {},
    onNavigateToCustomUnits: () -> Unit = {},
    viewModel: ConverterViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.isLoading) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CircularProgressIndicator()
        }
        return
    }

    var showFromPicker by rememberSaveable { mutableStateOf(false) }
    var showToPicker by rememberSaveable { mutableStateOf(false) }
    var menuExpanded by rememberSaveable { mutableStateOf(false) }
    val fromSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val toSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Unit Converter") },
                actions = {
                    IconButton(onClick = onNavigateToCooking) {
                        Icon(Icons.Default.Kitchen, contentDescription = "Cooking converter")
                    }
                    IconButton(
                        onClick = { viewModel.onEvent(ConverterEvent.ToggleFavorite) },
                        enabled = state.fromUnit != null && state.toUnit != null,
                    ) {
                        Icon(
                            imageVector = if (state.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (state.isFavorite) "Remove from favorites" else "Add to favorites",
                            tint = if (state.isFavorite) MaterialTheme.colorScheme.primary
                                   else MaterialTheme.colorScheme.onSurface,
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
            Spacer(Modifier.height(4.dp))

            CategorySelector(
                categories = state.categories,
                selectedCategory = state.selectedCategory,
                onCategorySelected = { viewModel.onEvent(ConverterEvent.SelectCategory(it)) },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(16.dp))

            // From card
            ConversionPanel(
                label = "From",
                unit = state.fromUnit,
                category = state.selectedCategory,
                inputText = state.inputText,
                onInputChanged = { viewModel.onEvent(ConverterEvent.InputChanged(it)) },
                onUnitPickerClicked = { showFromPicker = true },
                onClearClicked = { viewModel.onEvent(ConverterEvent.ClearInput) },
                errorMessage = state.errorMessage,
                isInput = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            )

            Spacer(Modifier.height(8.dp))

            // Swap button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                FilledTonalIconButton(
                    onClick = { viewModel.onEvent(ConverterEvent.SwapUnits) },
                    modifier = Modifier.semantics { contentDescription = "Swap units" },
                ) {
                    Icon(Icons.Default.SwapVert, contentDescription = null)
                }
            }

            Spacer(Modifier.height(8.dp))

            // To card
            ConversionPanel(
                label = "To",
                unit = state.toUnit,
                category = state.selectedCategory,
                inputText = state.resultText,
                onInputChanged = {},
                onUnitPickerClicked = { showToPicker = true },
                onClearClicked = {},
                errorMessage = null,
                isInput = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            )

            Spacer(Modifier.height(24.dp))
        }
    }

    // Bottom sheets
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

@Composable
private fun ConversionPanel(
    label: String,
    unit: UnitDef?,
    category: UnitCategory?,
    inputText: String,
    onInputChanged: (String) -> Unit,
    onUnitPickerClicked: () -> Unit,
    onClearClicked: () -> Unit,
    errorMessage: String?,
    isInput: Boolean,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                TextButton(
                    onClick = onUnitPickerClicked,
                    enabled = category != null,
                ) {
                    Text(
                        text = unit?.displayName ?: "Select unit",
                        style = MaterialTheme.typography.titleSmall,
                    )
                    if (unit?.symbol?.isNotEmpty() == true) {
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "(${unit.symbol})",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            if (isInput) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = onInputChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "Input value" },
                    placeholder = { Text("123.4  •  2^10  •  5 ft 11 in to cm") },
                    isError = errorMessage != null,
                    supportingText = errorMessage?.let { { Text(it) } },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done,
                    ),
                    trailingIcon = if (inputText.isNotEmpty()) {
                        {
                            TextButton(onClick = onClearClicked) {
                                Text("Clear")
                            }
                        }
                    } else null,
                    singleLine = true,
                )
            } else {
                // Read-only result
                OutlinedTextField(
                    value = inputText,
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "Result value" },
                    placeholder = { Text("Result") },
                    readOnly = true,
                    singleLine = true,
                )
            }
        }
    }
}
