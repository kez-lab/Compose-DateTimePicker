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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kez.picker.PickerDefaults
import com.kez.picker.WheelPicker
import compose.icons.FeatherIcons
import compose.icons.feathericons.Layers

private val SeatCountItems = (1..10).toList()

@Composable
internal fun WheelPickerSampleScreen(
    onBackPressed: () -> Unit
) {
    var selectedSeats by rememberSaveable { mutableIntStateOf(3) }
    var settledSeats by rememberSaveable { mutableIntStateOf(3) }
    var settledCallbackCount by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            SampleTopAppBar(
                title = "WheelPicker Sample",
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
                icon = FeatherIcons.Layers,
                label = "Live selection",
                value = "$selectedSeats seats",
                supportingText =
                    "Last settled: $settledSeats seats · Settled callbacks: $settledCallbackCount"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { selectedSeats = 8 },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Set 8 seats programmatically")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Programmatic values move the wheel without dispatching interaction callbacks.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            PickerPanel {
                WheelPicker(
                    items = SeatCountItems,
                    selectedItem = selectedSeats,
                    onSelectedItemChange = { selectedSeats = it },
                    onSelectionSettled = { seats ->
                        settledSeats = seats
                        settledCallbackCount += 1
                    },
                    isInfinity = false,
                    format = PickerDefaults.itemFormat(
                        itemText = { "$it seats" },
                        itemContentDescription = { "$it seats" }
                    ),
                    style = PickerDefaults.style(
                        visibleItemsCount = 5,
                        textStyles = PickerDefaults.textStyles(
                            textStyle = MaterialTheme.typography.bodyLarge,
                            selectedTextStyle = TextStyle(
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        ),
                        colors = PickerDefaults.colors(
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            dividerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                    ),
                    semantics = PickerDefaults.semantics(
                        pickerLabel = "Seat count",
                        previousItemActionLabel = "Select fewer seats",
                        nextItemActionLabel = "Select more seats"
                    )
                )
            }
        }
    }
}
