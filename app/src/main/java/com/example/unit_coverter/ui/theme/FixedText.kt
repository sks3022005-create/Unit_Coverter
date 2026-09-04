package com.example.unit_coverter.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

/**
 * Text size that ignores the user's system font scale.
 *
 * The converter's big display values (input number, result number, keypad
 * digits) are numeric, not prose — enlarging them with fontScale only makes the
 * layout balloon until the result card no longer fits next to the keypad, which
 * is exactly the bug seen on devices with large font settings: the keypad was
 * cut in half and the result needed scrolling.
 *
 * Prose (labels, hints, unit names) keeps scaling normally; only the numeric
 * displays opt out. This keeps the converter's vertical layout deterministic
 * on every device at every font scale.
 */
@Composable
fun fixedSp(dpEquivalent: Float): TextUnit {
    val fontScale = LocalDensity.current.fontScale
    return (dpEquivalent / fontScale.coerceAtLeast(0.1f)).sp
}
