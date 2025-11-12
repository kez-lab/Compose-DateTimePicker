package com.kez.picker.time

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kez.picker.Picker
import com.kez.picker.PickerState
import com.kez.picker.util.HOUR12_RANGE
import com.kez.picker.util.HOUR24_RANGE
import com.kez.picker.util.MINUTE_RANGE
import com.kez.picker.util.TimeFormat
import com.kez.picker.util.TimePeriod
import com.kez.picker.util.currentDateTime
import com.kez.picker.util.currentHour
import com.kez.picker.util.currentMinute
import kotlinx.datetime.LocalDateTime

/**
 * A time picker component that allows the user to select hours, minutes, and—when using the 12-hour format—the AM/PM period.
 *
 * @param modifier The modifier to be applied to the component.
 * @param pickerModifier The modifier to be applied to each picker.
 * @param minutePickerState The state for the minute picker.
 * @param hourPickerState The state for the hour picker.
 * @param periodPickerState The state for the AM/PM period picker.
 * @param timeFormat The time format (12-hour or 24-hour).
 * @param startTime The initial time to display.
 * @param minuteItems The list of minute values to display.
 * @param hourItems The list of hour values to display.
 * @param periodItems The list of period values to display.
 * @param visibleItemsCount The number of items visible at once.
 * @param itemPadding The padding around each item.
 * @param textStyle The style of the text for unselected items.
 * @param selectedTextStyle The style of the text for the selected item.
 * @param dividerColor The color of the dividers.
 * @param selectedItemBackgroundColor The background color of the selected item area.
 * @param selectedItemBackgroundShape The shape of the selected item background.
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
    minutePickerState: PickerState<Int> = remember { PickerState(currentMinute) },
    hourPickerState: PickerState<Int> = remember { PickerState(currentHour) },
    periodPickerState: PickerState<TimePeriod> = remember { PickerState(TimePeriod.AM) },
    timeFormat: TimeFormat = TimeFormat.HOUR_24,
    startTime: LocalDateTime = currentDateTime,
    minuteItems: List<Int> = MINUTE_RANGE,
    hourItems: List<Int> = when (timeFormat) {
        TimeFormat.HOUR_12 -> HOUR12_RANGE
        TimeFormat.HOUR_24 -> HOUR24_RANGE
    },
    periodItems: List<TimePeriod> = TimePeriod.entries,
    visibleItemsCount: Int = 3,
    itemPadding: PaddingValues = PaddingValues(8.dp),
    textStyle: TextStyle = TextStyle(fontSize = 16.sp),
    selectedTextStyle: TextStyle = TextStyle(fontSize = 22.sp),
    dividerColor: Color = LocalContentColor.current,
    selectedItemBackgroundColor: Color = Color.Transparent,
    selectedItemBackgroundShape: Shape = RoundedCornerShape(12.dp),
    fadingEdgeGradient: Brush = Brush.verticalGradient(
        0f to Color.Transparent,
        0.5f to Color.Black,
        1f to Color.Transparent
    ),
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    dividerThickness: Dp = 1.dp,
    dividerShape: Shape = RoundedCornerShape(10.dp),
    spacingBetweenPickers: Dp = 20.dp,
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
                val startHour = when (timeFormat) {
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
                if (timeFormat == TimeFormat.HOUR_12) {
                    Picker(
                        state = periodPickerState,
                        items = periodItems,
                        visibleItemsCount = visibleItemsCount,
                        modifier = pickerModifier.weight(1f),
                        textStyle = textStyle,
                        selectedTextStyle = selectedTextStyle,
                        dividerColor = dividerColor,
                        selectedItemBackgroundColor = selectedItemBackgroundColor,
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
                    state = hourPickerState,
                    modifier = pickerModifier.weight(1f),
                    items = hourItems,
                    startIndex = hourStartIndex,
                    visibleItemsCount = visibleItemsCount,
                    textStyle = textStyle,
                    selectedTextStyle = selectedTextStyle,
                    dividerColor = dividerColor,
                    selectedItemBackgroundColor = selectedItemBackgroundColor,
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
                    state = minutePickerState,
                    items = minuteItems,
                    startIndex = minuteStartIndex,
                    visibleItemsCount = visibleItemsCount,
                    modifier = pickerModifier.weight(1f),
                    textStyle = textStyle,
                    selectedTextStyle = selectedTextStyle,
                    dividerColor = dividerColor,
                    selectedItemBackgroundColor = selectedItemBackgroundColor,
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
        timeFormat = TimeFormat.HOUR_24
    )
}

@Preview(name = "12-Hour Format with AM/PM", group = "TimePicker - Formats", showBackground = true)
@Composable
fun TimePickerPreview12Hour() {
    TimePicker(
        timeFormat = TimeFormat.HOUR_12
    )
}

@Preview(name = "No Divider", group = "TimePicker - Styles", showBackground = true)
@Composable
fun TimePickerNoDividerPreview() {
    TimePicker(
        timeFormat = TimeFormat.HOUR_24,
        isDividerVisible = false
    )
}

@Preview(name = "Custom Colors", group = "TimePicker - Styles", showBackground = true)
@Composable
fun TimePickerCustomColorsPreview() {
    TimePicker(
        timeFormat = TimeFormat.HOUR_12,
        textStyle = TextStyle(fontSize = 16.sp, color = Color.Gray),
        selectedTextStyle = TextStyle(fontSize = 22.sp, color = Color(0xFF6200EE)),
        dividerColor = Color(0xFF6200EE)
    )
}

@Preview(name = "Large Text Size", group = "TimePicker - Styles", showBackground = true)
@Composable
fun TimePickerLargeTextPreview() {
    TimePicker(
        timeFormat = TimeFormat.HOUR_24,
        textStyle = TextStyle(fontSize = 20.sp),
        selectedTextStyle = TextStyle(fontSize = 28.sp),
        visibleItemsCount = 5
    )
}