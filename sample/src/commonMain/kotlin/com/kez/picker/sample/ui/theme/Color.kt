package com.kez.picker.sample.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// 심플한 단색 테마 (라이브러리 샘플 앱)
internal val LightThemeColors = lightColorScheme(
    primary = Color(0xFF2196F3),        // 표준 블루
    onPrimary = Color.White,
    secondary = Color(0xFF64B5F6),      // 라이트 블루 (톤온톤)
    background = Color(0xFFFAFAFA),     // 거의 흰색
    surface = Color.White,
    onSurface = Color(0xFF212121),      // 진한 회색 (대비 향상)
    onSurfaceVariant = Color(0xFF757575) // 보조 텍스트
)

internal val DarkThemeColors = darkColorScheme(
    primary = Color(0xFF42A5F5),        // 밝은 블루
    onPrimary = Color(0xFF001D35),
    secondary = Color(0xFF90CAF9),      // 라이트 블루
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE0E0E0),
    onSurfaceVariant = Color(0xFFBDBDBD)
)