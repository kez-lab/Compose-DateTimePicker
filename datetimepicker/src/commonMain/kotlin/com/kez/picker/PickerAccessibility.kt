package com.kez.picker

import androidx.compose.runtime.Immutable
import com.kez.picker.util.TimePeriod

/**
 * Accessibility configuration for one [Picker] column.
 *
 * @param pickerLabel Optional label used as the accessibility prefix for the picker column.
 * @param itemContentDescription Accessibility description for each item value.
 * @param previousItemActionLabel Accessibility action label for selecting the previous item. Pass null or blank to omit the action.
 * @param nextItemActionLabel Accessibility action label for selecting the next item. Pass null or blank to omit the action.
 * @see PickerDefaults.accessibility
 */
@Immutable
data class PickerAccessibility<T : Any>(
    val pickerLabel: String? = null,
    val itemContentDescription: (T) -> String = { it.toString() },
    val previousItemActionLabel: String? = PickerDefaults.PreviousItemActionLabel,
    val nextItemActionLabel: String? = PickerDefaults.NextItemActionLabel
)

/**
 * Accessibility configuration for [com.kez.picker.time.TimePicker].
 *
 * @param hour Accessibility configuration for the hour picker column.
 * @param minute Accessibility configuration for the minute picker column.
 * @param period Accessibility configuration for the AM/PM picker column in 12-hour time.
 * @see PickerDefaults.timePickerAccessibility
 */
@Immutable
data class TimePickerAccessibility(
    val hour: PickerAccessibility<Int>,
    val minute: PickerAccessibility<Int>,
    val period: PickerAccessibility<TimePeriod>
)

/**
 * Accessibility configuration for [com.kez.picker.date.DatePicker].
 *
 * @param year Accessibility configuration for the year picker column.
 * @param month Accessibility configuration for the month picker column.
 * @param day Accessibility configuration for the day picker column.
 * @see PickerDefaults.datePickerAccessibility
 */
@Immutable
data class DatePickerAccessibility(
    val year: PickerAccessibility<Int>,
    val month: PickerAccessibility<Int>,
    val day: PickerAccessibility<Int>
)

/**
 * Accessibility configuration for [com.kez.picker.date.DateRangePicker].
 *
 * @param start Accessibility configuration for the start date picker.
 * @param end Accessibility configuration for the end date picker.
 * @see PickerDefaults.dateRangePickerAccessibility
 */
@Immutable
data class DateRangePickerAccessibility(
    val start: DatePickerAccessibility,
    val end: DatePickerAccessibility
)

/**
 * Accessibility configuration for [com.kez.picker.date.YearMonthPicker].
 *
 * @param year Accessibility configuration for the year picker column.
 * @param month Accessibility configuration for the month picker column.
 * @see PickerDefaults.yearMonthPickerAccessibility
 */
@Immutable
data class YearMonthPickerAccessibility(
    val year: PickerAccessibility<Int>,
    val month: PickerAccessibility<Int>
)
