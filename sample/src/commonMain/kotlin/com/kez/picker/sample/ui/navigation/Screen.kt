package com.kez.picker.sample.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object WheelPicker : Screen("wheel_picker")
    object Integrated : Screen("integrated")
    object TimePicker : Screen("time_picker")
    object DurationPicker : Screen("duration_picker")
    object YearMonthPicker : Screen("year_month_picker")
    object DatePicker : Screen("date_picker")
    object DateRangePicker : Screen("date_range_picker")
    object BottomSheet : Screen("bottom_sheet")
    object BackgroundStyle : Screen("background_style")
}
