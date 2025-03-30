package com.kez.picker.date

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kez.picker.PickerState
import com.kez.picker.rememberPickerState
import com.kez.picker.util.MONTH_RANGE
import com.kez.picker.util.YEAR_RANGE
import com.kez.picker.util.currentDate
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

/**
 * A Composable that allows users to select a date range with year and month pickers
 */
@Composable
fun DateRangePicker(
    modifier: Modifier = Modifier,
    startDateYearPickerState: PickerState<Int> = rememberPickerState(currentDate.year),
    startDateMonthPickerState: PickerState<Int> = rememberPickerState(currentDate.monthNumber),
    endDateYearPickerState: PickerState<Int> = rememberPickerState(currentDate.year),
    endDateMonthPickerState: PickerState<Int> = rememberPickerState(currentDate.monthNumber),
    initialStartDate: LocalDate = currentDate,
    initialEndDate: LocalDate = currentDate.plus(DatePeriod(months = 1)),
    yearItems: List<Int> = YEAR_RANGE,
    monthItems: List<Int> = MONTH_RANGE,
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
    dividerThickness: Dp = 1.dp,
    dividerShape: Shape = RoundedCornerShape(10.dp),
    spacingBetweenPickers: Dp = 20.dp,
    spacingBetweenSections: Dp = 16.dp,
    pickerWidth: Dp = 80.dp,
    startDateLabel: String = "Start Date",
    endDateLabel: String = "End Date"
) {
    Surface(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacingBetweenSections),
            modifier = Modifier.padding(16.dp)
        ) {
            Column {
                Text(
                    text = startDateLabel,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                YearMonthPicker(
                    modifier = Modifier.fillMaxWidth(),
                    yearPickerState = startDateYearPickerState,
                    monthPickerState = startDateMonthPickerState,
                    startLocalDate = initialStartDate,
                    yearItems = yearItems,
                    monthItems = monthItems,
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
            }

            // Divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.LightGray.copy(alpha = 0.5f))
            )

            Column {
                Text(
                    text = endDateLabel,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                YearMonthPicker(
                    modifier = Modifier.fillMaxWidth(),
                    yearPickerState = endDateYearPickerState,
                    monthPickerState = endDateMonthPickerState,
                    startLocalDate = initialEndDate,
                    yearItems = yearItems,
                    monthItems = monthItems,
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
            }
        }
    }
}