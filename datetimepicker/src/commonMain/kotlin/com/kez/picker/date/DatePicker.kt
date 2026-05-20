package com.kez.picker.date

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.kez.picker.Picker
import com.kez.picker.PickerColors
import com.kez.picker.PickerDefaults
import com.kez.picker.PickerTextStyles
import com.kez.picker.util.MONTH_RANGE
import com.kez.picker.util.YEAR_RANGE
import com.kez.picker.util.currentDate
import kotlinx.datetime.LocalDate

/**
 * A date picker component that allows selecting year, month, and day.
 *
 * @param modifier The modifier to be applied to the component.
 * @param pickerModifier The modifier to be applied to each picker.
 * @param state The state object to control the picker.
 * @param startLocalDate Legacy initial date parameter. Prefer setting initial values in [state].
 * @param yearItems The list of year values to display. Must contain values in 1000..9999.
 * @param monthItems The list of month values to display. Must contain values in 1..12.
 * @param visibleItemsCount The number of items visible at once.
 * @param colors The colors used by the picker. See [PickerDefaults.colors].
 * @param textStyles The text styles used by the picker. See [PickerDefaults.textStyles].
 * @param selectedItemBackgroundShape The shape of the selected item background.
 * @param itemPadding The padding around each item.
 * @param fadingEdgeGradient The gradient to use for fading edges.
 * @param horizontalAlignment The horizontal alignment of items.
 * @param verticalAlignment The vertical alignment of the text within items.
 * @param dividerThickness The thickness of the dividers.
 * @param dividerShape The shape of the dividers.
 * @param spacingBetweenPickers The spacing between the pickers.
 * @param isDividerVisible Whether the divider should be visible.
 * @throws IllegalArgumentException if custom item lists are empty or contain values outside the supported ranges.
 */
@Composable
fun DatePicker(
    modifier: Modifier = Modifier,
    pickerModifier: Modifier = Modifier,
    state: DatePickerState = rememberDatePickerState(),
    startLocalDate: LocalDate = currentDate(),
    yearItems: List<Int> = YEAR_RANGE,
    monthItems: List<Int> = MONTH_RANGE,
    visibleItemsCount: Int = PickerDefaults.VisibleItemsCount,
    colors: PickerColors = PickerDefaults.colors(),
    textStyles: PickerTextStyles = PickerDefaults.textStyles(),
    selectedItemBackgroundShape: Shape = PickerDefaults.SelectedItemBackgroundShape,
    itemPadding: PaddingValues = PickerDefaults.ItemPadding,
    fadingEdgeGradient: Brush = PickerDefaults.fadingEdgeGradient(),
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    dividerThickness: Dp = PickerDefaults.DividerThickness,
    dividerShape: Shape = PickerDefaults.DividerShape,
    spacingBetweenPickers: Dp = PickerDefaults.SpacingBetweenPickers,
    isDividerVisible: Boolean = true
) {
    validateDatePickerItems(
        state = state,
        yearItems = yearItems,
        monthItems = monthItems
    )

    // Validate state whenever year or month changes to ensure day is within range
    LaunchedEffect(state.selectedYear, state.selectedMonth) {
        state.validate()
    }

    Box(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            val yearStartIndex = remember(yearItems) { yearItems.startIndexOf(state.selectedYear) }
            val monthStartIndex = remember(monthItems) { monthItems.startIndexOf(state.selectedMonth) }

            val maxDay = state.maxDay
            val dayItems = (1..maxDay).toList()
            val dayStartIndex = remember(dayItems, state.selectedDay) {
                val index = dayItems.indexOf(state.selectedDay.coerceIn(1, maxDay))
                if (index >= 0) index else 0
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Picker(
                    state = state.yearState,
                    modifier = pickerModifier.weight(1.2f), // Give Year slightly more width
                    items = yearItems,
                    startIndex = yearStartIndex,
                    visibleItemsCount = visibleItemsCount,
                    colors = colors,
                    textStyles = textStyles,
                    selectedItemBackgroundShape = selectedItemBackgroundShape,
                    itemPadding = itemPadding,
                    fadingEdgeGradient = fadingEdgeGradient,
                    horizontalAlignment = horizontalAlignment,
                    verticalAlignment = verticalAlignment,
                    dividerThickness = dividerThickness,
                    dividerShape = dividerShape,
                    isDividerVisible = isDividerVisible,
                    pickerLabel = "Year"
                )

                Picker(
                    state = state.monthState,
                    items = monthItems,
                    startIndex = monthStartIndex,
                    visibleItemsCount = visibleItemsCount,
                    modifier = pickerModifier.weight(0.8f),
                    colors = colors,
                    textStyles = textStyles,
                    selectedItemBackgroundShape = selectedItemBackgroundShape,
                    itemPadding = itemPadding,
                    fadingEdgeGradient = fadingEdgeGradient,
                    horizontalAlignment = horizontalAlignment,
                    verticalAlignment = verticalAlignment,
                    dividerThickness = dividerThickness,
                    dividerShape = dividerShape,
                    isDividerVisible = isDividerVisible,
                    pickerLabel = "Month"
                )

                key(maxDay) {
                    Picker(
                        state = state.dayState,
                        items = dayItems,
                        startIndex = dayStartIndex,
                        visibleItemsCount = visibleItemsCount,
                        modifier = pickerModifier.weight(0.8f),
                        colors = colors,
                        textStyles = textStyles,
                        selectedItemBackgroundShape = selectedItemBackgroundShape,
                        itemPadding = itemPadding,
                        isInfinity = false,
                        fadingEdgeGradient = fadingEdgeGradient,
                        horizontalAlignment = horizontalAlignment,
                        verticalAlignment = verticalAlignment,
                        dividerThickness = dividerThickness,
                        dividerShape = dividerShape,
                        isDividerVisible = isDividerVisible,
                        pickerLabel = "Day"
                    )
                }
            }
        }
    }
}

private fun <T> List<T>.startIndexOf(item: T): Int =
    indexOf(item).takeIf { it >= 0 } ?: 0

internal fun validateDatePickerItems(
    state: DatePickerState,
    yearItems: List<Int>,
    monthItems: List<Int>
) {
    val yearRange = 1000..9999
    val monthRange = 1..12
    val invalidYears = yearItems.invalidValuesFor(yearRange)
    val invalidMonths = monthItems.invalidValuesFor(monthRange)

    require(yearItems.isNotEmpty()) { "DatePicker yearItems must not be empty." }
    require(monthItems.isNotEmpty()) { "DatePicker monthItems must not be empty." }
    require(invalidYears.isEmpty()) {
        "DatePicker yearItems must contain only values in range [1000, 9999]. " +
                "Invalid values: $invalidYears"
    }
    require(invalidMonths.isEmpty()) {
        "DatePicker monthItems must contain only values in range [1, 12]. " +
                "Invalid values: $invalidMonths"
    }
}

private fun List<Int>.invalidValuesFor(range: IntRange): List<Int> =
    filterNot { it in range }.distinct()

@Preview(name = "Default", group = "DatePicker", showBackground = true)
@Composable
fun DatePickerPreview() {
    DatePicker()
}
