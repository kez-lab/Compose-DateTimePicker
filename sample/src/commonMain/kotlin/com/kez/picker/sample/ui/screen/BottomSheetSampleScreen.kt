package com.kez.picker.sample.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
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
import com.kez.picker.util.currentDate
import com.kez.picker.util.currentDateTime
import compose.icons.FeatherIcons
import compose.icons.feathericons.Calendar
import compose.icons.feathericons.Clock
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalTime
import kotlinx.datetime.number

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BottomSheetSampleScreen(
    onBackPressed: () -> Unit = {},
) {
    val currentDate = remember { currentDate() }
    val currentTime = remember { currentDateTime().time }
    val yearMonthState = rememberYearMonthPickerState(
        initialDate = currentDate
    )
    val timeState = rememberTimePickerState(
        initialTime = currentTime,
        timeFormat = TimeFormat.HOUR_12
    )

    var selectedYear by rememberSaveable { mutableIntStateOf(currentDate.year) }
    var selectedMonth by rememberSaveable { mutableIntStateOf(currentDate.month.number) }
    var selectedHour by rememberSaveable { mutableIntStateOf(currentTime.hour) }
    var selectedMinute by rememberSaveable { mutableIntStateOf(currentTime.minute) }

    val selectedDateText = remember(selectedYear, selectedMonth) {
        "${selectedYear}년 ${getMonthName(selectedMonth)}"
    }
    val selectedTimeText = remember(selectedHour, selectedMinute) {
        formatTime12(LocalTime(selectedHour, selectedMinute))
    }

    // Bottom sheet state
    var showDateBottomSheet by remember { mutableStateOf(false) }
    var showTimeBottomSheet by remember { mutableStateOf(false) }
    val dateSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val timeSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            SampleTopAppBar(
                title = "BottomSheet Sample",
                onBackPressed = onBackPressed
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Select date and time in bottom sheet",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Selected date/time display card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Date display
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = FeatherIcons.Calendar,
                            contentDescription = "Date",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = selectedDateText,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(0.8f),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Time display
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = FeatherIcons.Clock,
                            contentDescription = "Time",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = selectedTimeText,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Date selection button
            Button(
                onClick = {
                    yearMonthState.selectYearMonth(
                        year = selectedYear,
                        month = selectedMonth
                    )
                    showDateBottomSheet = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    FeatherIcons.Calendar,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                Text("Select Date", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Time selection button
            Button(
                onClick = {
                    timeState.selectTime(LocalTime(selectedHour, selectedMinute))
                    showTimeBottomSheet = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(FeatherIcons.Clock, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                Text("Select Time", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        // Date selection bottom sheet
        if (showDateBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showDateBottomSheet = false },
                sheetState = dateSheetState
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Select Date",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Box(
                        modifier = Modifier.height(280.dp),
                        contentAlignment = Alignment.Center
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
                                colors = PickerDefaults.colors(
                                    textColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    dividerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                )
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            val selectedYearMonth = yearMonthState.selectedYearMonth
                            selectedYear = selectedYearMonth.year
                            selectedMonth = selectedYearMonth.month
                            scope.launch {
                                dateSheetState.hide()
                                showDateBottomSheet = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Confirm", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // Time selection bottom sheet
        if (showTimeBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showTimeBottomSheet = false },
                sheetState = timeSheetState
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Select Time",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Box(
                        modifier = Modifier.height(280.dp),
                        contentAlignment = Alignment.Center
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
                                colors = PickerDefaults.colors(
                                    textColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    dividerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                )
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            selectedHour = timeState.selectedTime.hour
                            selectedMinute = timeState.selectedTime.minute
                            scope.launch {
                                timeSheetState.hide()
                                showTimeBottomSheet = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Confirm", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
