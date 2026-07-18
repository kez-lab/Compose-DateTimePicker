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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kez.picker.PickerDefaults
import com.kez.picker.sample.ui.screen.datetime.DateTimePicker
import com.kez.picker.sample.ui.screen.datetime.DefaultDateTimePickerItems
import com.kez.picker.sample.ui.screen.datetime.dateTimeDisplayText
import com.kez.picker.sample.ui.screen.datetime.rememberDateTimePickerState
import compose.icons.FeatherIcons
import compose.icons.feathericons.Clock
import kotlinx.datetime.LocalDateTime

@Composable
internal fun DateTimePickerSampleScreen(
    onBackPressed: () -> Unit
) {
    val items = remember { DefaultDateTimePickerItems }
    val initialDateTime = remember { LocalDateTime(2026, 2, 28, 23, 30) }
    val state = rememberDateTimePickerState(
        items = items,
        initialDateTime = initialDateTime
    )
    var userCallbackCount by rememberSaveable { mutableIntStateOf(0) }
    var lastUserSelection by rememberSaveable { mutableStateOf("No user change") }

    Scaffold(
        topBar = {
            SampleTopAppBar(
                title = "Exact Date-Time Slots",
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
                label = "Selected date-time",
                value = state.selectedDateTime.dateTimeDisplayText(),
                supportingText = "User callbacks: $userCallbackCount · Last: $lastUserSelection"
            )

            Spacer(modifier = Modifier.height(16.dp))

            SampleActionRow {
                Button(
                    onClick = {
                        state.selectDateTime(
                            dateTime = LocalDateTime(2026, 3, 1, 0, 30),
                            items = items
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Set Mar 1, 00:30 programmatically")
                }
                OutlinedButton(
                    onClick = {
                        state.selectDateTime(
                            dateTime = initialDateTime,
                            items = items
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Reset Feb 28, 23:30")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Sample-only correctness stress case — not an artifact API. The six " +
                        "exact candidates cross midnight. Changing February to March " +
                        "replaces day, hour, and minute sources and repairs 2026-02-28 23:30 " +
                        "to the nearest selectable 2026-03-01 00:00 in one commit.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            PickerPanel {
                Text(
                    text = "Swipe or scroll horizontally to inspect all five columns.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                DateTimePicker(
                    state = state,
                    items = items,
                    onSelectedDateTimeChange = { dateTime ->
                        lastUserSelection = dateTime.dateTimeDisplayText()
                        userCallbackCount += 1
                    },
                    style = PickerDefaults.style(visibleItemsCount = 3)
                )
            }
        }
    }
}
