package com.rpecalc.ui.theme

import androidx.compose.ui.graphics.Color

data class AppPalette(
    val gradientStart: Color,
    val gradientEnd: Color,
    val accent: Color,
    val name: String
)

val CardBackground = Color.White
val BackgroundColor = Color(0xFFF6F6F6)
val TextPrimary = Color(0xFF313131)
val TextSecondary = Color(0xFF757575)

val CardShadow = Color(0x73A3A3A3)
val BorderColor = Color(0xFFCDCDCD)

val Palettes = listOf(
    // 1. Original Purple
    AppPalette(
        gradientStart = Color(0xFF794A8F),
        gradientEnd = Color(0xFF9A74AC),
        accent = Color(0xFF5C2A73),
        name = "Original Purple"
    ),
    // 2. Ocean Blue
    AppPalette(
        gradientStart = Color(0xFF1E88E5),
        gradientEnd = Color(0xFF64B5F6),
        accent = Color(0xFF1565C0),
        name = "Ocean Blue"
    ),
    // 3. Forest Green
    AppPalette(
        gradientStart = Color(0xFF2E7D32),
        gradientEnd = Color(0xFF81C784),
        accent = Color(0xFF1B5E20),
        name = "Forest Green"
    ),
    // 4. Sunset Orange
    AppPalette(
        gradientStart = Color(0xFFF4511E),
        gradientEnd = Color(0xFFFF8A65),
        accent = Color(0xFFD84315),
        name = "Sunset Orange"
    ),
    // 5. Midnight Dark (Slate)
    AppPalette(
        gradientStart = Color(0xFF37474F),
        gradientEnd = Color(0xFF78909C),
        accent = Color(0xFF263238),
        name = "Midnight Slate"
    )
)

