package com.kez.picker.sample.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    var isDarkTheme by remember { mutableStateOf(false) }
    MaterialTheme(
        colorScheme = if (isDarkTheme) DarkThemeColors else LightThemeColors,
        content = content
    )
}