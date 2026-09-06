package com.example.unit_coverter.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
 * In-app numeric keypad, styled as a premium bottom tray.
 *
 * Sizing: the parent gives this a bounded slot (weight 1f) and the keys divide
 * ALL of that height — full-bleed down to the bottom bar — so no dead space is
 * left under the last row. Keys shrink on small screens, grow on tall ones.
 *
 * Look: the tray is an elevated rounded-top surface; digits sit on quiet
 * tonal keys, while the action column (clear / backspace / sign) carries the
 * category's accent gradient so the keypad participates in the per-category
 * theming. Every key springs down slightly under the finger.
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
        val rowGap = 8.dp
        val verticalPad = 12.dp
        val keyHeight: Dp = if (maxHeight.isFinite) {
            val usable = maxHeight - verticalPad * 2 - rowGap * (rows - 1)
            (usable / rows).coerceAtLeast(36.dp)
        } else {
            52.dp
        }
        val digitFontSize = fixedSp((keyHeight.value * 0.42f).coerceIn(16f, 34f))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = MaterialTheme.colorScheme.surfaceContainer,
            tonalElevation = 3.dp,
            shadowElevation = 14.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp, vertical = verticalPad),
                verticalArrangement = Arrangement.spacedBy(rowGap),
            ) {
                KeypadRow {
                    DigitKey("7", keyHeight, digitFontSize) { haptics.tap(); onDigit('7') }
                    DigitKey("8", keyHeight, digitFontSize) { haptics.tap(); onDigit('8') }
                    DigitKey("9", keyHeight, digitFontSize) { haptics.tap(); onDigit('9') }
                    GradientActionKey(
                        label = "C",
                        contentDescription = "Clear",
                        keyHeight = keyHeight,
                        brush = Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.errorContainer,
                                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.75f),
                            ),
                        ),
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    ) { haptics.tap(); onClear() }
                }
                KeypadRow {
                    DigitKey("4", keyHeight, digitFontSize) { haptics.tap(); onDigit('4') }
                    DigitKey("5", keyHeight, digitFontSize) { haptics.tap(); onDigit('5') }
                    DigitKey("6", keyHeight, digitFontSize) { haptics.tap(); onDigit('6') }
                    GradientActionKey(
                        icon = true,
                        contentDescription = "Backspace",
                        keyHeight = keyHeight,
                        brush = palette.gradient,
                        contentColor = palette.onContainer,
                    ) { haptics.tap(); onBackspace() }
                }
                KeypadRow {
                    DigitKey("1", keyHeight, digitFontSize) { haptics.tap(); onDigit('1') }
                    DigitKey("2", keyHeight, digitFontSize) { haptics.tap(); onDigit('2') }
                    DigitKey("3", keyHeight, digitFontSize) { haptics.tap(); onDigit('3') }
                    GradientActionKey(
                        label = "+/−",
                        contentDescription = "Toggle sign",
                        keyHeight = keyHeight,
                        brush = palette.softGradient,
                        contentColor = palette.onContainer,
                    ) { haptics.tap(); onToggleSign() }
                }
                KeypadRow {
                    DigitKey("0", keyHeight, digitFontSize) { haptics.tap(); onDigit('0') }
                    DigitKey("00", keyHeight, digitFontSize) { haptics.tap(); onDigit('0'); onDigit('0') }
                    DigitKey(decimalSeparator.toString(), keyHeight, digitFontSize) { haptics.tap(); onDecimal() }
                    // Fourth slot stays empty: backspace already lives in row 2,
                    // and the result updates live so there is no "equals" key.
                    Box(Modifier.weight(1f))
                }
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
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        content = content,
    )
}

/**
 * A digit key: quiet tonal surface, springs down under the finger.
 * [contentDescription] is only set for keys whose glyph is not the value itself.
 */
@Composable
private fun androidx.compose.foundation.layout.RowScope.DigitKey(
    label: String,
    keyHeight: Dp,
    digitFontSize: TextUnit,
    contentDescription: String? = null,
    onClick: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.94f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "keyPress",
    )
    val shape = RoundedCornerShape(18.dp)

    Box(
        modifier = Modifier
            .weight(1f)
            .height(keyHeight)
            .scale(scale)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            .clickable(interactionSource = interaction, indication = androidx.compose.material3.ripple()) { onClick() }
            .semantics {
                contentDescription?.let { this.contentDescription = it }
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Medium,
            fontSize = digitFontSize,
            maxLines = 1,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

/** An action key carrying a gradient — clear (error tones) or category accents. */
@Composable
private fun androidx.compose.foundation.layout.RowScope.GradientActionKey(
    contentDescription: String,
    keyHeight: Dp,
    brush: Brush,
    contentColor: Color,
    label: String? = null,
    icon: Boolean = false,
    onClick: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.94f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "actionPress",
    )
    val shape = RoundedCornerShape(18.dp)

    Box(
        modifier = Modifier
            .weight(1f)
            .height(keyHeight)
            .scale(scale)
            .clip(shape)
            .background(brush)
            .clickable(interactionSource = interaction, indication = androidx.compose.material3.ripple()) { onClick() }
            .semantics { this.contentDescription = contentDescription },
        contentAlignment = Alignment.Center,
    ) {
        if (icon) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Backspace,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size((keyHeight.value * 0.42f).dp),
            )
        } else {
            Text(
                text = label.orEmpty(),
                fontSize = fixedSp((keyHeight.value * 0.34f).coerceIn(13f, 24f)),
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                color = contentColor,
            )
        }
    }
}
