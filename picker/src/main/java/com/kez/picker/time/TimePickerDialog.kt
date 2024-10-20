package com.kez.picker.time

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kez.picker.PickerState
import com.kez.picker.util.HOUR12_RANGE
import com.kez.picker.util.HOUR24_RANGE
import com.kez.picker.util.MINUTE_RANGE
import com.kez.picker.util.TimeFormat
import com.kez.picker.util.TimePeriod
import com.kez.picker.util.calculateTime
import com.kez.picker.util.currentDateTime
import com.kez.picker.util.currentHour
import com.kez.picker.util.currentMinute
import kotlinx.datetime.LocalDateTime

@Composable
fun TimePickerDialog(
    modifier: Modifier = Modifier,
    properties: DialogProperties,
    minutePickerState: PickerState<Int> = PickerState(currentMinute),
    hourPickerState: PickerState<Int> = PickerState(currentHour),
    periodPickerState: PickerState<TimePeriod> = PickerState(TimePeriod.AM),
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
    fadingEdgeGradient: Brush = Brush.verticalGradient(
        0f to Color.Transparent,
        0.5f to Color.Black,
        1f to Color.Transparent
    ),
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    dividerThickness: Dp = 2.dp,
    dividerShape: Shape = RoundedCornerShape(10.dp),
    spacingBetweenPickers: Dp = 20.dp,
    pickerWidth: Dp = 80.dp,
    onDismissRequest: () -> Unit,
    onDoneClickListener: (LocalDateTime) -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties
    ) {
        Surface(
            modifier = modifier
        ) {
            Column {
                TimePicker(
                    modifier = Modifier.wrapContentSize(),
                    minutePickerState = minutePickerState,
                    hourPickerState = hourPickerState,
                    periodPickerState = periodPickerState,
                    timeFormat = timeFormat,
                    startTime = startTime,
                    minuteItems = minuteItems,
                    hourItems = hourItems,
                    periodItems = periodItems,
                    visibleItemsCount = visibleItemsCount,
                    itemPadding = itemPadding,
                    textStyle = textStyle,
                    selectedTextStyle = selectedTextStyle,
                    dividerColor = dividerColor,
                    fadingEdgeGradient = fadingEdgeGradient,
                    horizontalAlignment = horizontalAlignment,
                    verticalAlignment = verticalAlignment,
                    dividerThickness = dividerThickness,
                    dividerShape = dividerShape,
                    spacingBetweenPickers = spacingBetweenPickers,
                    pickerWidth = pickerWidth
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        modifier = Modifier,
                        onClick = onDismissRequest,
                    ) {
                        Text("Cancel")
                    }

                    TextButton(
                        modifier = Modifier,
                        onClick = {
                            val hour = hourPickerState.selectedItem
                            val minute = minutePickerState.selectedItem
                            val period = periodPickerState.selectedItem

                            onDoneClickListener(
                                calculateTime(
                                    hour = hour,
                                    minute = minute,
                                    period = period,
                                    timeFormat = timeFormat,
                                )
                            )
                        },
                    ) {
                        Text("Done")
                    }
                }
            }
        }
    }
}