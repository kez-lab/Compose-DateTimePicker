package com.kez.picker.benchmark.target

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kez.picker.Picker
import com.kez.picker.PickerDefaults
import com.kez.picker.date.DatePicker
import com.kez.picker.date.rememberDatePickerState
import kotlinx.datetime.LocalDate

private const val BenchmarkScenarioExtra = "com.kez.picker.benchmark.target.BENCHMARK_SCENARIO"
private const val DateDefaultScenario = "date-default"
private const val DateExactScenario = "date-custom-exact"

class PickerBenchmarkActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val benchmarkScenario = requireNotNull(
            intent.getStringExtra(BenchmarkScenarioExtra)
        ) { "$BenchmarkScenarioExtra is required" }
        setContent {
            MaterialTheme {
                PickerBenchmarkContent(benchmarkScenario)
            }
        }
    }
}

@Composable
private fun PickerBenchmarkContent(scenario: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (scenario) {
            DateDefaultScenario -> DatePicker(
                state = rememberDatePickerState(
                    initialDate = LocalDate(2024, 6, 15)
                )
            )

            DateExactScenario -> DatePicker(
                state = rememberDatePickerState(
                    initialDate = LocalDate(2024, 6, 15)
                ),
                format = PickerDefaults.datePickerFormat(
                    yearItemText = { it.toString() }
                )
            )

            else -> GenericPickerBenchmark(
                itemCount = scenario.removePrefix("generic-").toInt()
            )
        }
    }
}

@Composable
private fun GenericPickerBenchmark(itemCount: Int) {
    val items = remember(itemCount) { (1..itemCount).toList() }
    var selectedItem by remember(itemCount) {
        mutableIntStateOf(items[itemCount / 2])
    }

    Picker(
        items = items,
        selectedItem = selectedItem,
        onSelectedItemChange = { selectedItem = it },
        modifier = Modifier.width(160.dp),
        isInfinity = false
    )
}
