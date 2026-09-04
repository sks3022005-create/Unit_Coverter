package com.example.unit_coverter.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isFinite
import com.example.unit_coverter.ui.theme.CategoryPalette
import com.example.unit_coverter.ui.theme.fixedSp

/**
 * In-app numeric keypad.
 *
 * Sized from the height the parent actually gives it — never from the full
 * screen height — so on a tall phone (S23 Ultra) leftover space becomes larger
 * keys, and on a compact phone the keys shrink until they fit. The parent must
 * place this in a bounded slot (typically `Modifier.weight(1f)`); wrap-content
 * falls back to 48 dp keys.
 *
 * Keys report through callbacks; this component holds no state.
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

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val rows = 4
        val rowGap = 6.dp
        val verticalPad = 6.dp
        val keyHeight: Dp = if (maxHeight.isFinite) {
            val usable = maxHeight - verticalPad * 2 - rowGap * (rows - 1)
            (usable / rows).coerceAtMost(64.dp).coerceAtLeast(1.dp)
        } else {
            48.dp
        }
        val digitFontSize = fixedSp((keyHeight.value * 0.48f).coerceIn(14f, 28f))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = verticalPad),
            verticalArrangement = Arrangement.spacedBy(rowGap),
        ) {
            KeypadRow {
                DigitKey("7", keyHeight, digitFontSize) { haptics.tap(); onDigit('7') }
                DigitKey("8", keyHeight, digitFontSize) { haptics.tap(); onDigit('8') }
                DigitKey("9", keyHeight, digitFontSize) { haptics.tap(); onDigit('9') }
                ActionKey(
                    label = "C",
                    contentDescription = "Clear",
                    keyHeight = keyHeight,
                    container = MaterialTheme.colorScheme.errorContainer,
                    content = MaterialTheme.colorScheme.onErrorContainer,
                ) { haptics.tap(); onClear() }
            }
            KeypadRow {
                DigitKey("4", keyHeight, digitFontSize) { haptics.tap(); onDigit('4') }
                DigitKey("5", keyHeight, digitFontSize) { haptics.tap(); onDigit('5') }
                DigitKey("6", keyHeight, digitFontSize) { haptics.tap(); onDigit('6') }
                ActionKey(
                    icon = true,
                    contentDescription = "Backspace",
                    keyHeight = keyHeight,
                    container = palette.container,
                    content = palette.onContainer,
                ) { haptics.tap(); onBackspace() }
            }
            KeypadRow {
                DigitKey("1", keyHeight, digitFontSize) { haptics.tap(); onDigit('1') }
                DigitKey("2", keyHeight, digitFontSize) { haptics.tap(); onDigit('2') }
                DigitKey("3", keyHeight, digitFontSize) { haptics.tap(); onDigit('3') }
                ActionKey(
                    label = "+/−",
                    contentDescription = "Toggle sign",
                    keyHeight = keyHeight,
                    container = palette.container,
                    content = palette.onContainer,
                ) { haptics.tap(); onToggleSign() }
            }
            KeypadRow {
                DigitKey("0", keyHeight, digitFontSize) { haptics.tap(); onDigit('0') }
                DigitKey("00", keyHeight, digitFontSize) { haptics.tap(); onDigit('0'); onDigit('0') }
                DigitKey(decimalSeparator.toString(), keyHeight, digitFontSize) { haptics.tap(); onDecimal() }
                Box(Modifier.weight(1f))
            }
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
    keyHeight: Dp,
    digitFontSize: TextUnit,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .weight(1f)
            .height(keyHeight)
            .semantics { contentDescription = label },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
        tonalElevation = 1.dp,
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(
                text = label,
                fontWeight = FontWeight.Medium,
                fontSize = digitFontSize,
                maxLines = 1,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.ActionKey(
    contentDescription: String,
    keyHeight: Dp,
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
            .height(keyHeight)
            .semantics { this.contentDescription = contentDescription },
        shape = RoundedCornerShape(16.dp),
        color = container,
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            if (icon) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                    contentDescription = null,
                    tint = content,
                    modifier = Modifier.size((keyHeight.value * 0.45f).dp),
                )
            } else {
                Text(
                    text = label.orEmpty(),
                    fontSize = fixedSp((keyHeight.value * 0.36f).coerceIn(12f, 22f)),
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    color = content,
                )
            }
        }
    }
}
