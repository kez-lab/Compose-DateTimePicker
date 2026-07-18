package com.kez.picker.date

import com.kez.picker.DatePickerItems
import com.kez.picker.DatePickerColumn
import com.kez.picker.closestPickerValueTo
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

/**
 * Applies one DatePicker column change and repairs every dependent part as one logical value.
 */
internal fun DatePickerItems.repairedDateAfter(
    currentDate: LocalDate,
    column: DatePickerColumn,
    value: Int
): LocalDate {
    val isActiveValue = when (column) {
        DatePickerColumn.YEAR ->
            value in yearItems && selectableMonthItemsFor(year = value).isNotEmpty()

        DatePickerColumn.MONTH -> value in selectableMonthItemsFor(year = currentDate.year)
        DatePickerColumn.DAY -> value in selectableDayItemsFor(
            year = currentDate.year,
            month = currentDate.month.number
        )
    }
    if (!isActiveValue) return currentDate

    val nextYear = if (column == DatePickerColumn.YEAR) value else currentDate.year
    val availableMonths = selectableMonthItemsFor(year = nextYear)
    val requestedMonth = when (column) {
        DatePickerColumn.MONTH -> value
        DatePickerColumn.YEAR,
        DatePickerColumn.DAY -> currentDate.month.number
    }
    val nextMonth = availableMonths.closestPickerValueTo(
        value = requestedMonth,
        sourceName = "DatePicker dependent month items for year=$nextYear"
    )
    val availableDays = selectableDayItemsFor(year = nextYear, month = nextMonth)
    val requestedDay = when (column) {
        DatePickerColumn.DAY -> value
        DatePickerColumn.YEAR,
        DatePickerColumn.MONTH -> currentDate.day
    }
    val nextDay = availableDays.closestPickerValueTo(
        value = requestedDay,
        sourceName = "DatePicker dependent day items for year=$nextYear, month=$nextMonth"
    )
    return LocalDate(
        year = nextYear,
        month = nextMonth,
        day = nextDay
    )
}
