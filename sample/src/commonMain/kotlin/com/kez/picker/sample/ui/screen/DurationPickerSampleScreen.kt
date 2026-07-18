package com.kez.picker.sample.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kez.picker.PickerDefaults
import com.kez.picker.duration.DurationPicker
import com.kez.picker.duration.rememberDurationPickerState
import compose.icons.FeatherIcons
import compose.icons.feathericons.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@Composable
internal fun DurationPickerSampleScreen(
    onBackPressed: () -> Unit
) {
    val workoutItems = remember {
        PickerDefaults.durationPickerItems(
            hourItems = listOf(0, 1),
            minuteItems = (0..55 step 5).toList(),
            maxDuration = 90.minutes
        )
    }
    val state = rememberDurationPickerState(
        items = workoutItems,
        initialDuration = 45.minutes
    )
    var lastUserMinutes by rememberSaveable { mutableIntStateOf(-1) }
    var userCallbackCount by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            SampleTopAppBar(
                title = "DurationPicker Sample",
                onBackPressed = onBackPressed
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SelectedValueCard(
                icon = FeatherIcons.Clock,
                label = "Workout duration",
                value = state.selectedDuration.toHourMinuteText(),
                supportingText = if (lastUserMinutes < 0) {
                    "User callbacks: $userCallbackCount · Last: No user change"
                } else {
                    "User callbacks: $userCallbackCount · Last: " +
                            lastUserMinutes.minutes.toHourMinuteText()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            SampleActionRow {
                Button(
                    onClick = { state.selectDuration(90.minutes, workoutItems) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Set 90 min")
                }
                OutlinedButton(
                    onClick = { state.selectDuration(45.minutes, workoutItems) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Reset 45 min")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Two columns commit one duration bounded to 0–90 minutes in 5-minute steps.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            PickerPanel {
                DurationPicker(
                    state = state,
                    items = workoutItems,
                    onSelectedDurationChange = { duration ->
                        lastUserMinutes = duration.inWholeMinutes.toInt()
                        userCallbackCount += 1
                    },
                    format = PickerDefaults.durationPickerFormat(
                        hourItemText = { "$it h" },
                        minuteItemText = { "$it min" },
                        hourItemContentDescription = { "$it hours" },
                        minuteItemContentDescription = { "$it minutes" }
                    ),
                    semantics = PickerDefaults.durationPickerSemantics(
                        hourPickerLabel = "Workout hours",
                        minutePickerLabel = "Workout minutes"
                    ),
                    style = PickerDefaults.style(visibleItemsCount = 5)
                )
            }
        }
    }
}

private fun Duration.toHourMinuteText(): String =
    "${inWholeMinutes / 60}h ${inWholeMinutes % 60}m"
