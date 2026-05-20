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
import androidx.compose.runtime.remember
import com.kez.picker.time.TimePicker
import com.kez.picker.rememberTimePickerState
import com.kez.picker.util.TimeFormat
import com.kez.picker.util.currentDateTime

@Composable
fun TimePicker24hExample() {
    val initialTime = remember { currentDateTime().time }
    val state = rememberTimePickerState(
        initialTime = initialTime,
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
import androidx.compose.runtime.remember
import com.kez.picker.time.TimePicker
import com.kez.picker.rememberTimePickerState
import com.kez.picker.util.TimeFormat
import com.kez.picker.util.currentDateTime

@Composable
fun TimePicker12hExample() {
    // Handling of 12-hour format conversion is now done internally by the state
    val initialTime = remember { currentDateTime().time }
    val state = rememberTimePickerState(
        initialTime = initialTime,
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
import androidx.compose.runtime.remember
import com.kez.picker.date.DatePicker
import com.kez.picker.date.rememberDatePickerState
import com.kez.picker.util.currentDate

@Composable
fun DatePickerExample() {
    val initialDate = remember { currentDate() }
    val state = rememberDatePickerState(
        initialDate = initialDate
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
import androidx.compose.runtime.remember
import com.kez.picker.date.YearMonthPicker
import com.kez.picker.rememberYearMonthPickerState
import com.kez.picker.util.currentDate

@Composable
fun YearMonthPickerExample() {
    val initialDate = remember { currentDate() }
    val state = rememberYearMonthPickerState(
        initialDate = initialDate
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

Accessibility label parameters customize the picker-column prefix used in semantics. `*ItemContentDescription`
parameters customize the accessibility value text without changing the visual item text. Selection is exposed
through Compose `selected` semantics rather than appended as a hardcoded English phrase.

### Programmatic Selection

Create picker state with `remember*State`, pass it to the picker, then call the public selection method
from an event handler or a `LaunchedEffect(externalValue)`. Do not recreate the state just to reset the
selection.

| State | Method |
| :--- | :--- |
| `PickerState<T>` | `selectItem(item)` |
| `TimePickerState` | `selectTime(LocalTime(...))` |
| `DatePickerState` | `selectDate(LocalDate(...))` |
| `YearMonthPickerState` | `selectYearMonth(year, month)` or `selectDate(LocalDate(...))` |

```kotlin
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.kez.picker.rememberTimePickerState
import com.kez.picker.time.TimePicker
import kotlinx.datetime.LocalTime

@Composable
fun ProgrammaticTimePickerExample() {
    val state = rememberTimePickerState(initialTime = LocalTime(8, 0))

    Column {
        Button(onClick = { state.selectTime(LocalTime(9, 30)) }) {
            Text("Set 09:30")
        }

        TimePicker(state = state)
    }
}
```

The picker scroll position is synchronized when the current item lists contain the requested values. If a
requested value is missing from a custom list, that child picker normalizes back to its currently centered
item.

### TimePicker

| Parameter | Description | Default |
| :--- | :--- | :--- |
| `state` | The state object to control the picker. | `rememberTimePickerState()` |
| `startTime` | Legacy compatibility parameter. It does not initialize or update `state`, even when `state` is omitted; use `rememberTimePickerState(initialTime = ...)` or explicit initial values instead. | `currentDateTime()` |
| `minuteItems` | Minute values available for selection. Values must be in `0..59`. | `0..59` |
| `hourItems` | Hour values available for selection. Values must be in `0..23` for 24-hour time or display-hour `1..12` for 12-hour time. | `0..23` or `1..12` |
| `periodItems` | AM/PM values available in 12-hour time. Must not be empty when `timeFormat` is `HOUR_12`. | `TimePeriod.entries` |
| `visibleItemsCount` | Number of items visible in the list. | `3` |
| `colors` | Colors for text, selected text, dividers, and selected item background. | `PickerDefaults.colors()` |
| `textStyles` | Text styles for selected and unselected items. | `PickerDefaults.textStyles()` |
| `isDividerVisible` | Whether selection dividers are visible. | `true` |
| `hourPickerLabel` | Accessibility label for the hour picker. Pass `null` to omit the picker label prefix. | `"Hour"` |
| `minutePickerLabel` | Accessibility label for the minute picker. Pass `null` to omit the picker label prefix. | `"Minute"` |
| `periodPickerLabel` | Accessibility label for the AM/PM picker in 12-hour time. Pass `null` to omit the picker label prefix. | `"AM/PM"` |
| `hourItemContentDescription` | Accessibility description for each hour value. | `it.toString()` |
| `minuteItemContentDescription` | Accessibility description for each minute value. | `it.toString()` |
| `periodItemContentDescription` | Accessibility description for each AM/PM value in 12-hour time. | `it.name` |

**TimePickerState Properties:**

- `selectedHour`: The selected hour shown by the picker.
- `selectedMinute`: The selected minute (0-59).
- `selectedPeriod`: The selected AM/PM period when using 12-hour format.
- `selectedHourOfDay`: The selected hour converted to 24-hour clock time (0-23).
- `selectedTime`: The selected value as `kotlinx.datetime.LocalTime`.

`rememberTimePickerState` uses saveable state. On Android, selected values can be restored across Activity recreation when the platform saveable registry is available.

For initial values, use either `rememberTimePickerState(initialTime = LocalTime(...))` or the explicit `initialHour`/`initialMinute` parameters. The `startTime` component parameter is retained only for source compatibility and is not used.

To change the selection after state creation, call `state.selectTime(LocalTime(...))`.

Invalid custom item values throw `IllegalArgumentException` during composition. If the current or restored selection is valid but not present in a custom list, the picker starts from the first item in that list and normalizes the state. In 12-hour mode, `hourItems` uses display-hour values (`1..12`): `initialHour = 13` becomes `state.selectedHour == 1` with `PM`.

### DatePicker

| Parameter           | Description                             | Default                     |
|:--------------------|:----------------------------------------|:----------------------------|
| `state`             | The state object to control the picker. | `rememberDatePickerState()` |
| `startLocalDate`    | Legacy compatibility parameter. It does not initialize or update `state`, even when `state` is omitted; use `rememberDatePickerState(initialDate = ...)` or explicit initial values instead. | `currentDate()`             |
| `yearItems`         | List of years available for selection. Values must be in `1000..9999`. | `1000..9999`                |
| `monthItems`        | List of months available for selection. Values must be in `1..12`. | `1..12`                     |
| `visibleItemsCount` | Number of items visible in the list.    | `3`                         |
| `colors`            | Colors for text, selected text, dividers, and selected item background. | `PickerDefaults.colors()` |
| `textStyles`        | Text styles for selected and unselected items. | `PickerDefaults.textStyles()` |
| `yearPickerLabel`   | Accessibility label for the year picker. Pass `null` to omit the picker label prefix. | `"Year"` |
| `monthPickerLabel`  | Accessibility label for the month picker. Pass `null` to omit the picker label prefix. | `"Month"` |
| `dayPickerLabel`    | Accessibility label for the day picker. Pass `null` to omit the picker label prefix. | `"Day"` |
| `yearItemContentDescription` | Accessibility description for each year value. | `it.toString()` |
| `monthItemContentDescription` | Accessibility description for each month value. | `it.toString()` |
| `dayItemContentDescription` | Accessibility description for each day value. | `it.toString()` |

**DatePickerState Properties:**

- `selectedYear`: The currently selected year.
- `selectedMonth`: The currently selected month (1-12).
- `selectedDay`: The currently selected day (1-31, auto-adjusted based on month).
- `selectedDate`: The selected value as `kotlinx.datetime.LocalDate`.
- `maxDay`: The maximum valid day for the selected year and month.

`rememberDatePickerState` uses saveable state. On Android, selected values can be restored across Activity recreation when the platform saveable registry is available.

For initial values, use either `rememberDatePickerState(initialDate = LocalDate(...))` or the explicit `initialYear`/`initialMonth`/`initialDay` parameters. Initial years must be in `1000..9999`. The `startLocalDate` component parameter is retained only for source compatibility and is not used.

To change the selection after state creation, call `state.selectDate(LocalDate(...))`.

Invalid custom item values throw `IllegalArgumentException` during composition. If the current or restored year/month is valid but not present in a custom list, the picker starts from the first item in that list and normalizes the state.

### YearMonthPicker

| Parameter | Description | Default |
| :--- | :--- | :--- |
| `state` | The state object to control the picker. | `rememberYearMonthPickerState()` |
| `startLocalDate` | Legacy compatibility parameter. It does not initialize or update `state`, even when `state` is omitted; use `rememberYearMonthPickerState(initialDate = ...)` or explicit initial values instead. | `currentDate()` |
| `yearItems` | List of years available for selection. Values must be in `1000..9999`. | `1000..9999` |
| `monthItems` | List of months available for selection. Values must be in `1..12`. | `1..12` |
| `visibleItemsCount` | Number of items visible in the list. | `3` |
| `colors` | Colors for text, selected text, dividers, and selected item background. | `PickerDefaults.colors()` |
| `textStyles` | Text styles for selected and unselected items. | `PickerDefaults.textStyles()` |
| `yearPickerLabel` | Accessibility label for the year picker. Pass `null` to omit the picker label prefix. | `"Year"` |
| `monthPickerLabel` | Accessibility label for the month picker. Pass `null` to omit the picker label prefix. | `"Month"` |
| `yearItemContentDescription` | Accessibility description for each year value. | `it.toString()` |
| `monthItemContentDescription` | Accessibility description for each month value. | `it.toString()` |

**YearMonthPickerState Properties:**

- `selectedYear`: The currently selected year.
- `selectedMonth`: The currently selected month (1-12).
- `selectedMonthDate`: The selected year/month represented as the first day of that month.

`rememberYearMonthPickerState` uses saveable state. On Android, selected values can be restored across Activity recreation when the platform saveable registry is available.

For initial values, use either `rememberYearMonthPickerState(initialDate = LocalDate(...))` or the explicit `initialYear`/`initialMonth` parameters. Initial years must be in `1000..9999`. The `startLocalDate` component parameter is retained only for source compatibility and is not used.

To change the selection after state creation, call `state.selectYearMonth(year, month)` or `state.selectDate(LocalDate(...))`.

Invalid custom item values throw `IllegalArgumentException` during composition. If the current or restored year/month is valid but not present in a custom list, the picker starts from the first item in that list and normalizes the state.

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
