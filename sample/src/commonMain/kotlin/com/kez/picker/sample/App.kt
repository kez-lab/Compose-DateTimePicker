package com.kez.picker.sample

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kez.picker.sample.ui.screen.DatePickerSampleScreen
import com.kez.picker.sample.ui.screen.HomeScreen
import com.kez.picker.sample.ui.screen.IntegratedPickerScreen
import com.kez.picker.sample.ui.screen.TimePickerSampleScreen
import com.kez.picker.sample.ui.theme.AppTheme

@Composable
fun App() {
    AppTheme {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = "home"
        ) {
            composable("home") { HomeScreen(navController) }
            composable("integrated") { IntegratedPickerScreen(navController) }
            composable("time_picker") { TimePickerSampleScreen(navController) }
            composable("date_picker") { DatePickerSampleScreen(navController) }
        }
    }
}