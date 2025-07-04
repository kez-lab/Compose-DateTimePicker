package com.kez.picker.sample.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// 부드러운 테마 색상 정의
internal val LightThemeColors = lightColorScheme(
    primary = Color(0xFF4DB6AC),
    onPrimary = Color.White,
    secondary = Color(0xFFF06292),
    background = Color(0xFFF7F9F9),
    surface = Color.White,
    onSurface = Color(0xFF1A1C1E)
)

internal val DarkThemeColors = darkColorScheme(
    primary = Color(0xFF80CBC4),
    onPrimary = Color.Black,
    secondary = Color(0xFFF48FB1),
    background = Color(0xFF1A1C1E),
    surface = Color(0xFF242829),
    onSurface = Color(0xFFE2E2E2)
)