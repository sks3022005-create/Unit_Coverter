package com.example.unit_coverter.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unit_coverter.ui.theme.CategoryPalette

/**
 * In-app numeric keypad for the converter.
 *
 * The converter only ever needs digits, a decimal point and a sign, so the
 * system IME is the wrong tool: it covers half the screen, hides the result the
 * user is trying to read, and offers a full QWERTY for a four-character number.
 * This keypad is always visible, so a conversion is one tap away and the result
 * stays on screen while typing.
 *
 * Keys report through [onKey]; the caller decides how to mutate the input, so
 * this component holds no state.
 */
@Composable
fun NumericKeypad(
    onDigit: (Char) -> Unit,
    onDecimal: () -> Unit,
    onBackspace: () -> Unit,
    onClear: () -> Unit,
    onToggleSign: () -> Unit,
    palette: CategoryPalette,
    modifier: Modifier = Modifier,
    decimalSeparator: Char = '.',
) {
    val haptics = LocalHapticFeedback.current

    // Row-major layout. Digits sit under the thumb; destructive keys (clear,
    // backspace) live on the right edge, away from the digits.
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        KeypadRow {
            DigitKey("7") { haptics.tap(); onDigit('7') }
            DigitKey("8") { haptics.tap(); onDigit('8') }
            DigitKey("9") { haptics.tap(); onDigit('9') }
            ActionKey(
                label = "C",
                contentDescription = "Clear",
                container = MaterialTheme.colorScheme.errorContainer,
                content = MaterialTheme.colorScheme.onErrorContainer,
            ) { haptics.tap(); onClear() }
        }
        KeypadRow {
            DigitKey("4") { haptics.tap(); onDigit('4') }
            DigitKey("5") { haptics.tap(); onDigit('5') }
            DigitKey("6") { haptics.tap(); onDigit('6') }
            ActionKey(
                icon = true,
                contentDescription = "Backspace",
                container = palette.container,
                content = palette.onContainer,
            ) { haptics.tap(); onBackspace() }
        }
        KeypadRow {
            DigitKey("1") { haptics.tap(); onDigit('1') }
            DigitKey("2") { haptics.tap(); onDigit('2') }
            DigitKey("3") { haptics.tap(); onDigit('3') }
            ActionKey(
                label = "+/−",
                contentDescription = "Toggle sign",
                container = palette.container,
                content = palette.onContainer,
            ) { haptics.tap(); onToggleSign() }
        }
        KeypadRow {
            DigitKey("0") { haptics.tap(); onDigit('0') }
            DigitKey("00") { haptics.tap(); onDigit('0'); onDigit('0') }
            DigitKey(decimalSeparator.toString()) { haptics.tap(); onDecimal() }
            // Balances the grid; the result updates live so there is no "equals".
            Box(Modifier.weight(1f))
        }
    }
}

private fun androidx.compose.ui.hapticfeedback.HapticFeedback.tap() =
    performHapticFeedback(HapticFeedbackType.TextHandleMove)

@Composable
private fun KeypadRow(content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        content = content,
    )
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.DigitKey(
    label: String,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .weight(1f)
            .heightIn(min = 46.dp)
            .semantics { contentDescription = label },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
        tonalElevation = 1.dp,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Medium,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.ActionKey(
    contentDescription: String,
    container: Color,
    content: Color,
    label: String? = null,
    icon: Boolean = false,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .weight(1f)
            .heightIn(min = 46.dp)
            .semantics { this.contentDescription = contentDescription },
        shape = RoundedCornerShape(16.dp),
        color = container,
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (icon) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                    contentDescription = null,
                    tint = content,
                    modifier = Modifier.size(24.dp),
                )
            } else {
                Text(
                    text = label.orEmpty(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = content,
                )
            }
        }
    }
}
