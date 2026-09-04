package com.example.unit_coverter.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

/**
 * Per-category color identity.
 *
 * Every unit category owns a hue, so the whole app re-tints when the user
 * switches category: the result card, the active chip, the swap control and the
 * list accents all move together. This is orientation, not decoration — a glance
 * at the color tells you whether you're in Temperature or Data Storage.
 *
 * Each entry carries an explicit light and dark variant. Picking one saturated
 * color and darkening it programmatically produces muddy, low-contrast results
 * on dark backgrounds, so the dark tones are hand-set to stay vivid while
 * keeping text legible.
 */
@Immutable
data class CategoryPalette(
    /** Main brand hue for the category. */
    val accent: Color,
    /** Secondary hue used as the far end of gradients. */
    val accentSecondary: Color,
    /** Fill behind large surfaces (the result card). */
    val container: Color,
    /** Foreground guaranteed to read against [container]. */
    val onContainer: Color,
) {
    /** Diagonal gradient used for hero surfaces. */
    val gradient: Brush
        get() = Brush.linearGradient(listOf(accent, accentSecondary))

    /** Softer gradient for large fills that sit behind body text. */
    val softGradient: Brush
        get() = Brush.linearGradient(
            listOf(
                lerp(container, accent, 0.12f),
                lerp(container, accentSecondary, 0.28f),
            ),
        )
}

// ─────────────────────────────────────────────────────────────────────────────
// Light variants — saturated but not neon; these sit under dark text.
// ─────────────────────────────────────────────────────────────────────────────

private val LightPalettes: Map<String, CategoryPalette> = mapOf(
    "length" to CategoryPalette(
        accent = Color(0xFF00A6A0), accentSecondary = Color(0xFF00C2B2),
        container = Color(0xFFB8F5EF), onContainer = Color(0xFF00201E),
    ),
    "mass" to CategoryPalette(
        accent = Color(0xFF7A4FE0), accentSecondary = Color(0xFF9B6DFF),
        container = Color(0xFFE7DBFF), onContainer = Color(0xFF21005D),
    ),
    "volume" to CategoryPalette(
        accent = Color(0xFF0F7BE0), accentSecondary = Color(0xFF39A0FF),
        container = Color(0xFFD1E5FF), onContainer = Color(0xFF001C39),
    ),
    "temperature" to CategoryPalette(
        accent = Color(0xFFE0562B), accentSecondary = Color(0xFFFF8A3D),
        container = Color(0xFFFFDCC9), onContainer = Color(0xFF3A0B00),
    ),
    "area" to CategoryPalette(
        accent = Color(0xFF3D8B2F), accentSecondary = Color(0xFF6FBF4E),
        container = Color(0xFFD3F2C4), onContainer = Color(0xFF0B2000),
    ),
    "pressure" to CategoryPalette(
        accent = Color(0xFF0E7C86), accentSecondary = Color(0xFF35AEB4),
        container = Color(0xFFC4EEF1), onContainer = Color(0xFF00201F),
    ),
    "energy" to CategoryPalette(
        accent = Color(0xFFD79A00), accentSecondary = Color(0xFFFFC43D),
        container = Color(0xFFFFE9B0), onContainer = Color(0xFF2A1B00),
    ),
    "power" to CategoryPalette(
        accent = Color(0xFFC2185B), accentSecondary = Color(0xFFF0538A),
        container = Color(0xFFFFD9E3), onContainer = Color(0xFF3E001C),
    ),
    "force" to CategoryPalette(
        accent = Color(0xFF4A5AB9), accentSecondary = Color(0xFF7B87E8),
        container = Color(0xFFDEE0FF), onContainer = Color(0xFF00105C),
    ),
    "time" to CategoryPalette(
        accent = Color(0xFF5B6BC0), accentSecondary = Color(0xFF8B9AF0),
        container = Color(0xFFDDE1FF), onContainer = Color(0xFF001257),
    ),
    "speed" to CategoryPalette(
        accent = Color(0xFFE04E1B), accentSecondary = Color(0xFFFF8340),
        container = Color(0xFFFFDBCC), onContainer = Color(0xFF3B0A00),
    ),
    "angle" to CategoryPalette(
        accent = Color(0xFF8E4EC6), accentSecondary = Color(0xFFB57BEC),
        container = Color(0xFFF0DBFF), onContainer = Color(0xFF2C0051),
    ),
    "fuel_consumption" to CategoryPalette(
        accent = Color(0xFF107A5A), accentSecondary = Color(0xFF3FB98A),
        container = Color(0xFFBFF0DC), onContainer = Color(0xFF002018),
    ),
    "data_storage" to CategoryPalette(
        accent = Color(0xFF2E5CE6), accentSecondary = Color(0xFF5C8BFF),
        container = Color(0xFFDBE2FF), onContainer = Color(0xFF00164F),
    ),
    "currency" to CategoryPalette(
        accent = Color(0xFF00875A), accentSecondary = Color(0xFF37C48B),
        container = Color(0xFFC5F2DE), onContainer = Color(0xFF002014),
    ),
)

