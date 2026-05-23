package com.kez.picker.sample.ui.screen

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kez.picker.PickerDefaults
import com.kez.picker.TimePickerColumn
import com.kez.picker.sample.getTimePeriodContentDescription
import com.kez.picker.time.TimePicker
import com.kez.picker.time.rememberTimePickerState
import com.kez.picker.util.TimeFormat
import com.kez.picker.util.currentDateTime
import compose.icons.FeatherIcons
import compose.icons.feathericons.Clock
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TimePickerSampleScreen(
    onBackPressed: () -> Unit = {},
) {
    var selectedFormat by remember { mutableIntStateOf(0) }
    val currentTime = remember { currentDateTime().time }
    val businessHoursItems = remember {
        PickerDefaults.timePickerItems(
            minuteItems = listOf(0, 15, 30, 45),
            minTime = LocalTime(hour = 8, minute = 0),
            maxTime = LocalTime(hour = 18, minute = 0)
        )
    }

    val timeState12 = rememberTimePickerState(
        initialTime = currentTime,
        timeFormat = TimeFormat.HOUR_12
    )
    val timeState24 = rememberTimePickerState(
        items = businessHoursItems,
        initialTime = currentTime
    )
    val demoTime = LocalTime(hour = 9, minute = 30)
    var lastChangedHour by rememberSaveable { mutableIntStateOf(currentTime.hour) }
    var lastChangedMinute by rememberSaveable { mutableIntStateOf(currentTime.minute) }

    val ktxTimeFormat12 = LocalTime.Format {
        amPmHour(padding = Padding.ZERO)
        char(':')
        minute(padding = Padding.ZERO)
        char(' ')
        amPmMarker(am = "오전", pm = "오후")
    }

    val ktxTimeFormat24 = LocalTime.Format {
        hour(padding = Padding.ZERO)
        char(':')
        minute(padding = Padding.ZERO)
    }

    val selectedTimeText by remember {
        derivedStateOf {
            if (selectedFormat == 0) {
                timeState12.selectedTime.format(ktxTimeFormat12)
            } else {
                timeState24.selectedTime.format(ktxTimeFormat24)
            }
        }
    }

    Scaffold(
        topBar = {
            SampleTopAppBar(
                title = "TimePicker Sample",
                onBackPressed = onBackPressed
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SelectedValueCard(
                icon = FeatherIcons.Clock,
                label = "Selected time",
                value = selectedTimeText,
                supportingText = "Last callback: ${
                    LocalTime(lastChangedHour, lastChangedMinute).format(ktxTimeFormat24)
                }"
            )

            Spacer(modifier = Modifier.height(16.dp))

            SampleActionRow {
                Button(
                    onClick = {
                        val now = currentDateTime().time
                        timeState12.selectTime(now)
                        timeState24.selectTime(now, businessHoursItems)
                        val selectedTime = if (selectedFormat == 0) {
                            timeState12.selectedTime
                        } else {
                            timeState24.selectedTime
                        }
                        lastChangedHour = selectedTime.hour
                        lastChangedMinute = selectedTime.minute
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = FeatherIcons.Clock,
                        contentDescription = null
                    )
                    IconTextGap()
                    Text("Set now")
                }
                OutlinedButton(
                    onClick = {
                        timeState12.selectTime(demoTime)
                        timeState24.selectTime(demoTime, businessHoursItems)
                        val selectedTime = if (selectedFormat == 0) {
                            timeState12.selectedTime
                        } else {
                            timeState24.selectedTime
                        }
                        lastChangedHour = selectedTime.hour
                        lastChangedMinute = selectedTime.minute
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Set 09:30")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            PrimaryTabRow(
                selectedTabIndex = selectedFormat,
                modifier = Modifier.clip(RoundedCornerShape(16.dp))
            ) {
                Tab(
                    selected = selectedFormat == 0,
                    onClick = { selectedFormat = 0 },
                    text = { Text("12-Hour") })
                Tab(
                    selected = selectedFormat == 1,
                    onClick = { selectedFormat = 1 },
                    text = { Text("24-Hour") })
            }
            Spacer(modifier = Modifier.height(32.dp))
            PickerPanel {
                if (selectedFormat == 0) {
                    TimePicker(
                        state = timeState12,
                        onSelectedTimeChange = {
                            lastChangedHour = it.hour
                            lastChangedMinute = it.minute
                        },
                        display = PickerDefaults.timePickerDisplay(
                            hourItemText = { it.toString().padStart(2, '0') },
                            minuteItemText = { it.toString().padStart(2, '0') },
                            periodItemText = { getTimePeriodContentDescription(it) }
                        ),
                        layout = PickerDefaults.timePickerLayout(
                            columnOrder = listOf(
                                TimePickerColumn.HOUR,
                                TimePickerColumn.MINUTE,
                                TimePickerColumn.PERIOD
                            )
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
                        )
                    )
                } else {
                    TimePicker(
                        state = timeState24,
                        onSelectedTimeChange = {
                            lastChangedHour = it.hour
                            lastChangedMinute = it.minute
                        },
                        items = businessHoursItems,
                        display = PickerDefaults.timePickerDisplay(
                            hourItemText = { it.toString().padStart(2, '0') },
                            minuteItemText = { it.toString().padStart(2, '0') }
                        ),
                        style = PickerDefaults.style(visibleItemsCount = 5),
                        accessibility = PickerDefaults.timePickerAccessibility(
                            hourPickerLabel = "시간",
                            minutePickerLabel = "분",
                            hourItemContentDescription = { "${it}시" },
                            minuteItemContentDescription = { "${it}분" },
                            previousItemActionLabel = "이전 항목 선택",
                            nextItemActionLabel = "다음 항목 선택"
                        )
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun TimePickerSampleScreenPreview() {
    TimePickerSampleScreen()
}
