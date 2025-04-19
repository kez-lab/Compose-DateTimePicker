package com.kez.picker.sample

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Picker Demo",
        state = rememberWindowState(size = DpSize(400.dp, 800.dp))
    ) {
        App()
    }
} 