// ─────────────────────────────────────────────────────────────────────────────
// Dark variants — lifted and slightly desaturated so they glow on near-black
// without vibrating, with deep containers that keep light text readable.
// ─────────────────────────────────────────────────────────────────────────────

private val DarkPalettes: Map<String, CategoryPalette> = mapOf(
    "length" to CategoryPalette(
        accent = Color(0xFF4FE0D5), accentSecondary = Color(0xFF2BB5AC),
        container = Color(0xFF00504B), onContainer = Color(0xFFB8F5EF),
    ),
    "mass" to CategoryPalette(
        accent = Color(0xFFC0A6FF), accentSecondary = Color(0xFF9B6DFF),
        container = Color(0xFF3F2A80), onContainer = Color(0xFFE7DBFF),
    ),
    "volume" to CategoryPalette(
        accent = Color(0xFF7FC0FF), accentSecondary = Color(0xFF3F97F0),
        container = Color(0xFF0A3D6B), onContainer = Color(0xFFD1E5FF),
    ),
    "temperature" to CategoryPalette(
        accent = Color(0xFFFF9A6B), accentSecondary = Color(0xFFE0562B),
        container = Color(0xFF6B2410), onContainer = Color(0xFFFFDCC9),
    ),
    "area" to CategoryPalette(
        accent = Color(0xFF9EDF84), accentSecondary = Color(0xFF6FBF4E),
        container = Color(0xFF1F4A14), onContainer = Color(0xFFD3F2C4),
    ),
    "pressure" to CategoryPalette(
        accent = Color(0xFF5FD3DA), accentSecondary = Color(0xFF2AA3AA),
        container = Color(0xFF00464B), onContainer = Color(0xFFC4EEF1),
    ),
    "energy" to CategoryPalette(
        accent = Color(0xFFFFD166), accentSecondary = Color(0xFFE0A800),
        container = Color(0xFF5C4200), onContainer = Color(0xFFFFE9B0),
    ),
    "power" to CategoryPalette(
        accent = Color(0xFFFF8FB4), accentSecondary = Color(0xFFE0457E),
        container = Color(0xFF7A0035), onContainer = Color(0xFFFFD9E3),
    ),
    "force" to CategoryPalette(
        accent = Color(0xFFBAC3FF), accentSecondary = Color(0xFF8B97F5),
        container = Color(0xFF2B3A96), onContainer = Color(0xFFDEE0FF),
    ),
    "time" to CategoryPalette(
        accent = Color(0xFFBAC4FF), accentSecondary = Color(0xFF8B9AF0),
        container = Color(0xFF2A3B94), onContainer = Color(0xFFDDE1FF),
    ),
    "speed" to CategoryPalette(
        accent = Color(0xFFFF9670), accentSecondary = Color(0xFFE04E1B),
        container = Color(0xFF6B2200), onContainer = Color(0xFFFFDBCC),
    ),
    "angle" to CategoryPalette(
        accent = Color(0xFFD9A8FF), accentSecondary = Color(0xFFB57BEC),
        container = Color(0xFF52217A), onContainer = Color(0xFFF0DBFF),
    ),
    "fuel_consumption" to CategoryPalette(
        accent = Color(0xFF5FD9AB), accentSecondary = Color(0xFF29A87B),
        container = Color(0xFF00513A), onContainer = Color(0xFFBFF0DC),
    ),
    "data_storage" to CategoryPalette(
        accent = Color(0xFF9DB6FF), accentSecondary = Color(0xFF5C8BFF),
        container = Color(0xFF1C3C9E), onContainer = Color(0xFFDBE2FF),
    ),
    "currency" to CategoryPalette(
        accent = Color(0xFF57DBA4), accentSecondary = Color(0xFF20A874),
        container = Color(0xFF00513A), onContainer = Color(0xFFC5F2DE),
    ),
)

/** Neutral fallback for user-defined categories with no assigned hue. */
private val LightFallback = CategoryPalette(
    accent = Color(0xFF5A5D72), accentSecondary = Color(0xFF8288A8),
    container = Color(0xFFDFE1F9), onContainer = Color(0xFF171B2C),
)

private val DarkFallback = CategoryPalette(
    accent = Color(0xFFC3C6DD), accentSecondary = Color(0xFF9297B5),
    container = Color(0xFF424659), onContainer = Color(0xFFDFE1F9),
)

/**
 * Palette for [categoryId], resolved against the current light/dark theme.
 * Unknown ids (custom categories) get a neutral palette rather than crashing.
 */
@Composable
@ReadOnlyComposable
fun categoryPalette(categoryId: String?): CategoryPalette {
    val dark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val table = if (dark) DarkPalettes else LightPalettes
    val fallback = if (dark) DarkFallback else LightFallback
    return table[categoryId] ?: fallback
}

/** Perceptual luminance; avoids pulling in a graphics dependency just for this. */
private fun Color.luminance(): Float = (0.299f * red) + (0.587f * green) + (0.114f * blue)
