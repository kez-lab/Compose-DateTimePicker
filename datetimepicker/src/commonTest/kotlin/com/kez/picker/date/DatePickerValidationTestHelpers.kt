package com.kez.picker.date

import com.kez.picker.DatePickerItems

internal fun validateDatePickerItems(
    state: DatePickerState,
    yearItems: List<Int>,
    monthItems: List<Int>
) {
    validateDatePickerItems(
        state = state,
        items = DatePickerItems(
            yearItems = yearItems,
            monthItems = monthItems,
            dayItems = (1..31).toList()
        )
    )
}
