package com.kez.picker.date

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.kez.picker.Picker
import com.kez.picker.PickerColors
import com.kez.picker.PickerDefaults
import com.kez.picker.PickerTextStyles
import com.kez.picker.YearMonthPickerState
import com.kez.picker.rememberYearMonthPickerState
import com.kez.picker.util.MONTH_RANGE
import com.kez.picker.util.YEAR_RANGE
import com.kez.picker.util.currentDate
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

/**
 * A year and month picker component.
 *
 * @param modifier The modifier to be applied to the component.
 * @param pickerModifier The modifier to be applied to each picker.
 * @param state The state object to control the picker.
 * @param startLocalDate Legacy compatibility parameter. It does not initialize or update [state],
 * even when [state] is omitted; prefer [rememberYearMonthPickerState] with initial values.
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
fun YearMonthPicker(
    modifier: Modifier = Modifier,
    pickerModifier: Modifier = Modifier,
    state: YearMonthPickerState = rememberYearMonthPickerState(),
    @Suppress("UNUSED_PARAMETER")
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

            val yearStartIndex = remember(yearItems) {
                yearItems.startIndexOf(state.selectedYear)
            }
            val monthStartIndex = remember(monthItems) {
                monthItems.startIndexOf(state.selectedMonth)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    spacingBetweenPickers,
                    Alignment.CenterHorizontally
                ),
            ) {
                Picker(
                    state = state.yearState,
                    modifier = pickerModifier.weight(1f),
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
                    modifier = pickerModifier.weight(1f),
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
            }
        }
    }
}

private fun <T> List<T>.startIndexOf(item: T): Int =
    indexOf(item).takeIf { it >= 0 } ?: 0

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
    require(invalidYears.isEmpty()) {
        "YearMonthPicker yearItems must contain only values in range [1000, 9999]. " +
                "Invalid values: $invalidYears"
    }
    require(invalidMonths.isEmpty()) {
        "YearMonthPicker monthItems must contain only values in range [1, 12]. " +
                "Invalid values: $invalidMonths"
    }
}

private fun List<Int>.invalidValuesFor(range: IntRange): List<Int> =
    filterNot { it in range }.distinct()

@Preview(name = "Default", group = "YearMonthPicker - Basic", showBackground = true)
@Composable
fun YearMonthPickerPreview() {
    YearMonthPicker()
}

@Preview(name = "No Divider", group = "YearMonthPicker - Variations", showBackground = true)
@Composable
fun YearMonthPickerNoDividerPreview() {
    YearMonthPicker(
        isDividerVisible = false,
    )
}

@Preview(name = "Custom Colors", group = "YearMonthPicker - Styles", showBackground = true)
@Composable
fun YearMonthPickerCustomColorsPreview() {
    YearMonthPicker(
        colors = PickerDefaults.colors(
            textColor = Color.Gray,
            selectedTextColor = Color(0xFF03DAC5),
            dividerColor = Color(0xFF03DAC5)
        )
    )
}

@Preview(name = "Large Text", group = "YearMonthPicker - Styles", showBackground = true)
@Composable
fun YearMonthPickerLargeTextPreview() {
    YearMonthPicker(
        textStyles = PickerDefaults.textStyles(
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 18.sp),
            selectedTextStyle = androidx.compose.ui.text.TextStyle(fontSize = 28.sp)
        ),
        visibleItemsCount = 5
    )
}
