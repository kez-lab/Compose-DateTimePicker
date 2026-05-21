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
import com.kez.picker.util.MONTH_RANGE
import com.kez.picker.util.YEAR_RANGE

/**
 * A year and month picker component.
 *
 * @param modifier The modifier to be applied to the component.
 * @param pickerModifier The modifier to be applied to each picker.
 * @param state The state object to control the picker.
 * @param yearItems The list of year values to display. Must be non-empty, distinct, contain values in 1000..9999, and contain [YearMonthPickerState.selectedYear].
 * @param monthItems The list of month values to display. Must be non-empty, distinct, contain values in 1..12, and contain [YearMonthPickerState.selectedMonth].
 * @param style Visual and layout styling for each picker column.
 * @param spacingBetweenPickers The spacing between the pickers.
 * @param yearPickerLabel Accessibility label for the year picker. Pass null to omit the picker label prefix.
 * @param monthPickerLabel Accessibility label for the month picker. Pass null to omit the picker label prefix.
 * @param yearItemContentDescription Accessibility description for each year value.
 * @param monthItemContentDescription Accessibility description for each month value.
 * @param previousItemActionLabel Accessibility action label used by child pickers to select the previous item. Pass null or blank to omit the action.
 * @param nextItemActionLabel Accessibility action label used by child pickers to select the next item. Pass null or blank to omit the action.
 * @throws IllegalArgumentException if custom item lists are empty, contain duplicates, contain values outside the supported ranges, or omit the current selected year/month.
 */
@Composable
fun YearMonthPicker(
    modifier: Modifier = Modifier,
    pickerModifier: Modifier = Modifier,
    state: YearMonthPickerState = rememberYearMonthPickerState(),
    yearItems: List<Int> = YEAR_RANGE,
    monthItems: List<Int> = MONTH_RANGE,
    style: PickerStyle = PickerDefaults.style(),
    spacingBetweenPickers: Dp = PickerDefaults.SpacingBetweenPickers,
    yearPickerLabel: String? = "Year",
    monthPickerLabel: String? = "Month",
    yearItemContentDescription: (Int) -> String = { it.toString() },
    monthItemContentDescription: (Int) -> String = { it.toString() },
    previousItemActionLabel: String? = PickerDefaults.PreviousItemActionLabel,
    nextItemActionLabel: String? = PickerDefaults.NextItemActionLabel
) {
    validateYearMonthPickerItems(
        state = state,
        yearItems = yearItems,
        monthItems = monthItems
    )

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
                    items = yearItems,
                    selectedItem = state.selectedYear,
                    onSelectedItemChange = state::selectYear,
                    modifier = pickerModifier.weight(1f),
                    style = style,
                    pickerLabel = yearPickerLabel,
                    itemContentDescription = yearItemContentDescription,
                    previousItemActionLabel = previousItemActionLabel,
                    nextItemActionLabel = nextItemActionLabel
                )
                Picker(
                    items = monthItems,
                    selectedItem = state.selectedMonth,
                    onSelectedItemChange = state::selectMonth,
                    modifier = pickerModifier.weight(1f),
                    style = style,
                    pickerLabel = monthPickerLabel,
                    itemContentDescription = monthItemContentDescription,
                    previousItemActionLabel = previousItemActionLabel,
                    nextItemActionLabel = nextItemActionLabel
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
