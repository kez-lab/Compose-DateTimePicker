package com.kez.picker.sample.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
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
import kotlinx.datetime.number

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun IntegratedPickerScreen(
    onBackPressed: () -> Unit = {},
) {
    val now = remember { currentDateTime() }
    val currentDate = now.date
    val currentTime = now.time

    val yearMonthState = rememberYearMonthPickerState(
        initialDate = currentDate
    )
    val timeState = rememberTimePickerState(
        initialTime = currentTime,
        timeFormat = TimeFormat.HOUR_12
    )

    var selectedTabIndex by remember { mutableIntStateOf(0) }

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
                title = "Integrated Sample",
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SelectedDateTimeCard(selectedDateText, selectedTimeText)
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    PrimaryTabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clip(
                            RoundedCornerShape(
                                topStart = 20.dp,
                                topEnd = 20.dp
                            )
                        )
                    ) {
                        Tab(
                            selected = selectedTabIndex == 0,
                            onClick = { selectedTabIndex = 0 },
                            text = { Text("Date") })
                        Tab(
                            selected = selectedTabIndex == 1,
                            onClick = { selectedTabIndex = 1 },
                            text = { Text("Time") })
                    }
                    HorizontalDivider()
                    AnimatedContent(
                        targetState = selectedTabIndex,
                        transitionSpec = {
                            val direction = if (targetState > initialState) 1 else -1
                            slideInHorizontally { width -> direction * width } + fadeIn() togetherWith
                                    slideOutHorizontally { width -> -direction * width } + fadeOut() using
                                    SizeTransform(clip = false)
                        },
                        label = "Picker Animation"
                    ) { tabIndex ->
                        Box(
                            modifier = Modifier.padding(16.dp).height(280.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (tabIndex == 0) {
                                YearMonthPicker(
                                    state = yearMonthState,
                                    yearPickerLabel = "연도",
                                    monthPickerLabel = "월",
                                    yearItemContentDescription = { "${it}년" },
                                    monthItemContentDescription = { getMonthContentDescription(it) },
                                    previousItemActionLabel = "이전 항목 선택",
                                    nextItemActionLabel = "다음 항목 선택",
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
                            } else {
                                TimePicker(
                                    state = timeState,
                                    hourPickerLabel = "시간",
                                    minutePickerLabel = "분",
                                    periodPickerLabel = "오전/오후",
                                    hourItemContentDescription = { "${it}시" },
                                    minuteItemContentDescription = { "${it}분" },
                                    periodItemContentDescription = { getTimePeriodContentDescription(it) },
                                    previousItemActionLabel = "이전 항목 선택",
                                    nextItemActionLabel = "다음 항목 선택",
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
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    yearMonthState.selectDate(currentDate)
                    timeState.selectTime(currentTime)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Reset to launch selection", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}


@Composable
internal fun SelectedDateTimeCard(date: String, time: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clearAndSetSemantics {
                contentDescription = "Selected date, $date, selected time, $time"
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = FeatherIcons.Calendar,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = date,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(24.dp)
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = FeatherIcons.Clock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = time,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewTimeCard() {
    IntegratedPickerScreen()
}
