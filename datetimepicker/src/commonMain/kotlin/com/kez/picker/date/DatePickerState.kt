package com.kez.picker.date

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.kez.picker.DatePickerItems
import com.kez.picker.util.currentDate
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

/**
 * Creates and remembers a [DatePickerState].
 * Initial date values are read when the state is first created.
 *
 * @param initialYear The initial year to be selected. Defaults to the current year.
 * @param initialMonth The initial month to be selected. Defaults to the current month.
 * @param initialDay The initial day to be selected. Defaults to the current day.
 * @return A [DatePickerState] initialized with the given date values.
 */
@Composable
fun rememberDatePickerState(
    initialDate: LocalDate = currentDate()
): DatePickerState {
    val rememberedInitialDate = remember { initialDate }
    return rememberSaveable(saver = DatePickerState.Saver) {
        DatePickerState(
            initialYear = rememberedInitialDate.year,
            initialMonth = rememberedInitialDate.month.number,
            initialDay = rememberedInitialDate.day
        )
    }
}

/**
 * Creates and remembers a [DatePickerState] whose initial value is coerced by [items].
 *
 * Initial values and [items] are read when the state is first created. This is useful when the
 * picker is rendered with custom item lists and restored app state may fall outside those lists.
 *
 * @param items Selectable values used to coerce [initialDate] before creating the state.
 * @param initialDate The requested initial date.
 * @return A [DatePickerState] initialized to the closest selectable date.
 */
@Composable
fun rememberDatePickerState(
    items: DatePickerItems,
    initialDate: LocalDate = currentDate()
): DatePickerState {
    val rememberedInitialDate = remember { initialDate }
    val rememberedItems = remember { items }
    val coercedInitialDate = remember(rememberedInitialDate, rememberedItems) {
        rememberedItems.coerceDate(rememberedInitialDate)
    }
    return rememberDatePickerState(initialDate = coercedInitialDate)
}

/**
 * Creates and remembers a [DatePickerState] with explicit year, month, and day values.
 *
 * Initial values are read only when the state is first created.
 *
 * @param initialYear The initial year to be selected. Must be in 1000..9999.
 * @param initialMonth The initial month to be selected. Must be in 1..12.
 * @param initialDay The initial day to be selected. Must be at least 1.
 * @return A [DatePickerState] initialized with the given date values.
 */
@Composable
fun rememberDatePickerState(
    initialYear: Int,
    initialMonth: Int,
    initialDay: Int
): DatePickerState {
    val rememberedInitialYear = remember { initialYear }
    val rememberedInitialMonth = remember { initialMonth }
    val rememberedInitialDay = remember { initialDay }
    return rememberSaveable(saver = DatePickerState.Saver) {
        DatePickerState(rememberedInitialYear, rememberedInitialMonth, rememberedInitialDay)
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
 * @param initialMonth The initial month to be selected. Must be in 1..12.
 * @param initialDay The initial day to be selected. Must be at least 1. Values greater than the
 * maximum valid day for [initialYear] and [initialMonth] are clamped to that maximum.
 * @throws IllegalArgumentException if [initialYear] or [initialMonth] is outside the supported
 * range, or if [initialDay] is less than 1.
 */
@Stable
class DatePickerState(
    initialYear: Int,
    initialMonth: Int,
    initialDay: Int
) {
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
    }

    private var mutableSelectedYear: Int by mutableStateOf(initialYear)
    private var mutableSelectedMonth: Int by mutableStateOf(initialMonth)
    private var mutableSelectedDay: Int by mutableStateOf(
        initialDay.coerceAtMost(daysInMonth(initialYear, initialMonth))
    )

    /**
     * The currently selected year.
     */
    val selectedYear: Int
        get() = mutableSelectedYear

    /**
     * The currently selected month (1-12).
     */
    val selectedMonth: Int
        get() = mutableSelectedMonth

    /**
     * The currently selected day (1-31).
     */
    val selectedDay: Int
        get() = mutableSelectedDay

    /**
     * The currently selected date.
     */
    val selectedDate: LocalDate
        get() = LocalDate(selectedYear, selectedMonth, selectedDay)

    /**
     * Programmatically selects [date].
     *
     * @throws IllegalArgumentException if [date]'s year is outside the supported range.
     */
    fun selectDate(date: LocalDate) {
        updateDate(
            year = date.year,
            month = date.month.number,
            day = date.day
        )
    }

    /**
     * Programmatically selects the closest date to [date] that is allowed by [items].
     *
     * Use this overload when app-owned state can contain values outside custom picker lists.
     */
    fun selectDate(date: LocalDate, items: DatePickerItems) {
        selectDate(items.coerceDate(date))
    }

    /**
     * The currently valid maximum day for the selected year and month.
     * Calculated dynamically based on the selected year and month.
     */
    val maxDay: Int
        get() = daysInMonth(selectedYear, selectedMonth)

    internal fun selectYear(year: Int) {
        updateDate(
            year = year,
            month = selectedMonth,
            day = selectedDay
        )
    }

    internal fun selectMonth(month: Int) {
        updateDate(
            year = selectedYear,
            month = month,
            day = selectedDay
        )
    }

    internal fun selectDay(day: Int) {
        updateDate(
            year = selectedYear,
            month = selectedMonth,
            day = day
        )
    }

    private fun updateDate(year: Int, month: Int, day: Int) {
        require(year in 1000..9999) {
            "year must be in range [1000, 9999], but was $year"
        }
        require(month in 1..12) {
            "month must be in range [1, 12], but was $month"
        }
        require(day >= 1) {
            "day must be greater than or equal to 1, but was $day"
        }
        mutableSelectedYear = year
        mutableSelectedMonth = month
        mutableSelectedDay = day.coerceAtMost(daysInMonth(year, month))
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

internal fun daysInMonth(year: Int, month: Int): Int {
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
