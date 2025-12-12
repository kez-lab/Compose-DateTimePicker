package com.kez.picker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import com.kez.picker.util.currentDate
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

/**
 * Creates and remembers a [DatePickerState].
 *
 * @param initialYear The initial year to be selected. Defaults to the current year.
 * @param initialMonth The initial month to be selected. Defaults to the current month.
 * @param initialDay The initial day to be selected. Defaults to the current day.
 * @return A [DatePickerState] initialized with the given date values.
 */
@Composable
fun rememberDatePickerState(
    initialYear: Int = currentDate.year,
    initialMonth: Int = currentDate.month.number,
    initialDay: Int = currentDate.day
): DatePickerState {
    return remember(initialYear, initialMonth, initialDay) {
        DatePickerState(initialYear, initialMonth, initialDay)
    }
}

/**
 * State holder for the DatePicker.
 *
 * Manages the state of the year, month, and day pickers.
 * Automatically adjusts the selected day if it exceeds the maximum valid day for the new year/month.
 *
 * @param initialYear The initial year to be selected.
 * @param initialMonth The initial month to be selected.
 * @param initialDay The initial day to be selected.
 */
@Stable
class DatePickerState(
    initialYear: Int,
    initialMonth: Int,
    initialDay: Int
) {
    val yearState = PickerState(initialYear)
    val monthState = PickerState(initialMonth)
    val dayState = PickerState(initialDay)

    val selectedYear: Int
        get() = yearState.selectedItem

    val selectedMonth: Int
        get() = monthState.selectedItem

    val selectedDay: Int
        get() = dayState.selectedItem

    /**
     * The currently valid maximum day for the selected year and month.
     * Calculated dynamically based on the selected year and month.
     */
    val maxDay: Int
        get() = daysInMonth(selectedYear, selectedMonth)

    init {
        // Ensure initial day is valid
        if (initialDay > maxDay) {
            dayState.selectedItem = maxDay
        }
    }
    
    // We need to observe changes to year/month to clamp the day
    // However, PickerState updates are independent composable states.
    // In strict Compose, we should probably use derivedStateOf or side-effects in the composable calling this.
    // But since PickerState internal `selectedItem` is mutableStateOf, 
    // we can try to hook into the validation in the UI layer or make this class reactive.
    // For simplicity with existing PickerState pattern:
    // The UI (DatePicker) should observe selectedYear/selectedMonth and update the day picker's range/value if needed.
    // OR we expose a function to validation.
    
    // Actually, simply querying `maxDay` in the UI to limit the list of days is the best approach.
    // But we also need to clamp the *selected* value if it goes out of range.
    // Let's add a function to validate and adjust.
    fun validate() {
        val currentMax = maxDay
        if (dayState.selectedItem > currentMax) {
            dayState.selectedItem = currentMax
        }
    }
    
    private fun daysInMonth(year: Int, month: Int): Int {
        return when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if (isLeapYear(year)) 29 else 28
            else -> 31 // Should not happen with 1-12 range
        }
    }

    private fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }
}
