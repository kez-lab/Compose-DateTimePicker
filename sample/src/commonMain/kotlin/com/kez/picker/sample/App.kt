package com.kez.picker.sample

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kez.picker.sample.ui.navigation.Screen
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
            startDestination = Screen.Home.route
        ) {
            composable(Screen.Home.route) { HomeScreen(navController) }
            composable(Screen.Integrated.route) { IntegratedPickerScreen(navController) }
            composable(Screen.TimePicker.route) { TimePickerSampleScreen(navController) }
            composable(Screen.DatePicker.route) { DatePickerSampleScreen(navController) }
        }
    }
}