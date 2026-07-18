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
import com.kez.picker.sample.ui.screen.quantity.DefaultQuantityUnitItems
import com.kez.picker.sample.ui.screen.quantity.QuantityUnit
import com.kez.picker.sample.ui.screen.quantity.QuantityUnitPicker
import com.kez.picker.sample.ui.screen.quantity.QuantityUnitSelection
import com.kez.picker.sample.ui.screen.quantity.rememberQuantityUnitPickerState
import compose.icons.FeatherIcons
import compose.icons.feathericons.Layers

@Composable
internal fun QuantityUnitPickerSampleScreen(
    onBackPressed: () -> Unit
) {
    val items = remember { DefaultQuantityUnitItems }
    val state = rememberQuantityUnitPickerState(
        items = items,
        initialSelection = QuantityUnitSelection(
            quantity = 2_500,
            unit = QuantityUnit.GRAM
        )
    )
    var userCallbackCount by rememberSaveable { mutableIntStateOf(0) }
    var lastUserSelection by rememberSaveable { mutableStateOf("No user change") }

    Scaffold(
        topBar = {
            SampleTopAppBar(
                title = "Quantity + Unit Sample",
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
                label = "Selected mass",
                value = state.selectedSelection.displayText(),
                supportingText = "Normalized: ${state.selectedSelection.normalizedGrams} g · " +
                        "User callbacks: $userCallbackCount · Last: $lastUserSelection"
            )

            Spacer(modifier = Modifier.height(16.dp))

            SampleActionRow {
                Button(
                    onClick = {
                        state.selectSelection(
                            selection = QuantityUnitSelection(
                                quantity = 2,
                                unit = QuantityUnit.KILOGRAM
                            ),
                            items = items
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Set 2 kg programmatically")
                }
                OutlinedButton(
                    onClick = {
                        state.selectSelection(
                            selection = QuantityUnitSelection(
                                quantity = 2_500,
                                unit = QuantityUnit.GRAM
                            ),
                            items = items
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Reset 2,500 g")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "This coarse allowed-weight bucket intentionally replaces the quantity " +
                        "source and step when the unit changes. A unit switch can therefore " +
                        "change normalized mass: 2,500 g repairs to the smaller 2 kg in an " +
                        "equal-distance tie. It is not a precision unit converter.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            PickerPanel {
                QuantityUnitPicker(
                    state = state,
                    items = items,
                    onSelectedQuantityUnitChange = { selection ->
                        lastUserSelection = selection.displayText()
                        userCallbackCount += 1
                    },
                    style = PickerDefaults.style(visibleItemsCount = 5)
                )
            }
        }
    }
}
