package com.kez.picker

import com.kez.picker.date.YearMonthPickerState
import com.kez.picker.date.validateYearMonthPickerItems
import com.kez.picker.time.TimePickerState
import com.kez.picker.time.validateTimePickerItems
import com.kez.picker.util.TimePeriod

internal fun validateTimePickerItems(
    state: TimePickerState,
    minuteItems: List<Int>,
    hourItems: List<Int>,
    periodItems: List<TimePeriod>
) {
    validateTimePickerItems(
        state = state,
        items = TimePickerItems(
            minuteItems = minuteItems,
            hour24Items = hourItems,
            hour12Items = hourItems,
            periodItems = periodItems
        )
    )
}

internal fun validateYearMonthPickerItems(
    state: YearMonthPickerState,
    yearItems: List<Int>,
    monthItems: List<Int>
) {
    validateYearMonthPickerItems(
        state = state,
        items = YearMonthPickerItems(
            yearItems = yearItems,
            monthItems = monthItems
        )
    )
}
