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
import kotlinx.datetime.LocalDate

@Composable
fun DatePickerDialog(
    modifier: Modifier = Modifier,
    properties: DialogProperties = DialogProperties(),
    yearPickerState: PickerState<Int> = rememberPickerState(currentDate.year),
    monthPickerState: PickerState<Int> = rememberPickerState(currentDate.monthNumber),
    startLocalDate: LocalDate = currentDate,
    yearItems: List<Int> = YEAR_RANGE,
    monthItems: List<Int> = MONTH_RANGE,
    visibleItemsCount: Int = 3,
    itemPadding: PaddingValues = PaddingValues(8.dp),
    textStyle: TextStyle = TextStyle(fontSize = 16.sp),
    selectedTextStyle: TextStyle = TextStyle(fontSize = 24.sp),
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
    pickerWidth: Dp = 100.dp,
    titleText: String = "Date Picker",
    onDismissRequest: () -> Unit,
    onDoneClickListener: (LocalDate) -> Unit
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

                YearMonthPicker(
                    modifier = Modifier.wrapContentSize(),
                    yearPickerState = yearPickerState,
                    monthPickerState = monthPickerState,
                    startLocalDate = startLocalDate,
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
                            val year = yearPickerState.selectedItem
                            val month = monthPickerState.selectedItem

                            onDoneClickListener(
                                LocalDate(
                                    year = year,
                                    monthNumber = month,
                                    dayOfMonth = startLocalDate.dayOfMonth
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