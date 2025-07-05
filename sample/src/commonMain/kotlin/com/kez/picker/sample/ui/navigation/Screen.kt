package com.kez.picker.sample.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Integrated : Screen("integrated")
    object TimePicker : Screen("time_picker")
    object DatePicker : Screen("date_picker")
}
