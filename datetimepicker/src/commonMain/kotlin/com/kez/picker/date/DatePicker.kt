package com.kez.picker.date

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.kez.picker.DatePickerState
import com.kez.picker.Picker
import com.kez.picker.PickerColors
import com.kez.picker.PickerDefaults
import com.kez.picker.PickerTextStyles
import com.kez.picker.rememberDatePickerState
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
 * @param startLocalDate The initial date to display (relevant for initial index calculation if needed, though state handles values).
 * @param yearItems The list of year values to display.
 * @param monthItems The list of month values to display.
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

            // Calculate initial indices based on startLocalDate logic if strictly needed,
            // but usually we rely on the state's initial value. 
            // However, Picker component uses `startIndex`. 
            // We should sync them with state's initial values or just find index of state's current value.
            
            // To ensure 1:1 mapping with Picker's internal state on first render:
            val yearStartIndex = remember { yearItems.indexOf(state.selectedYear) }
            val monthStartIndex = remember { monthItems.indexOf(state.selectedMonth) }
            // Day items change dynamically, so we can't fully pre-calculate a static list and index 
            // without being careful.
            
            // Dynamic day items based on maxDay
            val maxDay = state.maxDay
            val dayItems = (1..maxDay).toList()
            // Ensure selected day index is valid for the current dayItems
            val dayStartIndex = remember(dayItems) { 
                val index = dayItems.indexOf(state.selectedDay)
                if (index >= 0) index else 0
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Year Picker
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

                // Month Picker
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

                // Day Picker
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

@Preview(name = "Default", group = "DatePicker", showBackground = true)
@Composable
fun DatePickerPreview() {
    DatePicker()
}
