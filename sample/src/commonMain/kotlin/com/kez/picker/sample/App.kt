package com.kez.picker.sample

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kez.picker.sample.ui.navigation.Screen
import com.kez.picker.sample.ui.screen.BottomSheetSampleScreen
import com.kez.picker.sample.ui.screen.DatePickerSampleScreen
import com.kez.picker.sample.ui.screen.HomeScreen
import com.kez.picker.sample.ui.screen.IntegratedPickerScreen
import com.kez.picker.sample.ui.screen.TimePickerSampleScreen
import com.kez.picker.sample.ui.theme.AppTheme

@Composable
fun App() {
    fun handleNavigateBack(navController: NavHostController) {
        if (navController.currentBackStackEntry?.destination?.route != Screen.Home.route) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Home.route) { inclusive = true }
            }
        } else {
            navController.popBackStack()
        }
    }

    AppTheme {
        val navController = rememberNavController()
        NavHost(
            modifier = Modifier.fillMaxSize(),
            navController = navController,
            startDestination = Screen.Home.route
        ) {
            composable(Screen.Home.route) { HomeScreen(navController) }
            composable(Screen.Integrated.route) {
                IntegratedPickerScreen(
                    onBackPressed = { handleNavigateBack(navController) }
                )
            }
            composable(Screen.TimePicker.route) {
                TimePickerSampleScreen(
                    onBackPressed = { handleNavigateBack(navController) }
                )
            }
            composable(Screen.DatePicker.route) {
                DatePickerSampleScreen(
                    onBackPressed = { handleNavigateBack(navController) }
                )
            }
            composable(Screen.BottomSheet.route) {
                BottomSheetSampleScreen(
                    onBackPressed = { handleNavigateBack(navController) }
                )
            }
        }
    }
}