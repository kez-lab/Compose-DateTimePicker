package com.kez.picker.date

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kez.picker.PickerState
import com.kez.picker.rememberPickerState
import com.kez.picker.util.MONTH_RANGE
import com.kez.picker.util.YEAR_RANGE
import com.kez.picker.util.currentDate
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

@Composable
fun DateRangePickerDialog(
    modifier: Modifier = Modifier,
    properties: DialogProperties = DialogProperties(),
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
    endDateLabel: String = "End Date",
    titleText: String = "Date Range",
    onDismissRequest: () -> Unit,
    onDoneClickListener: (startDate: LocalDate, endDate: LocalDate) -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties
    ) {
        Surface(
            modifier = modifier.clip(
                shape = RoundedCornerShape(10.dp)
            )
        ) {
            Column {
                Text(
                    text = titleText,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 16.dp),
                    textAlign = TextAlign.Start
                )

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                DateRangePicker(
                    modifier = Modifier.wrapContentSize(),
                    startDateYearPickerState = startDateYearPickerState,
                    startDateMonthPickerState = startDateMonthPickerState,
                    endDateYearPickerState = endDateYearPickerState,
                    endDateMonthPickerState = endDateMonthPickerState,
                    initialStartDate = initialStartDate,
                    initialEndDate = initialEndDate,
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
                    spacingBetweenSections = spacingBetweenSections,
                    pickerWidth = pickerWidth,
                    startDateLabel = startDateLabel,
                    endDateLabel = endDateLabel
                )

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
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
                            val startYear = startDateYearPickerState.selectedItem
                            val startMonth = startDateMonthPickerState.selectedItem
                            val endYear = endDateYearPickerState.selectedItem
                            val endMonth = endDateMonthPickerState.selectedItem

                            val startDate = LocalDate(
                                year = startYear,
                                monthNumber = startMonth,
                                dayOfMonth = initialStartDate.dayOfMonth
                            )

                            val endDate = LocalDate(
                                year = endYear,
                                monthNumber = endMonth,
                                dayOfMonth = initialEndDate.dayOfMonth
                            )

                            onDoneClickListener(startDate, endDate)
                        },
                    ) {
                        Text("Done")
                    }
                }
            }
        }
    }
}