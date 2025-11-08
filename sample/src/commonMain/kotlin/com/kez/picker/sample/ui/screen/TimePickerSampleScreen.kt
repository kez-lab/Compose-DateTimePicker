package com.kez.picker.sample.ui.screen

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kez.picker.rememberPickerState
import com.kez.picker.time.TimePicker
import com.kez.picker.util.TimeFormat
import com.kez.picker.util.TimePeriod
import com.kez.picker.util.currentHour
import com.kez.picker.util.currentMinute
import compose.icons.FeatherIcons
import compose.icons.feathericons.ArrowLeft
import compose.icons.feathericons.Clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TimePickerSampleScreen(navController: NavController) {
    var selectedFormat by remember { mutableIntStateOf(0) }
    val hour12State =
        rememberPickerState(if (currentHour > 12) currentHour - 12 else if (currentHour == 0) 12 else currentHour)
    val hour24State = rememberPickerState(currentHour)
    val minuteState = rememberPickerState(currentMinute)
    val periodState = rememberPickerState(if (currentHour >= 12) TimePeriod.PM else TimePeriod.AM)

    // 선택된 시간 텍스트 계산
    val selectedTimeText by remember {
        derivedStateOf {
            if (selectedFormat == 0) {
                "%02d:%02d %s".format(hour12State.selectedItem, minuteState.selectedItem, periodState.selectedItem)
            } else {
                "%02d:%02d".format(hour24State.selectedItem, minuteState.selectedItem)
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("시간 피커 샘플", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(FeatherIcons.ArrowLeft, contentDescription = "뒤로가기")
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(it).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 선택 결과 표시 카드
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
                        contentDescription = "선택된 시간",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                    Text(
                        text = "선택된 시간: $selectedTimeText",
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
                    text = { Text("12시간제") })
                Tab(
                    selected = selectedFormat == 1,
                    onClick = { selectedFormat = 1 },
                    text = { Text("24시간제") })
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