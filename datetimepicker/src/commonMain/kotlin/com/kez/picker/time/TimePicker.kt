package com.kez.picker.time

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
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
import com.kez.picker.TimePickerState
import com.kez.picker.rememberTimePickerState
import com.kez.picker.util.HOUR12_RANGE
import com.kez.picker.util.HOUR24_RANGE
import com.kez.picker.util.MINUTE_RANGE
import com.kez.picker.util.TimeFormat
import com.kez.picker.util.TimePeriod
import com.kez.picker.util.currentDateTime
import kotlinx.datetime.LocalDateTime

/**
 * A time picker component that allows the user to select hours, minutes, and—when using the 12-hour format—the AM/PM period.
 *
 * @param modifier The modifier to be applied to the component.
 * @param pickerModifier The modifier to be applied to each picker.
 * @param state The state object to control the picker.
 * @param startTime The initial time to display.
 * @param minuteItems The list of minute values to display.
 * @param hourItems The list of hour values to display.
 * @param periodItems The list of period values to display.
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
fun TimePicker(
    modifier: Modifier = Modifier,
    pickerModifier: Modifier = Modifier,
    state: TimePickerState = rememberTimePickerState(),
    startTime: LocalDateTime = currentDateTime(),
    minuteItems: List<Int> = MINUTE_RANGE,
    hourItems: List<Int> = when (state.timeFormat) {
        TimeFormat.HOUR_12 -> HOUR12_RANGE
        TimeFormat.HOUR_24 -> HOUR24_RANGE
    },
    periodItems: List<TimePeriod> = TimePeriod.entries,
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
    Box(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {

            val minuteStartIndex = remember {
                minuteItems.indexOf(startTime.minute)
            }

            val hourStartIndex = remember {
                val startHour = when (state.timeFormat) {
                    TimeFormat.HOUR_12 -> {
                        val hour = startTime.hour % 12
                        if (hour == 0) 12 else hour
                    }

                    TimeFormat.HOUR_24 -> startTime.hour
                }
                hourItems.indexOf(startHour)
            }

            val periodStartIndex = remember {
                val period = if (startTime.hour >= 12) TimePeriod.PM else TimePeriod.AM
                periodItems.indexOf(period)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (state.timeFormat == TimeFormat.HOUR_12) {
                    Picker(
                        state = state.periodState,
                        items = periodItems,
                        visibleItemsCount = visibleItemsCount,
                        modifier = pickerModifier.weight(1f),
                        colors = colors,
                        textStyles = textStyles,
                        selectedItemBackgroundShape = selectedItemBackgroundShape,
                        itemPadding = itemPadding,
                        startIndex = periodStartIndex,
                        fadingEdgeGradient = fadingEdgeGradient,
                        horizontalAlignment = horizontalAlignment,
                        itemTextAlignment = verticalAlignment,
                        dividerThickness = dividerThickness,
                        dividerShape = dividerShape,
                        isDividerVisible = isDividerVisible,
                        isInfinity = false
                    )
                    Spacer(modifier = Modifier.width(spacingBetweenPickers))
                }
                Picker(
                    state = state.hourState,
                    modifier = pickerModifier.weight(1f),
                    items = hourItems,
                    startIndex = hourStartIndex,
                    visibleItemsCount = visibleItemsCount,
                    colors = colors,
                    textStyles = textStyles,
                    selectedItemBackgroundShape = selectedItemBackgroundShape,
                    itemPadding = itemPadding,
                    fadingEdgeGradient = fadingEdgeGradient,
                    horizontalAlignment = horizontalAlignment,
                    itemTextAlignment = verticalAlignment,
                    dividerThickness = dividerThickness,
                    dividerShape = dividerShape,
                    isDividerVisible = isDividerVisible
                )
                Spacer(modifier = Modifier.width(spacingBetweenPickers))
                Picker(
                    state = state.minuteState,
                    items = minuteItems,
                    startIndex = minuteStartIndex,
                    visibleItemsCount = visibleItemsCount,
                    modifier = pickerModifier.weight(1f),
                    colors = colors,
                    textStyles = textStyles,
                    selectedItemBackgroundShape = selectedItemBackgroundShape,
                    itemPadding = itemPadding,
                    fadingEdgeGradient = fadingEdgeGradient,
                    horizontalAlignment = horizontalAlignment,
                    itemTextAlignment = verticalAlignment,
                    dividerThickness = dividerThickness,
                    dividerShape = dividerShape,
                    isDividerVisible = isDividerVisible
                )
            }
        }
    }
}

@Preview(name = "24-Hour Format", group = "TimePicker - Formats", showBackground = true)
@Composable
fun TimePickerPreview24Hour() {
    TimePicker(
        state = rememberTimePickerState(timeFormat = TimeFormat.HOUR_24)
    )
}

@Preview(name = "12-Hour Format with AM/PM", group = "TimePicker - Formats", showBackground = true)
@Composable
fun TimePickerPreview12Hour() {
    TimePicker(
        state = rememberTimePickerState(timeFormat = TimeFormat.HOUR_12)
    )
}

@Preview(name = "No Divider", group = "TimePicker - Styles", showBackground = true)
@Composable
fun TimePickerNoDividerPreview() {
    TimePicker(
        state = rememberTimePickerState(timeFormat = TimeFormat.HOUR_24),
        isDividerVisible = false
    )
}

@Preview(name = "Custom Colors", group = "TimePicker - Styles", showBackground = true)
@Composable
fun TimePickerCustomColorsPreview() {
    TimePicker(
        state = rememberTimePickerState(timeFormat = TimeFormat.HOUR_12),
        colors = PickerDefaults.colors(
            textColor = Color.Gray,
            selectedTextColor = Color(0xFF6200EE),
            dividerColor = Color(0xFF6200EE)
        )
    )
}

@Preview(name = "Large Text Size", group = "TimePicker - Styles", showBackground = true)
@Composable
fun TimePickerLargeTextPreview() {
    TimePicker(
        state = rememberTimePickerState(timeFormat = TimeFormat.HOUR_24),
        textStyles = PickerDefaults.textStyles(
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 20.sp),
            selectedTextStyle = androidx.compose.ui.text.TextStyle(fontSize = 28.sp)
        ),
        visibleItemsCount = 5
    )
}