package com.kez.picker.sample

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "DateTimePicker Demo",
        state = rememberWindowState()
    ) {
        Surface(modifier = Modifier.fillMaxSize()) {
            App()
        }
    }
} 