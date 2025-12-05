# Compose DateTimePicker

[![Read in Korean](https://img.shields.io/badge/README-Korean-green)](./README_KO.md)

A generic, customizable, and multiplatform date and time picker library for Compose Multiplatform.
It provides consistent UI components across Android, iOS, Desktop (JVM), and Web (Wasm).

## Features

*   **Multiplatform Support**: seamless integration for Android, iOS, Desktop (JVM), and Web (Wasm).
*   **TimePicker**: Supports both 12-hour (AM/PM) and 24-hour formats.
*   **YearMonthPicker**: A dedicated component for selecting years and months.
*   **Customizable**: Extensible API allowing custom content rendering, styling, and configuration.
*   **State Management**: simplified state handling with `rememberTimePickerState` and `rememberYearMonthPickerState`.
*   **Accessibility**: Built with accessibility in mind, supporting screen readers and navigation.

## Installation

Add the dependency to your version catalog or build file.

### Version Catalog (libs.versions.toml)

```toml
[versions]
composeDateTimePicker = "0.4.0"

[libraries]
compose-date-time-picker = { module = "io.github.kez-lab:compose-date-time-picker", version.ref = "composeDateTimePicker" }
```

### Gradle (build.gradle.kts)

```kotlin
dependencies {
    implementation("io.github.kez-lab:compose-date-time-picker:0.4.0")
}
```

## Usage

### TimePicker

Use `TimePicker` for time selection. It supports both 12-hour and 24-hour formats.

#### 1. 24-Hour Format

```kotlin
import androidx.compose.runtime.Composable
import com.kez.picker.time.TimePicker
import com.kez.picker.rememberTimePickerState
import com.kez.picker.util.TimeFormat
import com.kez.picker.util.currentHour
import com.kez.picker.util.currentMinute

@Composable
fun TimePicker24hExample() {
    val state = rememberTimePickerState(
        initialHour = currentHour,
        initialMinute = currentMinute,
        timeFormat = TimeFormat.HOUR_24
    )

    TimePicker(
        state = state
    )
}
```

#### 2. 12-Hour Format (AM/PM)

```kotlin
import androidx.compose.runtime.Composable
import com.kez.picker.time.TimePicker
import com.kez.picker.rememberTimePickerState
import com.kez.picker.util.TimeFormat
import com.kez.picker.util.TimePeriod
import com.kez.picker.util.currentHour
import com.kez.picker.util.currentMinute

@Composable
fun TimePicker12hExample() {
    // Handling of 12-hour format conversion is now done internally by the state
    val state = rememberTimePickerState(
        initialHour = currentHour,
        initialMinute = currentMinute,
        timeFormat = TimeFormat.HOUR_12
    )

    TimePicker(
        state = state
    )
}
```

### YearMonthPicker

Use `YearMonthPicker` for selecting a specific month in a year.

```kotlin
import androidx.compose.runtime.Composable
import com.kez.picker.date.YearMonthPicker
import com.kez.picker.rememberYearMonthPickerState
import com.kez.picker.util.currentDate

@Composable
fun YearMonthPickerExample() {
    val state = rememberYearMonthPickerState(
        initialYear = currentDate.year,
        initialMonth = currentDate.monthNumber
    )

    YearMonthPicker(
        state = state
    )
}
```

### BottomSheet Integration

The pickers work seamlessly within a `ModalBottomSheet` or other dialog components.

```kotlin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.kez.picker.time.TimePicker
import com.kez.picker.rememberTimePickerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetPickerExample() {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val state = rememberTimePickerState()
    val scope = rememberCoroutineScope()

    Button(onClick = { showBottomSheet = true }) {
        Text("Select Time")
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            TimePicker(state = state)
            // Add confirmation buttons logic here
        }
    }
}
```

## API Reference

### TimePicker

| Parameter | Description | Default |
| :--- | :--- | :--- |
| `state` | The state object to control the picker. | `rememberTimePickerState()` |
| `startTime` | The initial time to set the picker to. | `currentDateTime` |
| `visibleItemsCount` | Number of items visible in the list. | `3` |
| `textStyle` | Style for unselected items. | `16.sp` |
| `selectedTextStyle` | Style for selected item. | `22.sp` |
| `dividerColor` | Color of the selection dividers. | `LocalContentColor.current` |

### YearMonthPicker

| Parameter | Description | Default |
| :--- | :--- | :--- |
| `state` | The state object to control the picker. | `rememberYearMonthPickerState()` |
| `startLocalDate` | The initial date to set the picker to. | `currentDate` |
| `yearItems` | List of years available for selection. | `1900..2100` |
| `monthItems` | List of months available for selection. | `1..12` |
| `visibleItemsCount` | Number of items visible in the list. | `3` |

## License

```
Copyright 2024 KEZ Lab

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
