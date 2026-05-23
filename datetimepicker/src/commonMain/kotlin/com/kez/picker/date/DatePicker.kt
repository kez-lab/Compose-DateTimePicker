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
import com.kez.picker.DatePickerAccessibility
import com.kez.picker.DatePickerDisplay
import com.kez.picker.DatePickerItems
import com.kez.picker.Picker
import com.kez.picker.PickerDefaults
import com.kez.picker.PickerStyle
import kotlinx.datetime.LocalDate
import kotlin.math.abs

/**
 * A date picker component that allows selecting year, month, and day.
 *
 * @param modifier The modifier to be applied to the component.
 * @param pickerModifier The modifier to be applied to each picker.
 * @param state The state object to control the picker.
 * @param onSelectedDateChange Called after user interaction changes the selected date.
 * @param items Selectable year, month, and day item lists for the picker.
 * @param display Visible item text formatters for each picker column.
 * @param style Visual and layout styling for each picker column.
 * @param spacingBetweenPickers The spacing between the pickers.
 * @param accessibility Accessibility labels, item descriptions, and custom action labels for each picker column.
 * @throws IllegalArgumentException if custom item lists are empty, contain duplicates, contain values outside the supported ranges, or omit the current selected year/month/day after date constraints are applied.
 */
@Composable
fun DatePicker(
    modifier: Modifier = Modifier,
    pickerModifier: Modifier = Modifier,
    state: DatePickerState = rememberDatePickerState(),
    onSelectedDateChange: (LocalDate) -> Unit = {},
    items: DatePickerItems = PickerDefaults.datePickerItems(),
    display: DatePickerDisplay = PickerDefaults.datePickerDisplay(),
    style: PickerStyle = PickerDefaults.style(),
    spacingBetweenPickers: Dp = PickerDefaults.SpacingBetweenPickers,
    accessibility: DatePickerAccessibility = PickerDefaults.datePickerAccessibility()
) {
    validateDatePickerItems(
        state = state,
        items = items
    )

    fun moveSelectionInsideAvailableItems() {
        val availableMonthItems = items.selectableMonthItemsFor(state.selectedYear)
        if (state.selectedMonth !in availableMonthItems) {
            state.selectMonth(availableMonthItems.closestTo(state.selectedMonth))
        }
        val availableDayItems = items.selectableDayItemsFor(
            year = state.selectedYear,
            month = state.selectedMonth
        )
        if (state.selectedDay !in availableDayItems) {
            state.selectDay(availableDayItems.closestTo(state.selectedDay))
        }
    }

    fun updateSelectedDate(update: () -> Unit) {
        val previousDate = state.selectedDate
        update()
        moveSelectionInsideAvailableItems()
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
            val yearItems = items.selectableYearItems()
            val monthItems = items.selectableMonthItemsFor(state.selectedYear)
            val dayItems = items.selectableDayItemsFor(
                year = state.selectedYear,
                month = state.selectedMonth
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Picker(
                    items = yearItems,
                    selectedItem = state.selectedYear,
                    onSelectedItemChange = { year ->
                        updateSelectedDate { state.selectYear(year) }
                    },
                    modifier = pickerModifier.weight(1.2f), // Give Year slightly more width
                    style = style,
                    accessibility = accessibility.year,
                    itemText = display.year.itemText
                )

                Picker(
                    items = monthItems,
                    selectedItem = state.selectedMonth,
                    onSelectedItemChange = { month ->
                        updateSelectedDate { state.selectMonth(month) }
                    },
                    modifier = pickerModifier.weight(0.8f),
                    style = style,
                    accessibility = accessibility.month,
                    itemText = display.month.itemText
                )

                key(dayItems) {
                    Picker(
                        items = dayItems,
                        selectedItem = state.selectedDay,
                        onSelectedItemChange = { day ->
                            updateSelectedDate { state.selectDay(day) }
                        },
                        modifier = pickerModifier.weight(0.8f),
                        style = style,
                        isInfinity = false,
                        accessibility = accessibility.day,
                        itemText = display.day.itemText
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
            monthItems = monthItems,
            dayItems = (1..31).toList()
        )
    )
}

internal fun validateDatePickerItems(
    state: DatePickerState,
    items: DatePickerItems
) {
    val yearItems = items.yearItems
    val monthItems = items.monthItems
    val dayItems = items.dayItems

    val yearRange = 1000..9999
    val monthRange = 1..12
    val dayRange = 1..31
    val invalidYears = yearItems.invalidValuesFor(yearRange)
    val invalidMonths = monthItems.invalidValuesFor(monthRange)
    val invalidDays = dayItems.invalidValuesFor(dayRange)

    require(yearItems.isNotEmpty()) { "DatePicker yearItems must not be empty." }
    require(monthItems.isNotEmpty()) { "DatePicker monthItems must not be empty." }
    require(dayItems.isNotEmpty()) { "DatePicker dayItems must not be empty." }
    require(yearItems.distinct().size == yearItems.size) {
        "DatePicker yearItems must not contain duplicate values."
    }
    require(monthItems.distinct().size == monthItems.size) {
        "DatePicker monthItems must not contain duplicate values."
    }
    require(dayItems.distinct().size == dayItems.size) {
        "DatePicker dayItems must not contain duplicate values."
    }
    require(invalidYears.isEmpty()) {
        "DatePicker yearItems must contain only values in range [1000, 9999]. " +
                "Invalid values: $invalidYears"
    }
    require(invalidMonths.isEmpty()) {
        "DatePicker monthItems must contain only values in range [1, 12]. " +
                "Invalid values: $invalidMonths"
    }
    require(invalidDays.isEmpty()) {
        "DatePicker dayItems must contain only values in range [1, 31]. " +
                "Invalid values: $invalidDays"
    }
    require(state.selectedYear in yearItems) {
        "DatePicker yearItems must contain state.selectedYear=${state.selectedYear}."
    }
    require(state.selectedMonth in monthItems) {
        "DatePicker monthItems must contain state.selectedMonth=${state.selectedMonth}."
    }
    if (items.constraints.isUnbounded) {
        val minimumMaxDay = minimumMaxDayFor(yearItems, monthItems)
        require(dayItems.any { it <= minimumMaxDay }) {
            "DatePicker dayItems must contain at least one day valid for every selectable " +
                    "year/month combination. Smallest maximum day is $minimumMaxDay."
        }
    }
    val availableYearItems = items.selectableYearItems()
    require(state.selectedYear in availableYearItems) {
        "DatePicker constraints must allow state.selectedYear=${state.selectedYear}."
    }
    val availableMonthItems = items.selectableMonthItemsFor(state.selectedYear)
    require(state.selectedMonth in availableMonthItems) {
        "DatePicker constraints must allow state.selectedMonth=${state.selectedMonth} " +
                "for selectedYear=${state.selectedYear}."
    }
    val availableDayItems = items.selectableDayItemsFor(
        year = state.selectedYear,
        month = state.selectedMonth
    )
    require(state.selectedDay in availableDayItems) {
        "DatePicker dayItems and constraints must allow state.selectedDay=${state.selectedDay} " +
                "for selectedYear=${state.selectedYear} and selectedMonth=${state.selectedMonth}."
    }
}

private fun List<Int>.invalidValuesFor(range: IntRange): List<Int> =
    filterNot { it in range }.distinct()

private fun List<Int>.closestTo(value: Int): Int =
    minWith(
        compareBy<Int> { abs(it - value) }
            .thenBy { it }
    )

private fun minimumMaxDayFor(yearItems: List<Int>, monthItems: List<Int>): Int =
    monthItems.minOf { month ->
        when (month) {
            2 -> if (yearItems.any { daysInMonth(it, month) == 28 }) 28 else 29
            4, 6, 9, 11 -> 30
            else -> 31
        }
    }

@Preview(name = "Default", group = "DatePicker", showBackground = true)
@Composable
private fun DatePickerPreview() {
    DatePicker()
}
