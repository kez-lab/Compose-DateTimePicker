package com.kez.picker.sample.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kez.picker.PickerDefaults
import com.kez.picker.date.YearMonthPicker
import com.kez.picker.date.rememberYearMonthPickerState
import com.kez.picker.sample.formatTime12
import com.kez.picker.sample.getMonthContentDescription
import com.kez.picker.sample.getMonthName
import com.kez.picker.sample.getTimePeriodContentDescription
import com.kez.picker.time.TimePicker
import com.kez.picker.time.rememberTimePickerState
import com.kez.picker.util.TimeFormat
import com.kez.picker.util.currentDateTime
import compose.icons.FeatherIcons
import compose.icons.feathericons.Calendar
import compose.icons.feathericons.Clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BackgroundStylePickerScreen(
    onBackPressed: () -> Unit = {},
) {
    val now = remember { currentDateTime() }
    val yearMonthState = rememberYearMonthPickerState(
        initialDate = now.date
    )
    val timeState = rememberTimePickerState(
        initialTime = now.time,
        timeFormat = TimeFormat.HOUR_12
    )

    val selectedDateText = remember(yearMonthState.selectedYear, yearMonthState.selectedMonth) {
        "${yearMonthState.selectedYear}년 ${getMonthName(yearMonthState.selectedMonth)}"
    }
    val selectedTimeText =
        remember(timeState.selectedHour, timeState.selectedMinute, timeState.selectedPeriod) {
            formatTime12(timeState.selectedHour, timeState.selectedMinute, timeState.selectedPeriod)
        }

    Scaffold(
        topBar = {
            SampleTopAppBar(
                title = "Background Style Sample",
                onBackPressed = onBackPressed
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SelectedValueCard(
                icon = FeatherIcons.Calendar,
                label = "Selected month",
                value = selectedDateText,
                supportingText = "YearMonthPicker styled without dividers"
            )

            SelectedValueCard(
                icon = FeatherIcons.Clock,
                label = "Selected time",
                value = selectedTimeText,
                supportingText = "TimePicker using the same state/update pattern"
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    YearMonthPicker(
                        state = yearMonthState,
                        display = PickerDefaults.yearMonthPickerDisplay(
                            yearItemText = { "${it}년" },
                            monthItemText = { getMonthName(it) }
                        ),
                        accessibility = PickerDefaults.yearMonthPickerAccessibility(
                            yearPickerLabel = "연도",
                            monthPickerLabel = "월",
                            yearItemContentDescription = { "${it}년" },
                            monthItemContentDescription = { getMonthContentDescription(it) },
                            previousItemActionLabel = "이전 항목 선택",
                            nextItemActionLabel = "다음 항목 선택"
                        ),
                        style = PickerDefaults.style(
                            textStyles = PickerDefaults.textStyles(
                                textStyle = TextStyle(
                                    fontSize = 18.sp
                                ),
                                selectedTextStyle = TextStyle(
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            ),
                            isDividerVisible = false,
                            colors = PickerDefaults.colors(
                                textColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                selectedItemBackgroundColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TimePicker(
                        state = timeState,
                        display = PickerDefaults.timePickerDisplay(
                            hourItemText = { it.toString().padStart(2, '0') },
                            minuteItemText = { it.toString().padStart(2, '0') },
                            periodItemText = { getTimePeriodContentDescription(it) }
                        ),
                        accessibility = PickerDefaults.timePickerAccessibility(
                            hourPickerLabel = "시간",
                            minutePickerLabel = "분",
                            periodPickerLabel = "오전/오후",
                            hourItemContentDescription = { "${it}시" },
                            minuteItemContentDescription = { "${it}분" },
                            periodItemContentDescription = { getTimePeriodContentDescription(it) },
                            previousItemActionLabel = "이전 항목 선택",
                            nextItemActionLabel = "다음 항목 선택"
                        ),
                        style = PickerDefaults.style(
                            textStyles = PickerDefaults.textStyles(
                                textStyle = TextStyle(
                                    fontSize = 18.sp
                                ),
                                selectedTextStyle = TextStyle(
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            ),
                            isDividerVisible = false,
                            colors = PickerDefaults.colors(
                                textColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                selectedItemBackgroundColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    yearMonthState.selectDate(now.date)
                    timeState.selectTime(now.time)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Reset to launch values", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Preview
@Composable
fun PreviewBackgroundStylePickerScreen() {
    BackgroundStylePickerScreen()
}
