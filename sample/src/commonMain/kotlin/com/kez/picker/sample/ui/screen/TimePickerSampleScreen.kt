package com.kez.picker.sample.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kez.picker.rememberTimePickerState
import com.kez.picker.time.TimePicker
import com.kez.picker.util.TimeFormat
import com.kez.picker.util.TimePeriod
import com.kez.picker.util.currentDateTime
import compose.icons.FeatherIcons
import compose.icons.feathericons.ArrowLeft
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

    val timeState12 = rememberTimePickerState(
        initialTime = currentTime,
        timeFormat = TimeFormat.HOUR_12
    )
    val timeState24 = rememberTimePickerState(
        initialTime = currentTime
    )
    val demoTime = LocalTime(hour = 9, minute = 30)

    val ktxTimeFormat12 = LocalTime.Format {
        amPmHour(padding = Padding.ZERO)
        char(':')
        minute(padding = Padding.ZERO)
        char(' ')
        amPmMarker(am = TimePeriod.AM.name, pm = TimePeriod.PM.name)
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
            CenterAlignedTopAppBar(
                title = { Text("TimePicker Sample", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { onBackPressed() }) {
                        Icon(FeatherIcons.ArrowLeft, contentDescription = "Back")
                    }
                }
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = FeatherIcons.Clock,
                        contentDescription = "Selected time",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                    Text(
                        text = "Selected time: $selectedTimeText",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        val now = currentDateTime().time
                        timeState12.selectTime(now)
                        timeState24.selectTime(now)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = FeatherIcons.Clock,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Set now")
                }
                OutlinedButton(
                    onClick = {
                        timeState12.selectTime(demoTime)
                        timeState24.selectTime(demoTime)
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
            if (selectedFormat == 0) {
                TimePicker(
                    state = timeState12,
                    hourPickerLabel = "시",
                    minutePickerLabel = "분",
                    periodPickerLabel = "오전/오후",
                    hourItemContentDescription = { "${it}시" },
                    minuteItemContentDescription = { "${it}분" },
                    periodItemContentDescription = { it.name },
                    previousItemActionLabel = "이전 항목 선택",
                    nextItemActionLabel = "다음 항목 선택"
                )
            } else {
                TimePicker(
                    state = timeState24,
                    visibleItemsCount = 5,
                    hourPickerLabel = "시",
                    minutePickerLabel = "분",
                    hourItemContentDescription = { "${it}시" },
                    minuteItemContentDescription = { "${it}분" },
                    previousItemActionLabel = "이전 항목 선택",
                    nextItemActionLabel = "다음 항목 선택"
                )
            }
        }
    }
}

@Preview
@Composable
fun TimePickerSampleScreenPreview() {
    TimePickerSampleScreen()
}
