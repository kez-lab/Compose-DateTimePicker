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
import com.kez.picker.PickerSelectionBand
import com.kez.picker.PickerSelectionIndicator
import com.kez.picker.PickerStyle
import com.kez.picker.commitMultiWheelSelection
import com.kez.picker.maxPickerItemHeight
import com.kez.picker.pickerColumnModifier
import com.kez.picker.rememberPickerItemHeight
import kotlinx.datetime.LocalDate

/**
 * A date picker component that allows selecting year, month, and day.
 *
 * @param modifier The modifier to be applied to the component.
 * @param pickerModifier The modifier to be applied to each picker.
 * @param state The state object to control the picker.
 * @param onSelectedDateChange Called once after a user interaction settles on a new column value,
 * dependent month/day values are repaired, and the final selectable date is committed to [state].
 * If an upstream settle replaces a still-moving dependent column source, that invalidated child
 * interaction is cancelled without a callback.
 * Programmatic [DatePickerState.selectDate] calls update [state] directly and do not invoke this
 * callback. When app code changes the picker from a button, preset, or external value, update any
 * app-owned state in the same handler.
 * @param enabled Whether user scroll, click, and semantics selection actions are enabled.
 * @param items Selectable year, month, and day item lists for the picker.
 * @param format Visible item text and optional accessibility value descriptions for each picker column.
 * @param style Visual and layout styling for each picker column. Per-column divider settings do not
 * apply here; use [selectionIndicator] for the shared selection band instead.
 * @param selectionIndicator The single selection band drawn across the whole picker. Defaults to a
 * band derived from [style].
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
    selectionIndicator: PickerSelectionIndicator = PickerDefaults.selectionIndicator(style),
    layout: DatePickerLayout = PickerDefaults.datePickerLayout(),
    spacingBetweenPickers: Dp = PickerDefaults.SpacingBetweenPickers,
    semantics: DatePickerSemantics = PickerDefaults.datePickerSemantics()
) {
    val columnStyle = remember(style) { style.copy(isDividerVisible = false) }

    remember(items) {
        validateDatePickerItemConfiguration(items = items)
    }
    remember(items, state, state.selectedYear, state.selectedMonth, state.selectedDay) {
        validateDatePickerSelection(state = state, items = items)
    }

    fun commitColumnChange(column: DatePickerColumn, value: Int) {
        val currentDate = state.selectedDate
        val nextDate = items.repairedDateAfter(
            currentDate = currentDate,
            column = column,
            value = value
        )
        commitMultiWheelSelection(
            currentState = currentDate,
            nextState = nextDate,
            commitState = state::selectDate,
            onSelectionCommitted = onSelectedDateChange
        )
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
            val yearItemHeight = rememberPickerItemHeight(
                items = yearItems,
                format = format.year,
                style = columnStyle
            )
            val monthItemHeight = rememberPickerItemHeight(
                items = monthItems,
                format = format.month,
                style = columnStyle
            )
            val dayItemHeight = rememberPickerItemHeight(
                items = dayItems,
                format = format.day,
                style = columnStyle
            )

            PickerSelectionBand(
                indicator = selectionIndicator,
                itemHeight = maxPickerItemHeight(yearItemHeight, monthItemHeight, dayItemHeight),
                enabled = enabled,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        spacingBetweenPickers,
                        Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    layout.columnOrder.forEach { column ->
                        key(column, state, items) {
                            when (column) {
                                DatePickerColumn.YEAR -> {
                                    Picker(
                                        items = yearItems,
                                        selectedItem = state.selectedYear,
                                        onSelectedItemChange = { year ->
                                            commitColumnChange(DatePickerColumn.YEAR, year)
                                        },
                                        modifier = pickerColumnModifier(pickerModifier, layout.yearWeight),
                                        enabled = enabled,
                                        style = columnStyle,
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
                                            commitColumnChange(DatePickerColumn.MONTH, month)
                                        },
                                        modifier = pickerColumnModifier(pickerModifier, layout.monthWeight),
                                        enabled = enabled,
                                        style = columnStyle,
                                        semantics = semantics.month,
                                        format = format.month
                                    )
                                }

                                DatePickerColumn.DAY -> {
                                    Picker(
                                        items = dayItems,
                                        selectedItem = state.selectedDay,
                                        onSelectedItemChange = { day ->
                                            commitColumnChange(DatePickerColumn.DAY, day)
                                        },
                                        modifier = pickerColumnModifier(pickerModifier, layout.dayWeight),
                                        enabled = enabled,
                                        style = columnStyle,
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
}

internal fun validateDatePickerItems(
    state: DatePickerState,
    items: DatePickerItems
) {
    validateDatePickerItemConfiguration(items = items)
    validateDatePickerSelection(state = state, items = items)
}

private fun validateDatePickerItemConfiguration(items: DatePickerItems) {
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
    if (items.constraints.isUnbounded) {
        val minimumMaxDay = minimumMaxDayFor(yearItems, monthItems)
        require(dayItems.any { it <= minimumMaxDay }) {
            "DatePicker dayItems must contain at least one day valid for every selectable " +
                    "year/month combination. Smallest maximum day is $minimumMaxDay."
        }
    }
}

private fun validateDatePickerSelection(state: DatePickerState, items: DatePickerItems) {
    require(state.selectedYear in items.yearItems) {
        "DatePicker yearItems must contain state.selectedYear=${state.selectedYear}. " +
                datePickerStateItemsAdvice()
    }
    require(state.selectedMonth in items.monthItems) {
        "DatePicker monthItems must contain state.selectedMonth=${state.selectedMonth}. " +
                datePickerStateItemsAdvice()
    }
    val availableMonthItems = items.selectableMonthItemsFor(state.selectedYear)
    require(availableMonthItems.isNotEmpty()) {
        "DatePicker constraints must allow state.selectedYear=${state.selectedYear}. " +
                datePickerStateItemsAdvice()
    }
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
