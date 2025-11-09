package com.kez.picker.sample.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.kez.picker.rememberPickerState
import com.kez.picker.time.TimePicker
import com.kez.picker.util.TimeFormat
import com.kez.picker.util.TimePeriod
import com.kez.picker.util.currentHour
import com.kez.picker.util.currentMinute
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
    val hour12State =
        rememberPickerState(if (currentHour > 12) currentHour - 12 else if (currentHour == 0) 12 else currentHour)
    val hour24State = rememberPickerState(currentHour)
    val minuteState = rememberPickerState(currentMinute)
    val periodState = rememberPickerState(if (currentHour >= 12) TimePeriod.PM else TimePeriod.AM)

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
                val hour12 = hour12State.selectedItem // (1 ~ 12)
                val minute = minuteState.selectedItem
                val isAm = periodState.selectedItem == TimePeriod.AM

                val hour24 = when {
                    isAm && hour12 == 12 -> 0    // 12:xx AM (midnight) -> 0
                    !isAm && hour12 != 12 -> hour12 + 12 // 1:xx PM (13) ~ 11:xx PM (23)
                    else -> hour12               // 1:xx AM (1) ~ 11:xx AM (11), 12:xx PM (12)
                }
                val time = LocalTime(hour24, minute)
                time.format(ktxTimeFormat12)

            } else {
                val time = LocalTime(hour24State.selectedItem, minuteState.selectedItem)
                time.format(ktxTimeFormat24)
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
            modifier = Modifier.fillMaxSize().padding(it).padding(16.dp),
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

            Spacer(modifier = Modifier.height(24.dp))

            TabRow(
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
                    hourPickerState = hour12State,
                    minutePickerState = minuteState,
                    periodPickerState = periodState,
                    timeFormat = TimeFormat.HOUR_12
                )
            } else {
                TimePicker(
                    hourPickerState = hour24State,
                    minutePickerState = minuteState,
                    timeFormat = TimeFormat.HOUR_24
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