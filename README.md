# Compose DateTimePicker

[![Read in Korean](https://img.shields.io/badge/README-Korean-green)](./README_KO.md)

A generic, customizable, and multiplatform date and time picker library for Compose Multiplatform.
It provides consistent UI components across Android, iOS, Desktop (JVM), and Web (Wasm).

## Features

*   **Multiplatform Support**: seamless integration for Android, iOS, Desktop (JVM), and Web (Wasm).
*   **TimePicker**: Supports both 12-hour (AM/PM) and 24-hour formats.
* **DatePicker**: A complete date picker for selecting year, month, and day with automatic day
  validation.
*   **YearMonthPicker**: A dedicated component for selecting years and months.
*   **Customizable**: Extensible API allowing custom content rendering, styling, and configuration.
* **State Management**: simplified state handling with `rememberTimePickerState`,
  `rememberDatePickerState`, and `rememberYearMonthPickerState`.
*   **Accessibility**: Built with accessibility in mind, supporting screen readers and navigation.

## Installation

Add the dependency to your version catalog or build file.

### Version Catalog (libs.versions.toml)

```toml
[versions]
composeDateTimePicker = "0.6.0"

[libraries]
compose-date-time-picker = { module = "io.github.kez-lab:compose-date-time-picker", version.ref = "composeDateTimePicker" }
```

### Gradle (build.gradle.kts)

```kotlin
dependencies {
    implementation("io.github.kez-lab:compose-date-time-picker:0.6.0")
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
        initialHour = currentHour(),
        initialMinute = currentMinute(),
        timeFormat = TimeFormat.HOUR_24
    )

    TimePicker(
        state = state
    )

    // Use state.selectedTime when passing the result to app logic.
}
```

#### 2. 12-Hour Format (AM/PM)

```kotlin
import androidx.compose.runtime.Composable
import com.kez.picker.time.TimePicker
import com.kez.picker.rememberTimePickerState
import com.kez.picker.util.TimeFormat
import com.kez.picker.util.currentHour
import com.kez.picker.util.currentMinute

@Composable
fun TimePicker12hExample() {
    // Handling of 12-hour format conversion is now done internally by the state
    val state = rememberTimePickerState(
        initialHour = currentHour(),
        initialMinute = currentMinute(),
        timeFormat = TimeFormat.HOUR_12
    )

    TimePicker(
        state = state
    )

    // state.selectedTime is always a kotlinx.datetime.LocalTime.
}
```

### DatePicker

Use `DatePicker` for selecting a complete date (year, month, and day). The component automatically
adjusts the day when the selected month changes (e.g., Feb 30 → Feb 28).

```kotlin
import androidx.compose.runtime.Composable
import com.kez.picker.date.DatePicker
import com.kez.picker.date.rememberDatePickerState
import com.kez.picker.util.currentDate
import com.kez.picker.util.currentMonth

@Composable
fun DatePickerExample() {
    val state = rememberDatePickerState(
        initialYear = currentDate().year,
        initialMonth = currentMonth(),
        initialDay = currentDate().day
    )

    DatePicker(
        state = state
    )

    // Use state.selectedDate when passing the result to app logic.
}
```

### YearMonthPicker

Use `YearMonthPicker` for selecting a specific month in a year.

```kotlin
import androidx.compose.runtime.Composable
import com.kez.picker.date.YearMonthPicker
import com.kez.picker.rememberYearMonthPickerState
import com.kez.picker.util.currentDate
import com.kez.picker.util.currentMonth

@Composable
fun YearMonthPickerExample() {
    val state = rememberYearMonthPickerState(
        initialYear = currentDate().year,
        initialMonth = currentMonth()
    )

    YearMonthPicker(
        state = state
    )

    // state.selectedMonthDate is the first day of the selected month.
}
```

### BottomSheet Integration

The pickers work seamlessly within a `ModalBottomSheet` or other dialog components.

```kotlin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.kez.picker.time.TimePicker
import com.kez.picker.rememberTimePickerState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetPickerExample() {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val state = rememberTimePickerState()

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
| `startTime` | Legacy initial time parameter. Prefer setting initial values in `rememberTimePickerState`. | `currentDateTime()` |
| `minuteItems` | Minute values available for selection. | `0..59` |
| `hourItems` | Hour values available for selection. | `0..23` or `1..12` |
| `visibleItemsCount` | Number of items visible in the list. | `3` |
| `colors` | Colors for text, selected text, dividers, and selected item background. | `PickerDefaults.colors()` |
| `textStyles` | Text styles for selected and unselected items. | `PickerDefaults.textStyles()` |
| `isDividerVisible` | Whether selection dividers are visible. | `true` |

**TimePickerState Properties:**

- `selectedHour`: The selected hour shown by the picker.
- `selectedMinute`: The selected minute (0-59).
- `selectedPeriod`: The selected AM/PM period when using 12-hour format.
- `selectedHourOfDay`: The selected hour converted to 24-hour clock time (0-23).
- `selectedTime`: The selected value as `kotlinx.datetime.LocalTime`.

`rememberTimePickerState` uses saveable state. On Android, selected values can be restored across Activity recreation when the platform saveable registry is available.

### DatePicker

| Parameter           | Description                             | Default                     |
|:--------------------|:----------------------------------------|:----------------------------|
| `state`             | The state object to control the picker. | `rememberDatePickerState()` |
| `startLocalDate`    | Legacy initial date parameter. Prefer setting initial values in `rememberDatePickerState`. | `currentDate()`             |
| `yearItems`         | List of years available for selection.  | `1000..9999`                |
| `monthItems`        | List of months available for selection. | `1..12`                     |
| `visibleItemsCount` | Number of items visible in the list.    | `3`                         |
| `colors`            | Colors for text, selected text, dividers, and selected item background. | `PickerDefaults.colors()` |
| `textStyles`        | Text styles for selected and unselected items. | `PickerDefaults.textStyles()` |

**DatePickerState Properties:**

- `selectedYear`: The currently selected year.
- `selectedMonth`: The currently selected month (1-12).
- `selectedDay`: The currently selected day (1-31, auto-adjusted based on month).
- `selectedDate`: The selected value as `kotlinx.datetime.LocalDate`.
- `maxDay`: The maximum valid day for the selected year and month.

`rememberDatePickerState` uses saveable state. On Android, selected values can be restored across Activity recreation when the platform saveable registry is available.

### YearMonthPicker

| Parameter | Description | Default |
| :--- | :--- | :--- |
| `state` | The state object to control the picker. | `rememberYearMonthPickerState()` |
| `startLocalDate` | Legacy initial date parameter. Prefer setting initial values in `rememberYearMonthPickerState`. | `currentDate()` |
| `yearItems` | List of years available for selection. | `1000..9999` |
| `monthItems` | List of months available for selection. | `1..12` |
| `visibleItemsCount` | Number of items visible in the list. | `3` |
| `colors` | Colors for text, selected text, dividers, and selected item background. | `PickerDefaults.colors()` |
| `textStyles` | Text styles for selected and unselected items. | `PickerDefaults.textStyles()` |

**YearMonthPickerState Properties:**

- `selectedYear`: The currently selected year.
- `selectedMonth`: The currently selected month (1-12).
- `selectedMonthDate`: The selected year/month represented as the first day of that month.

`rememberYearMonthPickerState` uses saveable state. On Android, selected values can be restored across Activity recreation when the platform saveable registry is available.

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
