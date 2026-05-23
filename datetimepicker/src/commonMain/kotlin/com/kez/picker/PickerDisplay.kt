package com.kez.picker

import androidx.compose.runtime.Immutable
import com.kez.picker.util.TimePeriod

/**
 * Text displayed for one [Picker] column.
 *
 * This controls visible item text. Accessibility output is configured separately through
 * [PickerAccessibility].
 *
 * @param itemText Text displayed for each item value.
 * @see PickerDefaults.itemText
 */
@Immutable
data class PickerItemText<T : Any>(
    val itemText: (T) -> String = { it.toString() }
)

/**
 * Visible item text configuration for [com.kez.picker.time.TimePicker].
 *
 * @param hour Text displayed for hour values.
 * @param minute Text displayed for minute values.
 * @param period Text displayed for AM/PM values.
 * @see PickerDefaults.timePickerDisplay
 */
@Immutable
data class TimePickerDisplay(
    val hour: PickerItemText<Int>,
    val minute: PickerItemText<Int>,
    val period: PickerItemText<TimePeriod>
)

/**
 * Visible item text configuration for [com.kez.picker.date.DatePicker].
 *
 * @param year Text displayed for year values.
 * @param month Text displayed for month values.
 * @param day Text displayed for day values.
 * @see PickerDefaults.datePickerDisplay
 */
@Immutable
data class DatePickerDisplay(
    val year: PickerItemText<Int>,
    val month: PickerItemText<Int>,
    val day: PickerItemText<Int>
)

/**
 * Visible item text configuration for [com.kez.picker.date.YearMonthPicker].
 *
 * @param year Text displayed for year values.
 * @param month Text displayed for month values.
 * @see PickerDefaults.yearMonthPickerDisplay
 */
@Immutable
data class YearMonthPickerDisplay(
    val year: PickerItemText<Int>,
    val month: PickerItemText<Int>
)
