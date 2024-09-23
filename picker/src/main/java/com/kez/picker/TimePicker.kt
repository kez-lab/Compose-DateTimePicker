package com.kez.picker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kez.picker.util.HOUR12_RANGE
import com.kez.picker.util.HOUR24_RANGE
import com.kez.picker.util.MINUTE_RANGE
import com.kez.picker.util.TimeFormat
import com.kez.picker.util.currentDateTime
import kotlinx.datetime.LocalDateTime

@Composable
fun TimePicker(
    modifier: Modifier = Modifier,
    minutePickerState: PickerState = rememberPickerState(),
    hourPickerState: PickerState = rememberPickerState(),
    periodPickerState: PickerState = rememberPickerState(),
    timeFormat: TimeFormat = TimeFormat.HOUR_24,
    startTime: LocalDateTime = currentDateTime,
    minuteItems: List<String> = MINUTE_RANGE,
    hourItems: List<String> = when (timeFormat) {
        TimeFormat.HOUR_12 -> HOUR12_RANGE
        TimeFormat.HOUR_24 -> HOUR24_RANGE
    },
    periodItems: List<String> = listOf("AM", "PM"),
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
    pickerWidth: Dp = 100.dp
) {
    Surface(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {

            val minuteStartIndex = remember {
                minuteItems.indexOf(startTime.minute.toString())
            }

            val hourStartIndex = remember {
                val startHour = when (timeFormat) {
                    TimeFormat.HOUR_12 -> {
                        val hour = startTime.hour % 12
                        if (hour == 0) 12 else hour
                    }

                    TimeFormat.HOUR_24 -> startTime.hour
                }
                hourItems.indexOf(startHour.toString())
            }

            val periodStartIndex = remember {
                when (timeFormat) {
                    TimeFormat.HOUR_24 -> 0
                    TimeFormat.HOUR_12 -> 1
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Picker(
                    state = periodPickerState,
                    items = periodItems,
                    visibleItemsCount = visibleItemsCount,
                    modifier = Modifier.width(pickerWidth),
                    textStyle = textStyle,
                    selectedTextStyle = selectedTextStyle,
                    textModifier = Modifier.padding(itemPadding),
                    dividerColor = dividerColor,
                    itemPadding = itemPadding,
                    startIndex = periodStartIndex,
                    fadingEdgeGradient = fadingEdgeGradient,
                    horizontalAlignment = horizontalAlignment,
                    itemTextAlignment = verticalAlignment,
                    dividerThickness = dividerThickness,
                    dividerShape = dividerShape,
                    isInfinity = false,
                )

                Spacer(modifier = Modifier.width(spacingBetweenPickers))

                Box(contentAlignment = Alignment.Center) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Picker(
                            state = hourPickerState,
                            modifier = Modifier.width(pickerWidth),
                            items = hourItems,
                            startIndex = hourStartIndex,
                            visibleItemsCount = visibleItemsCount,
                            textModifier = Modifier.padding(itemPadding),
                            textStyle = textStyle,
                            selectedTextStyle = selectedTextStyle,
                            dividerColor = dividerColor,
                            itemPadding = itemPadding,
                            fadingEdgeGradient = fadingEdgeGradient,
                            horizontalAlignment = horizontalAlignment,
                            itemTextAlignment = verticalAlignment,
                            dividerThickness = dividerThickness,
                            dividerShape = dividerShape
                        )

                        Spacer(modifier = Modifier.width(spacingBetweenPickers))

                        Picker(
                            state = minutePickerState,
                            items = minuteItems,
                            startIndex = minuteStartIndex,
                            visibleItemsCount = visibleItemsCount,
                            modifier = Modifier.width(pickerWidth),
                            textStyle = textStyle,
                            selectedTextStyle = selectedTextStyle,
                            textModifier = Modifier.padding(itemPadding),
                            dividerColor = dividerColor,
                            itemPadding = itemPadding,
                            fadingEdgeGradient = fadingEdgeGradient,
                            horizontalAlignment = horizontalAlignment,
                            itemTextAlignment = verticalAlignment,
                            dividerThickness = dividerThickness,
                            dividerShape = dividerShape,
                        )
                    }
                }
            }
        }
    }
}