package com.kez.picker.date

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.kez.picker.DatePickerColumn
import com.kez.picker.DatePickerFormat
import com.kez.picker.DatePickerItems
import com.kez.picker.DatePickerLayout
import com.kez.picker.DatePickerSemantics
import com.kez.picker.Picker
import com.kez.picker.PickerDefaults
import com.kez.picker.PickerStyle
import com.kez.picker.pickerColumnModifier
import kotlinx.datetime.LocalDate
import kotlin.math.abs

/**
 * A date picker component that allows selecting year, month, and day.
 *
 * @param modifier The modifier to be applied to the component.
 * @param pickerModifier The modifier to be applied to each picker.
 * @param state The state object to control the picker.
 * @param onSelectedDateChange Called after user interaction changes the selected date.
 * Programmatic [DatePickerState.selectDate] calls update [state] directly and do not invoke this
 * callback. When app code changes the picker from a button, preset, or external value, update any
 * app-owned state in the same handler.
 * @param enabled Whether user scroll, click, and semantics selection actions are enabled.
 * @param items Selectable year, month, and day item lists for the picker.
 * @param format Visible item text and optional accessibility value descriptions for each picker column.
 * @param style Visual and layout styling for each picker column.
 * @param layout Column layout weights and visual order for each picker column.
 * @param spacingBetweenPickers The spacing between the pickers.
 * @param semantics Accessibility labels and custom action labels for each picker column.
 * @throws IllegalArgumentException if custom item lists are empty, contain duplicates, contain values outside the supported ranges, or omit the current selected year/month/day after date constraints are applied.
 */
@Composable
fun DatePicker(
    modifier: Modifier = Modifier,
    pickerModifier: Modifier = Modifier,
    state: DatePickerState = rememberDatePickerState(),
    onSelectedDateChange: (LocalDate) -> Unit = {},
    enabled: Boolean = true,
    items: DatePickerItems = PickerDefaults.datePickerItems(),
    format: DatePickerFormat = PickerDefaults.datePickerFormat(),
    style: PickerStyle = PickerDefaults.style(),
    layout: DatePickerLayout = PickerDefaults.datePickerLayout(),
    spacingBetweenPickers: Dp = PickerDefaults.SpacingBetweenPickers,
    semantics: DatePickerSemantics = PickerDefaults.datePickerSemantics()
) {
    remember(items, state, state.selectedYear, state.selectedMonth, state.selectedDay) {
        validateDatePickerItems(state = state, items = items)
    }

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
            val yearItems by remember(items) {
                derivedStateOf { items.selectableYearItems() }
            }
            val monthItems by remember(items, state) {
                derivedStateOf { items.selectableMonthItemsFor(state.selectedYear) }
            }
            val dayItems by remember(items, state) {
                derivedStateOf {
                    items.selectableDayItemsFor(
                        year = state.selectedYear,
                        month = state.selectedMonth
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    spacingBetweenPickers,
                    Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                layout.columnOrder.forEach { column ->
                    key(column) {
                        when (column) {
                            DatePickerColumn.YEAR -> {
                                Picker(
                                    items = yearItems,
                                    selectedItem = state.selectedYear,
                                    onSelectedItemChange = { year ->
                                        updateSelectedDate { state.selectYear(year) }
                                    },
                                    modifier = pickerColumnModifier(pickerModifier, layout.yearWeight),
                                    enabled = enabled,
                                    style = style,
                                    isInfinity = false,
                                    semantics = semantics.year,
                                    format = format.year
                                )
                            }

                            DatePickerColumn.MONTH -> {
                                Picker(
                                    items = monthItems,
                                    selectedItem = state.selectedMonth,
                                    onSelectedItemChange = { month ->
                                        updateSelectedDate { state.selectMonth(month) }
                                    },
                                    modifier = pickerColumnModifier(pickerModifier, layout.monthWeight),
                                    enabled = enabled,
                                    style = style,
                                    semantics = semantics.month,
                                    format = format.month
                                )
                            }

                            DatePickerColumn.DAY -> {
                                Picker(
                                    items = dayItems,
                                    selectedItem = state.selectedDay,
                                    onSelectedItemChange = { day ->
                                        updateSelectedDate { state.selectDay(day) }
                                    },
                                    modifier = pickerColumnModifier(pickerModifier, layout.dayWeight),
                                    enabled = enabled,
                                    style = style,
                                    isInfinity = false,
                                    semantics = semantics.day,
                                    format = format.day
                                )
                            }
                        }
                    }
                }
            }
        }
    }
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
        "DatePicker yearItems must contain state.selectedYear=${state.selectedYear}. " +
                datePickerStateItemsAdvice()
    }
    require(state.selectedMonth in monthItems) {
        "DatePicker monthItems must contain state.selectedMonth=${state.selectedMonth}. " +
                datePickerStateItemsAdvice()
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
        "DatePicker constraints must allow state.selectedYear=${state.selectedYear}. " +
                datePickerStateItemsAdvice()
    }
    val availableMonthItems = items.selectableMonthItemsFor(state.selectedYear)
    require(state.selectedMonth in availableMonthItems) {
        "DatePicker constraints must allow state.selectedMonth=${state.selectedMonth} " +
                "for selectedYear=${state.selectedYear}. " +
                datePickerStateItemsAdvice()
    }
    val availableDayItems = items.selectableDayItemsFor(
        year = state.selectedYear,
        month = state.selectedMonth
    )
    require(state.selectedDay in availableDayItems) {
        "DatePicker dayItems and constraints must allow state.selectedDay=${state.selectedDay} " +
                "for selectedYear=${state.selectedYear} and selectedMonth=${state.selectedMonth}. " +
                datePickerStateItemsAdvice()
    }
}

private fun datePickerStateItemsAdvice(): String =
    "Use rememberDatePickerState(items = items, initialDate = ...) for initial values. For later " +
            "programmatic changes, coerce app values with items.coerceDate(...) and call " +
            "state.selectDate(date, items) before composing DatePicker."

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
