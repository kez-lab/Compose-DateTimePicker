package com.kez.picker

import androidx.compose.runtime.Immutable

/**
 * Semantics configuration for one [Picker] column.
 *
 * Value descriptions are configured through [PickerItemFormat]. This object only describes the
 * picker column and its custom accessibility actions.
 *
 * @param pickerLabel Optional label used as the accessibility prefix for the picker column.
 * @param previousItemActionLabel Accessibility action label for selecting the previous item. Pass null or blank to omit the action.
 * @param nextItemActionLabel Accessibility action label for selecting the next item. Pass null or blank to omit the action.
 * @see PickerDefaults.semantics
 */
@Immutable
data class PickerSemantics(
    val pickerLabel: String? = null,
    val previousItemActionLabel: String? = PickerDefaults.PreviousItemActionLabel,
    val nextItemActionLabel: String? = PickerDefaults.NextItemActionLabel
)

/**
 * Semantics configuration for [com.kez.picker.time.TimePicker].
 *
 * @param hour Accessibility configuration for the hour picker column.
 * @param minute Accessibility configuration for the minute picker column.
 * @param period Accessibility configuration for the AM/PM picker column in 12-hour time.
 * @see PickerDefaults.timePickerSemantics
 */
@Immutable
data class TimePickerSemantics(
    val hour: PickerSemantics,
    val minute: PickerSemantics,
    val period: PickerSemantics
)

/**
 * Semantics configuration for [com.kez.picker.date.DatePicker].
 *
 * @param year Accessibility configuration for the year picker column.
 * @param month Accessibility configuration for the month picker column.
 * @param day Accessibility configuration for the day picker column.
 * @see PickerDefaults.datePickerSemantics
 */
@Immutable
data class DatePickerSemantics(
    val year: PickerSemantics,
    val month: PickerSemantics,
    val day: PickerSemantics
)

/**
 * Semantics configuration for [com.kez.picker.date.DateRangePicker].
 *
 * @param start Accessibility configuration for the start date picker.
 * @param end Accessibility configuration for the end date picker.
 * @see PickerDefaults.dateRangePickerSemantics
 */
@Immutable
data class DateRangePickerSemantics(
    val start: DatePickerSemantics,
    val end: DatePickerSemantics
)

/**
 * Semantics configuration for [com.kez.picker.date.YearMonthPicker].
 *
 * @param year Accessibility configuration for the year picker column.
 * @param month Accessibility configuration for the month picker column.
 * @see PickerDefaults.yearMonthPickerSemantics
 */
@Immutable
data class YearMonthPickerSemantics(
    val year: PickerSemantics,
    val month: PickerSemantics
)
