package com.kez.picker.date

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.kez.picker.Picker
import com.kez.picker.PickerDefaults
import com.kez.picker.PickerStyle
import com.kez.picker.YearMonthPickerAccessibility
import com.kez.picker.YearMonthPickerDisplay
import com.kez.picker.YearMonthPickerItems

/**
 * A year and month picker component.
 *
 * @param modifier The modifier to be applied to the component.
 * @param pickerModifier The modifier to be applied to each picker.
 * @param state The state object to control the picker.
 * @param onSelectedYearMonthChange Called after user interaction changes the selected year/month.
 * @param items Selectable year and month item lists for the picker.
 * @param display Visible item text formatters for each picker column.
 * @param style Visual and layout styling for each picker column.
 * @param spacingBetweenPickers The spacing between the pickers.
 * @param accessibility Accessibility labels, item descriptions, and custom action labels for each picker column.
 * @throws IllegalArgumentException if custom item lists are empty, contain duplicates, contain values outside the supported ranges, or omit the current selected year/month.
 */
@Composable
fun YearMonthPicker(
    modifier: Modifier = Modifier,
    pickerModifier: Modifier = Modifier,
    state: YearMonthPickerState = rememberYearMonthPickerState(),
    onSelectedYearMonthChange: (YearMonth) -> Unit = {},
    items: YearMonthPickerItems = PickerDefaults.yearMonthPickerItems(),
    display: YearMonthPickerDisplay = PickerDefaults.yearMonthPickerDisplay(),
    style: PickerStyle = PickerDefaults.style(),
    spacingBetweenPickers: Dp = PickerDefaults.SpacingBetweenPickers,
    accessibility: YearMonthPickerAccessibility = PickerDefaults.yearMonthPickerAccessibility()
) {
    validateYearMonthPickerItems(
        state = state,
        items = items
    )

    fun updateSelectedYearMonth(update: () -> Unit) {
        val previousYearMonth = state.selectedYearMonth
        update()
        val nextYearMonth = state.selectedYearMonth
        if (nextYearMonth != previousYearMonth) {
            onSelectedYearMonthChange(nextYearMonth)
        }
    }

    Box(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    spacingBetweenPickers,
                    Alignment.CenterHorizontally
                ),
            ) {
                Picker(
                    items = items.yearItems,
                    selectedItem = state.selectedYear,
                    onSelectedItemChange = { year ->
                        updateSelectedYearMonth { state.selectYear(year) }
                    },
                    modifier = pickerModifier.weight(1f),
                    style = style,
                    accessibility = accessibility.year,
                    itemText = display.year.itemText
                )
                Picker(
                    items = items.monthItems,
                    selectedItem = state.selectedMonth,
                    onSelectedItemChange = { month ->
                        updateSelectedYearMonth { state.selectMonth(month) }
                    },
                    modifier = pickerModifier.weight(1f),
                    style = style,
                    accessibility = accessibility.month,
                    itemText = display.month.itemText
                )
            }
        }
    }
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

internal fun validateYearMonthPickerItems(
    state: YearMonthPickerState,
    items: YearMonthPickerItems
) {
    val yearItems = items.yearItems
    val monthItems = items.monthItems

    val yearRange = 1000..9999
    val monthRange = 1..12
    val invalidYears = yearItems.invalidValuesFor(yearRange)
    val invalidMonths = monthItems.invalidValuesFor(monthRange)

    require(yearItems.isNotEmpty()) { "YearMonthPicker yearItems must not be empty." }
    require(monthItems.isNotEmpty()) { "YearMonthPicker monthItems must not be empty." }
    require(yearItems.distinct().size == yearItems.size) {
        "YearMonthPicker yearItems must not contain duplicate values."
    }
    require(monthItems.distinct().size == monthItems.size) {
        "YearMonthPicker monthItems must not contain duplicate values."
    }
    require(invalidYears.isEmpty()) {
        "YearMonthPicker yearItems must contain only values in range [1000, 9999]. " +
                "Invalid values: $invalidYears"
    }
    require(invalidMonths.isEmpty()) {
        "YearMonthPicker monthItems must contain only values in range [1, 12]. " +
                "Invalid values: $invalidMonths"
    }
    require(state.selectedYear in yearItems) {
        "YearMonthPicker yearItems must contain state.selectedYear=${state.selectedYear}."
    }
    require(state.selectedMonth in monthItems) {
        "YearMonthPicker monthItems must contain state.selectedMonth=${state.selectedMonth}."
    }
}

private fun List<Int>.invalidValuesFor(range: IntRange): List<Int> =
    filterNot { it in range }.distinct()

@Preview(name = "Default", group = "YearMonthPicker - Basic", showBackground = true)
@Composable
private fun YearMonthPickerPreview() {
    YearMonthPicker()
}

@Preview(name = "No Divider", group = "YearMonthPicker - Variations", showBackground = true)
@Composable
private fun YearMonthPickerNoDividerPreview() {
    YearMonthPicker(
        style = PickerDefaults.style(isDividerVisible = false),
    )
}

@Preview(name = "Custom Colors", group = "YearMonthPicker - Styles", showBackground = true)
@Composable
private fun YearMonthPickerCustomColorsPreview() {
    YearMonthPicker(
        style = PickerDefaults.style(
            colors = PickerDefaults.colors(
                textColor = Color.Gray,
                selectedTextColor = Color(0xFF03DAC5),
                dividerColor = Color(0xFF03DAC5)
            )
        )
    )
}

@Preview(name = "Large Text", group = "YearMonthPicker - Styles", showBackground = true)
@Composable
private fun YearMonthPickerLargeTextPreview() {
    YearMonthPicker(
        style = PickerDefaults.style(
            textStyles = PickerDefaults.textStyles(
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 18.sp),
                selectedTextStyle = androidx.compose.ui.text.TextStyle(fontSize = 28.sp)
            ),
            visibleItemsCount = 5
        )
    )
}
