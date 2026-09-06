package com.example.unit_coverter.feature.currency

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.unit_coverter.data.currency.CurrencyInfo
import com.example.unit_coverter.data.currency.currencyInfo
import com.example.unit_coverter.ui.theme.categoryPalette
import com.example.unit_coverter.ui.theme.fixedSp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyScreen(
    modifier: Modifier = Modifier,
    viewModel: CurrencyViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val palette = categoryPalette("currency")

    var showFromPicker by rememberSaveable { mutableStateOf(false) }
    var showToPicker by rememberSaveable { mutableStateOf(false) }
    val fromSheet = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val toSheet = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val clipboard = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Spin the refresh icon continuously while a fetch is in flight.
    val spin = rememberInfiniteTransition(label = "refreshSpin")
    val spinAngle by spin.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(900)),
        label = "refreshAngle",
    )

    var swapCount by rememberSaveable { mutableStateOf(0) }
    val swapRotation by animateFloatAsState(swapCount * 180f, label = "swapRotation")

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Currency") },
                actions = {
                    IconButton(onClick = viewModel::refresh, enabled = !state.isRefreshing) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh rates",
                            modifier = if (state.isRefreshing) {
                                Modifier.rotate(spinAngle)
                            } else {
                                Modifier
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
        ) {
            Spacer(Modifier.height(8.dp))

            // ── Amount ───────────────────────────────────────────────────────
            Card(
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            "AMOUNT",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        CurrencyChip(
                            info = currencyInfo(state.fromCode),
                            onClick = { showFromPicker = true },
                        )
                    }
                    OutlinedTextField(
                        value = state.amountText,
                        onValueChange = viewModel::onAmountChanged,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MaterialTheme.typography.headlineSmall.copy(
                            fontSize = fixedSp(20f),
                        ),
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Done,
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = palette.accent,
                            cursorColor = palette.accent,
                        ),
                    )
                }
            }

            // ── Swap ─────────────────────────────────────────────────────────
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                FilledIconButton(
                    onClick = { swapCount++; viewModel.swap() },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = palette.container,
                        contentColor = palette.onContainer,
                    ),
                    modifier = Modifier.size(48.dp),
                ) {
                    Icon(
                        Icons.Default.SwapHoriz,
                        contentDescription = "Swap currencies",
                        modifier = Modifier.rotate(swapRotation),
                    )
                }
            }

            // ── Result ───────────────────────────────────────────────────────
            Card(
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.Transparent),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(
                    Modifier
                        .background(palette.softGradient, MaterialTheme.shapes.large)
                        .fillMaxWidth(),
                ) {
                    Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                "CONVERTED TO",
                                style = MaterialTheme.typography.labelSmall,
                                color = palette.onContainer.copy(alpha = 0.75f),
                            )
                            CurrencyChip(
                                info = currencyInfo(state.toCode),
                                onClick = { showToPicker = true },
                                contentColor = palette.onContainer,
                            )
                        }

                        Spacer(Modifier.height(4.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = state.resultText.ifEmpty { "—" },
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontSize = fixedSp(26f),
                                ),
                                fontWeight = FontWeight.SemiBold,
                                color = palette.onContainer,
                                maxLines = 2,
                                modifier = Modifier.weight(1f),
                            )
                            AnimatedVisibility(
                                visible = state.resultText.isNotEmpty(),
                                enter = fadeIn(), exit = fadeOut(),
                            ) {
                                IconButton(onClick = {
                                    clipboard.setText(AnnotatedString(state.resultText))
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Copied ${state.resultText}")
                                    }
                                }) {
                                    Icon(
                                        Icons.Default.ContentCopy,
                                        contentDescription = "Copy amount",
                                        tint = palette.onContainer,
                                    )
                                }
                            }
                        }

                        if (state.unitRateText.isNotEmpty()) {
                            Text(
                                text = state.unitRateText,
                                style = MaterialTheme.typography.titleSmall,
                                color = palette.onContainer.copy(alpha = 0.85f),
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // ── Status line ──────────────────────────────────────────────────
            when {
                state.isRefreshing && state.rates == null -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Fetching latest rates…",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                state.errorMessage != null && state.rates == null -> {
                    OfflineNotice(state.errorMessage!!, onRetry = viewModel::refresh)
                }

                else -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (state.errorMessage != null || state.isStale) {
                            Icon(
                                Icons.Default.CloudOff,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp),
                            )
                            Spacer(Modifier.width(6.dp))
                        }
                        Text(
                            text = if (state.errorMessage != null) {
                                "Offline · showing saved rates"
                            } else {
                                state.lastUpdatedLabel
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Text(
                        text = "Rates are indicative and exclude bank fees.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }

    if (showFromPicker) {
        CurrencyPickerSheet(
            currencies = state.availableCurrencies,
            selectedCode = state.fromCode,
            sheetState = fromSheet,
            onSelected = { viewModel.onFromSelected(it); showFromPicker = false },
            onDismiss = { showFromPicker = false },
        )
    }
    if (showToPicker) {
        CurrencyPickerSheet(
            currencies = state.availableCurrencies,
            selectedCode = state.toCode,
            sheetState = toSheet,
            onSelected = { viewModel.onToSelected(it); showToPicker = false },
            onDismiss = { showToPicker = false },
        )
    }
}

@Composable
private fun CurrencyChip(
    info: CurrencyInfo,
    onClick: () -> Unit,
    contentColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
) {
    TextButton(onClick = onClick) {
        Text(info.code, style = MaterialTheme.typography.titleMedium, color = contentColor)
        Spacer(Modifier.width(4.dp))
        Icon(
            Icons.Default.UnfoldMore,
            contentDescription = null,
            tint = contentColor.copy(alpha = 0.7f),
            modifier = Modifier.size(18.dp),
        )
    }
}

@Composable
private fun OfflineNotice(message: String, onRetry: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
        ),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Default.CloudOff,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer,
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    "No rates yet",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                )
                Text(
                    message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                )
            }
            TextButton(onClick = onRetry) { Text("Retry") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CurrencyPickerSheet(
    currencies: List<CurrencyInfo>,
    selectedCode: String,
    sheetState: androidx.compose.material3.SheetState,
    onSelected: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Text(
            "Select currency",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
        )
        LazyColumn(Modifier.fillMaxWidth()) {
            items(currencies, key = { it.code }) { c ->
                val selected = c.code == selectedCode
                ListItem(
                    headlineContent = {
                        Text(
                            c.code,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        )
                    },
                    supportingContent = { Text(c.displayName) },
                    leadingContent = {
                        Box(
                            Modifier
                                .size(40.dp)
                                .background(
                                    if (selected) {
                                        MaterialTheme.colorScheme.primaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.surfaceContainerHigh
                                    },
                                    RoundedCornerShape(12.dp),
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(c.symbol, style = MaterialTheme.typography.titleMedium)
                        }
                    },
                    modifier = Modifier.clickable { onSelected(c.code) },
                )
            }
        }
    }
}
