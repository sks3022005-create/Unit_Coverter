package com.example.unit_coverter.feature.calculator

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.unit_coverter.core.math.ScientificEvaluator.AngleMode

/** Visual/behavioral category of a keypad key, driving its colors. */
private enum class KeyKind { NUMBER, OPERATOR, FUNCTION, ACCENT, TOGGLE }

private class CalcKey(
    val label: String,
    val kind: KeyKind,
    val active: Boolean = false,
    val description: String? = null,
    val onClick: () -> Unit,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    modifier: Modifier = Modifier,
    viewModel: CalculatorViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    fun on(event: CalculatorEvent) = viewModel.onEvent(event)
    fun input(token: String) = viewModel.onEvent(CalculatorEvent.Input(token))

    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text("Calculator") }) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 12.dp),
        ) {
            Display(state)

            Spacer(Modifier.height(8.dp))

            // ── Scientific keys ──────────────────────────────────────────────────
            val sec = state.isSecondFunction
            val scientificRows: List<List<CalcKey>> = listOf(
                listOf(
                    CalcKey("2nd", KeyKind.TOGGLE, active = sec) { on(CalculatorEvent.ToggleSecondFunction) },
                    CalcKey(
                        label = if (state.angleMode == AngleMode.RADIAN) "RAD" else "DEG",
                        kind = KeyKind.TOGGLE,
                        description = "Toggle angle mode",
                    ) { on(CalculatorEvent.ToggleAngleMode) },
                    if (sec) CalcKey("∛", KeyKind.FUNCTION) { input("cbrt(") }
                    else CalcKey("√", KeyKind.FUNCTION) { input("√(") },
                    CalcKey("^", KeyKind.FUNCTION, description = "power") { input("^") },
                    CalcKey("x²", KeyKind.FUNCTION, description = "squared") { input("^2") },
                ),
                listOf(
                    if (sec) CalcKey("sin⁻¹", KeyKind.FUNCTION) { input("asin(") }
                    else CalcKey("sin", KeyKind.FUNCTION) { input("sin(") },
                    if (sec) CalcKey("cos⁻¹", KeyKind.FUNCTION) { input("acos(") }
                    else CalcKey("cos", KeyKind.FUNCTION) { input("cos(") },
                    if (sec) CalcKey("tan⁻¹", KeyKind.FUNCTION) { input("atan(") }
                    else CalcKey("tan", KeyKind.FUNCTION) { input("tan(") },
                    if (sec) CalcKey("eˣ", KeyKind.FUNCTION) { input("exp(") }
                    else CalcKey("ln", KeyKind.FUNCTION) { input("ln(") },
                    if (sec) CalcKey("log₂", KeyKind.FUNCTION) { input("log2(") }
                    else CalcKey("log", KeyKind.FUNCTION) { input("log(") },
                ),
                listOf(
                    CalcKey("π", KeyKind.FUNCTION) { input("π") },
                    CalcKey("e", KeyKind.FUNCTION) { input("e") },
                    CalcKey("!", KeyKind.FUNCTION, description = "factorial") { input("!") },
                    CalcKey("%", KeyKind.FUNCTION, description = "percent") { input("%") },
                    CalcKey("|x|", KeyKind.FUNCTION, description = "absolute value") { input("abs(") },
                ),
            )

            // ── Number pad & core operators ──────────────────────────────────────
            val mainRows: List<List<CalcKey>> = listOf(
                listOf(
                    CalcKey("C", KeyKind.OPERATOR, description = "clear") { on(CalculatorEvent.Clear) },
                    CalcKey("(", KeyKind.OPERATOR) { input("(") },
                    CalcKey(")", KeyKind.OPERATOR) { input(")") },
                    CalcKey("÷", KeyKind.OPERATOR, description = "divide") { input("÷") },
                ),
                listOf(
                    CalcKey("7", KeyKind.NUMBER) { input("7") },
                    CalcKey("8", KeyKind.NUMBER) { input("8") },
                    CalcKey("9", KeyKind.NUMBER) { input("9") },
                    CalcKey("×", KeyKind.OPERATOR, description = "multiply") { input("×") },
                ),
                listOf(
                    CalcKey("4", KeyKind.NUMBER) { input("4") },
                    CalcKey("5", KeyKind.NUMBER) { input("5") },
                    CalcKey("6", KeyKind.NUMBER) { input("6") },
                    CalcKey("−", KeyKind.OPERATOR, description = "minus") { input("-") },
                ),
                listOf(
                    CalcKey("1", KeyKind.NUMBER) { input("1") },
                    CalcKey("2", KeyKind.NUMBER) { input("2") },
                    CalcKey("3", KeyKind.NUMBER) { input("3") },
                    CalcKey("+", KeyKind.OPERATOR, description = "plus") { input("+") },
                ),
                listOf(
                    CalcKey("0", KeyKind.NUMBER) { input("0") },
                    CalcKey(".", KeyKind.NUMBER) { input(".") },
                    CalcKey("⌫", KeyKind.OPERATOR, description = "backspace") { on(CalculatorEvent.Backspace) },
                    CalcKey("=", KeyKind.ACCENT, description = "equals") { on(CalculatorEvent.Evaluate) },
                ),
            )

            KeyGrid(scientificRows, keyHeight = 48.dp)
            Spacer(Modifier.height(4.dp))
            KeyGrid(mainRows, keyHeight = 60.dp)
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun Display(state: CalculatorUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalAlignment = Alignment.End,
    ) {
        Text(
            text = state.expression.ifEmpty { "0" },
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.End,
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = state.result,
            fontSize = 40.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
            color = if (state.isError) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.primary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.End,
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
        )
    }
}

@Composable
private fun KeyGrid(rows: List<List<CalcKey>>, keyHeight: androidx.compose.ui.unit.Dp) {
    Column(modifier = Modifier.fillMaxWidth()) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                row.forEach { key ->
                    KeyButton(
                        key = key,
                        modifier = Modifier
                            .weight(1f)
                            .height(keyHeight)
                            .padding(vertical = 3.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun KeyButton(key: CalcKey, modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    val (bg, fg) = when (key.kind) {
        KeyKind.NUMBER -> scheme.surfaceContainerHighest to scheme.onSurface
        KeyKind.OPERATOR -> scheme.secondaryContainer to scheme.onSecondaryContainer
        KeyKind.FUNCTION -> scheme.surfaceContainerHigh to scheme.onSurfaceVariant
        KeyKind.ACCENT -> scheme.primary to scheme.onPrimary
        KeyKind.TOGGLE ->
            if (key.active) scheme.tertiary to scheme.onTertiary
            else scheme.tertiaryContainer to scheme.onTertiaryContainer
    }

    Surface(
        onClick = key.onClick,
        modifier = modifier.semantics {
            key.description?.let { contentDescription = it }
        },
        shape = MaterialTheme.shapes.medium,
        color = bg,
        contentColor = fg,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = key.label,
                style = if (key.kind == KeyKind.NUMBER) MaterialTheme.typography.titleLarge
                else MaterialTheme.typography.titleMedium,
                color = Color.Unspecified,
            )
        }
    }
}
