package com.kez.picker

import com.kez.picker.util.TimeFormat
import com.kez.picker.util.TimePeriod

/**
 * Selectable item lists for [com.kez.picker.time.TimePicker].
 *
 * Lists must be non-empty, contain distinct values, stay within their documented ranges, and contain
 * the current state selection for the active time format.
 *
 * @param minuteItems Minute values available for selection. Values must be in 0..59.
 * @param hour24Items Hour values available when using 24-hour time. Values must be in 0..23.
 * @param hour12Items Display-hour values available when using 12-hour time. Values must be in 1..12.
 * @param periodItems AM/PM values available when using 12-hour time.
 * @see PickerDefaults.timePickerItems
 */
data class TimePickerItems(
    val minuteItems: List<Int>,
    val hour24Items: List<Int>,
    val hour12Items: List<Int>,
    val periodItems: List<TimePeriod>
) {
    internal fun hourItemsFor(timeFormat: TimeFormat): List<Int> =
        when (timeFormat) {
            TimeFormat.HOUR_12 -> hour12Items
            TimeFormat.HOUR_24 -> hour24Items
        }
}

/**
 * Selectable item lists for [com.kez.picker.date.DatePicker].
 *
 * Lists must be non-empty, contain distinct values, stay within their documented ranges, and contain
 * the current state selection. [dayItems] is filtered by the selected year/month maximum day before
 * rendering.
 *
 * @param yearItems Year values available for selection. Values must be in 1000..9999.
 * @param monthItems Month values available for selection. Values must be in 1..12.
 * @param dayItems Day values available for selection. Values must be in 1..31.
 * @see PickerDefaults.datePickerItems
 */
data class DatePickerItems(
    val yearItems: List<Int>,
    val monthItems: List<Int>,
    val dayItems: List<Int>
)

/**
 * Selectable item lists for [com.kez.picker.date.YearMonthPicker].
 *
 * Lists must be non-empty, contain distinct values, stay within their documented ranges, and contain
 * the current state selection.
 *
 * @param yearItems Year values available for selection. Values must be in 1000..9999.
 * @param monthItems Month values available for selection. Values must be in 1..12.
 * @see PickerDefaults.yearMonthPickerItems
 */
data class YearMonthPickerItems(
    val yearItems: List<Int>,
    val monthItems: List<Int>
)
