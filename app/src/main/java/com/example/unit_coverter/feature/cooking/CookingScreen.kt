package com.example.unit_coverter.feature.cooking

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import com.example.unit_coverter.core.cooking.Ingredient
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.ui.semantics.Role

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CookingScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CookingViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    var showFromPicker by rememberSaveable { mutableStateOf(false) }
    var showToPicker by rememberSaveable { mutableStateOf(false) }
    val fromSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val toSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Cooking Converter") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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

            // ── Ingredient selector ───────────────────────────────────────────
            IngredientSelector(
                ingredients = state.ingredients,
                selected = state.selectedIngredient,
                onSelect = { viewModel.onEvent(CookingEvent.SelectIngredient(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            )

            state.selectedIngredient?.let { ing ->
                Text(
                    text = "Density: ${ing.densityGPerMl.stripTrailingZeros().toPlainString()} g/mL",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 2.dp),
                )
            }

            Spacer(Modifier.height(16.dp))

            // ── From panel ────────────────────────────────────────────────────
            CookingPanel(
                label = "From",
                unit = state.fromUnit,
                inputText = state.inputText,
                onInputChanged = { viewModel.onEvent(CookingEvent.InputChanged(it)) },
                onUnitPickerClicked = { showFromPicker = true },
                onClearClicked = { viewModel.onEvent(CookingEvent.ClearInput) },
                errorMessage = state.errorMessage,
                isInput = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            )

            Spacer(Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                FilledTonalIconButton(
                    onClick = { viewModel.onEvent(CookingEvent.SwapUnits) },
                    modifier = Modifier.semantics { contentDescription = "Swap units" },
                ) {
                    Icon(Icons.Default.SwapVert, contentDescription = null)
                }
            }

            Spacer(Modifier.height(8.dp))

            // ── To panel ──────────────────────────────────────────────────────
            CookingPanel(
                label = "To",
                unit = state.toUnit,
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

    // ── Unit pickers ─────────────────────────────────────────────────────────
    if (showFromPicker) {
        CookingUnitPickerSheet(
            massUnits = state.massUnits,
            volumeUnits = state.volumeUnits,
            selectedUnit = state.fromUnit,
            sheetState = fromSheetState,
            onUnitSelected = { viewModel.onEvent(CookingEvent.SelectFromUnit(it)) },
            onDismiss = { showFromPicker = false },
        )
    }
    if (showToPicker) {
        CookingUnitPickerSheet(
            massUnits = state.massUnits,
            volumeUnits = state.volumeUnits,
            selectedUnit = state.toUnit,
            sheetState = toSheetState,
            onUnitSelected = { viewModel.onEvent(CookingEvent.SelectToUnit(it)) },
            onDismiss = { showToPicker = false },
        )
    }
}

// ── Ingredient selector (ExposedDropdown) ─────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IngredientSelector(
    ingredients: List<Ingredient>,
    selected: Ingredient?,
    onSelect: (Ingredient) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = selected?.displayName ?: "Select ingredient",
            onValueChange = {},
            readOnly = true,
            label = { Text("Ingredient") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            ingredients.forEach { ingredient ->
                DropdownMenuItem(
                    text = { Text(ingredient.displayName) },
                    onClick = {
                        onSelect(ingredient)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}

// ── Conversion panel (shared between From and To) ─────────────────────────────

@Composable
private fun CookingPanel(
    label: String,
    unit: CookingUnitItem?,
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
                TextButton(onClick = onUnitPickerClicked) {
                    Text(
                        text = unit?.unit?.displayName ?: "Select unit",
                        style = MaterialTheme.typography.titleSmall,
                    )
                    if (unit?.unit?.symbol?.isNotEmpty() == true) {
                        Text(
                            text = " (${unit.unit.symbol})",
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
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Amount") },
                    isError = errorMessage != null,
                    supportingText = errorMessage?.let { { Text(it) } },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done,
                    ),
                    trailingIcon = if (inputText.isNotEmpty()) {
                        { TextButton(onClick = onClearClicked) { Text("Clear") } }
                    } else null,
                    singleLine = true,
                )
            } else {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Result") },
                    readOnly = true,
                    singleLine = true,
                )
            }
        }
    }
}

// ── Unit picker bottom sheet ──────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CookingUnitPickerSheet(
    massUnits: List<CookingUnitItem>,
    volumeUnits: List<CookingUnitItem>,
    selectedUnit: CookingUnitItem?,
    sheetState: androidx.compose.material3.SheetState,
    onUnitSelected: (CookingUnitItem) -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            item {
                Text(
                    text = "Mass",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
            items(massUnits, key = { it.unit.id }) { item ->
                CookingUnitRow(
                    item = item,
                    isSelected = item.unit.id == selectedUnit?.unit?.id,
                    onClick = { onUnitSelected(item); onDismiss() },
                )
            }
            item { HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp)) }
            item {
                Text(
                    text = "Volume",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
            items(volumeUnits, key = { it.unit.id }) { item ->
                CookingUnitRow(
                    item = item,
                    isSelected = item.unit.id == selectedUnit?.unit?.id,
                    onClick = { onUnitSelected(item); onDismiss() },
                )
            }
            item { Spacer(Modifier.height(32.dp)) }
        }
    }
}

@Composable
private fun CookingUnitRow(
    item: CookingUnitItem,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    ListItem(
        headlineContent = { Text(item.unit.displayName) },
        supportingContent = if (item.unit.symbol.isNotEmpty()) {
            { Text(item.unit.symbol) }
        } else null,
        leadingContent = { RadioButton(selected = isSelected, onClick = null) },
        modifier = Modifier.clickable(role = Role.RadioButton, onClick = onClick),
    )
}
