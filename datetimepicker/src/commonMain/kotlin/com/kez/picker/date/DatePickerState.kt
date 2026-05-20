package com.kez.picker.date

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import com.kez.picker.PickerState
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
    initialYear: Int = currentDate().year,
    initialMonth: Int = currentDate().month.number,
    initialDay: Int = currentDate().day
): DatePickerState {
    return rememberSaveable(initialYear, initialMonth, initialDay, saver = DatePickerState.Saver) {
        DatePickerState(initialYear, initialMonth, initialDay)
    }
}

/**
 * State holder for the DatePicker.
 *
 * Manages the state of the year, month, and day pickers.
 * Automatically adjusts the selected day if it exceeds the maximum valid day for the new year/month.
 * Internal picker states are not directly accessible to prevent inconsistent state modifications.
 *
 * @param initialYear The initial year to be selected. Must be in 1000..9999.
 * @param initialMonth The initial month to be selected.
 * @param initialDay The initial day to be selected.
 * @throws IllegalArgumentException if [initialYear], [initialMonth], or [initialDay] is outside the supported range.
 */
@Stable
class DatePickerState(
    initialYear: Int,
    initialMonth: Int,
    initialDay: Int
) {
    internal val yearState = PickerState(initialYear)
    internal val monthState = PickerState(initialMonth)
    internal val dayState = PickerState(initialDay)

    /**
     * The currently selected year.
     */
    val selectedYear: Int
        get() = yearState.selectedItem

    /**
     * The currently selected month (1-12).
     */
    val selectedMonth: Int
        get() = monthState.selectedItem

    /**
     * The currently selected day (1-31).
     */
    val selectedDay: Int
        get() = dayState.selectedItem

    /**
     * The currently selected date.
     */
    val selectedDate: LocalDate
        get() = LocalDate(selectedYear, selectedMonth, selectedDay.coerceIn(1, maxDay))

    /**
     * The currently valid maximum day for the selected year and month.
     * Calculated dynamically based on the selected year and month.
     */
    val maxDay: Int
        get() = daysInMonth(selectedYear, selectedMonth)

    init {
        require(initialYear in 1000..9999) {
            "initialYear must be in range [1000, 9999], but was $initialYear"
        }
        require(initialMonth in 1..12) {
            "initialMonth must be in range [1, 12], but was $initialMonth"
        }
        require(initialDay >= 1) {
            "initialDay must be greater than or equal to 1, but was $initialDay"
        }
        val initialMaxDay = daysInMonth(initialYear, initialMonth)
        if (initialDay > initialMaxDay) {
            dayState.selectedItem = initialMaxDay
        }
    }

    /**
     * Validates and adjusts the selected day if it exceeds the maximum valid day
     * for the currently selected year and month.
     *
     * This function should be called when the year or month changes to ensure
     * the day remains valid (e.g., Feb 30 -> Feb 28/29).
     */
    internal fun validate() {
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

    companion object {
        /**
         * Saves and restores [DatePickerState] across configuration changes.
         */
        val Saver: Saver<DatePickerState, Any> = listSaver(
            save = { listOf(it.selectedYear, it.selectedMonth, it.selectedDay) },
            restore = {
                DatePickerState(
                    initialYear = it[0] as Int,
                    initialMonth = it[1] as Int,
                    initialDay = it[2] as Int
                )
            }
        )
    }
}
