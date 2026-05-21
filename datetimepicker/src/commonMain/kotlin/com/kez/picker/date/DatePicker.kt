package com.kez.picker.date

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.kez.picker.DatePickerItems
import com.kez.picker.Picker
import com.kez.picker.PickerDefaults
import com.kez.picker.PickerStyle
import com.kez.picker.DatePickerAccessibility
import kotlinx.datetime.LocalDate

/**
 * A date picker component that allows selecting year, month, and day.
 *
 * @param modifier The modifier to be applied to the component.
 * @param pickerModifier The modifier to be applied to each picker.
 * @param state The state object to control the picker.
 * @param onSelectedDateChange Called after user interaction changes the selected date.
 * @param items Selectable year and month item lists for the picker.
 * @param style Visual and layout styling for each picker column.
 * @param spacingBetweenPickers The spacing between the pickers.
 * @param accessibility Accessibility labels, item descriptions, and custom action labels for each picker column.
 * @throws IllegalArgumentException if custom item lists are empty, contain duplicates, contain values outside the supported ranges, or omit the current selected year/month.
 */
@Composable
fun DatePicker(
    modifier: Modifier = Modifier,
    pickerModifier: Modifier = Modifier,
    state: DatePickerState = rememberDatePickerState(),
    onSelectedDateChange: (LocalDate) -> Unit = {},
    items: DatePickerItems = PickerDefaults.datePickerItems(),
    style: PickerStyle = PickerDefaults.style(),
    spacingBetweenPickers: Dp = PickerDefaults.SpacingBetweenPickers,
    accessibility: DatePickerAccessibility = PickerDefaults.datePickerAccessibility()
) {
    validateDatePickerItems(
        state = state,
        items = items
    )

    fun updateSelectedDate(update: () -> Unit) {
        val previousDate = state.selectedDate
        update()
        val nextDate = state.selectedDate
        if (nextDate != previousDate) {
            onSelectedDateChange(nextDate)
        }
    }

    Box(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            val maxDay = state.maxDay
            val dayItems = (1..maxDay).toList()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Picker(
                    items = items.yearItems,
                    selectedItem = state.selectedYear,
                    onSelectedItemChange = { year ->
                        updateSelectedDate { state.selectYear(year) }
                    },
                    modifier = pickerModifier.weight(1.2f), // Give Year slightly more width
                    style = style,
                    accessibility = accessibility.year
                )

                Picker(
                    items = items.monthItems,
                    selectedItem = state.selectedMonth,
                    onSelectedItemChange = { month ->
                        updateSelectedDate { state.selectMonth(month) }
                    },
                    modifier = pickerModifier.weight(0.8f),
                    style = style,
                    accessibility = accessibility.month
                )

                key(maxDay) {
                    Picker(
                        items = dayItems,
                        selectedItem = state.selectedDay,
                        onSelectedItemChange = { day ->
                            updateSelectedDate { state.selectDay(day) }
                        },
                        modifier = pickerModifier.weight(0.8f),
                        style = style,
                        isInfinity = false,
                        accessibility = accessibility.day
                    )
                }
            }
        }
    }
}

internal fun validateDatePickerItems(
    state: DatePickerState,
    yearItems: List<Int>,
    monthItems: List<Int>
) {
    validateDatePickerItems(
        state = state,
        items = DatePickerItems(
            yearItems = yearItems,
            monthItems = monthItems
        )
    )
}

internal fun validateDatePickerItems(
    state: DatePickerState,
    items: DatePickerItems
) {
    val yearItems = items.yearItems
    val monthItems = items.monthItems

    val yearRange = 1000..9999
    val monthRange = 1..12
    val invalidYears = yearItems.invalidValuesFor(yearRange)
    val invalidMonths = monthItems.invalidValuesFor(monthRange)

    require(yearItems.isNotEmpty()) { "DatePicker yearItems must not be empty." }
    require(monthItems.isNotEmpty()) { "DatePicker monthItems must not be empty." }
    require(yearItems.distinct().size == yearItems.size) {
        "DatePicker yearItems must not contain duplicate values."
    }
    require(monthItems.distinct().size == monthItems.size) {
        "DatePicker monthItems must not contain duplicate values."
    }
    require(invalidYears.isEmpty()) {
        "DatePicker yearItems must contain only values in range [1000, 9999]. " +
                "Invalid values: $invalidYears"
    }
    require(invalidMonths.isEmpty()) {
        "DatePicker monthItems must contain only values in range [1, 12]. " +
                "Invalid values: $invalidMonths"
    }
    require(state.selectedYear in yearItems) {
        "DatePicker yearItems must contain state.selectedYear=${state.selectedYear}."
    }
    require(state.selectedMonth in monthItems) {
        "DatePicker monthItems must contain state.selectedMonth=${state.selectedMonth}."
    }
}

private fun List<Int>.invalidValuesFor(range: IntRange): List<Int> =
    filterNot { it in range }.distinct()

@Preview(name = "Default", group = "DatePicker", showBackground = true)
@Composable
private fun DatePickerPreview() {
    DatePicker()
}
