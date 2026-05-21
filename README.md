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
*   **Customizable**: Extensible API with `PickerStyle` for reusable visual configuration.
* **State Management**: simplified state handling with `rememberTimePickerState`,
  `rememberDatePickerState`, and `rememberYearMonthPickerState`.
*   **Accessibility**: Built with accessibility in mind, supporting screen readers and navigation.

## Sample App

The repository includes a Compose Multiplatform sample app with copyable date, time, and bottom sheet flows.

<p align="center">
  <img src="docs/images/sample/sample-home.png" alt="Sample app home screen" width="23%" />
  <img src="docs/images/sample/sample-date-picker.png" alt="DatePicker sample screen" width="23%" />
  <img src="docs/images/sample/sample-time-picker.png" alt="TimePicker sample screen" width="23%" />
  <img src="docs/images/sample/sample-bottom-sheet.png" alt="Bottom sheet picker sample screen" width="23%" />
</p>

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

> **Release status:** `0.4.0` is the latest public Maven Central/GitHub Releases version. This README is maintained from `main` and documents unreleased `0.6.0` API work, so the Usage and API Reference sections may include APIs that are not available in `0.4.0`. For published APIs, use the `0.4.0` release/tag docs. To test `main` locally, run `./gradlew :datetimepicker:publishToMavenLocal`, add `mavenLocal()` to your consuming build, and depend on `0.6.0`.

For release notes and upgrade-impact details, see [CHANGELOG.md](CHANGELOG.md).

## Usage

> The examples below target the current `main` branch API. They may require unreleased `0.6.0` APIs rather than the public `0.4.0` dependency shown above.

### TimePicker

Use `TimePicker` for time selection. It supports both 12-hour and 24-hour formats.

#### 1. 24-Hour Format

```kotlin
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.kez.picker.time.TimePicker
import com.kez.picker.time.rememberTimePickerState
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
        state = state,
        onSelectedTimeChange = { selectedTime ->
            // Update app state, ViewModel, or form data here.
        }
    )

    // Use state.selectedTime when passing the result to app logic.
}
```

#### 2. 12-Hour Format (AM/PM)

```kotlin
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.kez.picker.time.TimePicker
import com.kez.picker.time.rememberTimePickerState
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
import com.kez.picker.PickerDefaults
import com.kez.picker.date.DatePicker
import com.kez.picker.date.rememberDatePickerState
import com.kez.picker.util.currentDate

@Composable
fun DatePickerExample() {
    val initialDate = remember { currentDate() }
    val selectableYears = remember(initialDate.year) {
        ((initialDate.year - 1)..(initialDate.year + 1)).toList()
    }
    val state = rememberDatePickerState(
        initialDate = initialDate
    )

    DatePicker(
        state = state,
        onSelectedDateChange = { selectedDate ->
            // Update app state, ViewModel, or form data here.
        },
        items = PickerDefaults.datePickerItems(
            yearItems = selectableYears
        )
    )

    // Use state.selectedDate when passing the result to app logic.
}
```

When you restrict selectable item lists with `PickerDefaults.*Items(...)`, keep the remembered
initial or restored state value inside those lists. If an external date changes after composition, call
`state.selectDate(newDate)` instead of relying on a new `initialDate` argument.

### YearMonthPicker

Use `YearMonthPicker` for selecting a specific month in a year.

```kotlin
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.kez.picker.date.YearMonth
import com.kez.picker.date.YearMonthPicker
import com.kez.picker.date.rememberYearMonthPickerState
import com.kez.picker.util.currentDate

@Composable
fun YearMonthPickerExample() {
    val initialDate = remember { currentDate() }
    val state = rememberYearMonthPickerState(
        initialDate = initialDate
    )

    YearMonthPicker(
        state = state,
        onSelectedYearMonthChange = { selectedYearMonth: YearMonth ->
            // Update app state, ViewModel, or form data here.
        }
    )

    // state.selectedYearMonth is YearMonth(year, month).
    // state.selectedMonthDate is still available for LocalDate interoperability.
}
```

### BottomSheet Integration

The pickers work seamlessly within a `ModalBottomSheet` or other dialog components. Keep the
committed value separate from the temporary sheet state so dismissing the sheet does not accidentally
change app state.

```kotlin
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kez.picker.time.rememberTimePickerState
import com.kez.picker.time.TimePicker
import kotlinx.datetime.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetPickerExample() {
    var committedHour by rememberSaveable { mutableIntStateOf(9) }
    var committedMinute by rememberSaveable { mutableIntStateOf(30) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val committedTime = LocalTime(committedHour, committedMinute)

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Selected time: $committedTime")

        Button(onClick = { showBottomSheet = true }) {
            Text("Select Time")
        }
    }

    if (showBottomSheet) {
        val draftState = rememberTimePickerState(initialTime = committedTime)

        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TimePicker(state = draftState)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { showBottomSheet = false },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            val selected = draftState.selectedTime
                            committedHour = selected.hour
                            committedMinute = selected.minute
                            showBottomSheet = false
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Apply")
                    }
                }
            }
        }
    }
}
```

The example stores hour and minute separately because primitive values work with `rememberSaveable`;
`LocalTime` is recreated as a derived value before creating the draft picker state.

## API Reference

> This reference describes the current `main` branch API. Check [CHANGELOG.md](CHANGELOG.md) and the `0.4.0` release/tag docs before copying API examples into a project that depends on the public `0.4.0` artifact.

Public state APIs live beside their components: `TimePicker`, `TimePickerState`, and
`rememberTimePickerState` are in `com.kez.picker.time`; `DatePicker`, `DatePickerState`,
`YearMonthPicker`, `YearMonthPickerState`, and their `remember*State` functions are in
`com.kez.picker.date`.

Accessibility options customize the picker-column prefix, accessibility value text, and previous/next
accessibility action labels without changing the visual item text. Selection is exposed through Compose
`selected` semantics rather than appended as a hardcoded English phrase. Use
`PickerDefaults.accessibility(...)`, `timePickerAccessibility(...)`, `datePickerAccessibility(...)`, or
`yearMonthPickerAccessibility(...)` to create reusable localized accessibility objects.

```kotlin
TimePicker(
    state = state,
    accessibility = PickerDefaults.timePickerAccessibility(
        hourPickerLabel = "Hour",
        minutePickerLabel = "Minute",
        hourItemContentDescription = { "$it hour" },
        minuteItemContentDescription = { "$it minute" },
        previousItemActionLabel = "Select previous value",
        nextItemActionLabel = "Select next value"
    )
)
```

### Generic Picker

Use `Picker<T>` when you need a single custom picker column.

```kotlin
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberSaveable
import androidx.compose.runtime.setValue
import com.kez.picker.Picker
import com.kez.picker.PickerDefaults

@Composable
fun SizePickerExample() {
    val items = listOf("Small", "Medium", "Large")
    var selectedSize by rememberSaveable { mutableStateOf("Medium") }

    Picker(
        items = items,
        selectedItem = selectedSize,
        onSelectedItemChange = { selectedSize = it },
        isInfinity = false,
        accessibility = PickerDefaults.accessibility(
            pickerLabel = "Size",
            itemContentDescription = { it }
        )
    )
}
```

`Picker<T>` is a controlled component. Keep the selected value in app state, pass it through
`selectedItem`, and update that state from `onSelectedItemChange`. `items` must be non-empty and
distinct, and `selectedItem` must exist in `items`. If `T` is not saveable, store a saveable key in
your app state and map that key back to an item before rendering the picker.

Use `style = PickerDefaults.style(...)` to customize visible item count, colors, text styles,
dividers, item padding, selected item background, and fading edge behavior with one reusable object.

### Programmatic Selection

Create picker state with `remember*State`, pass it to the picker, then call the public selection method
from an event handler or a `LaunchedEffect(externalValue)`. Do not recreate the state just to reset the
selection.

| State | Method |
| :--- | :--- |
| Generic `Picker<T>` | Update the app-owned `selectedItem` value |
| `time.TimePickerState` | `selectTime(LocalTime(...))` |
| `date.DatePickerState` | `selectDate(LocalDate(...))` |
| `date.YearMonthPickerState` | `selectYearMonth(YearMonth(...))`, `selectYearMonth(year, month)`, or `selectDate(LocalDate(...))` |

```kotlin
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.kez.picker.time.rememberTimePickerState
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

The picker scroll position is synchronized when the current item lists contain the requested values. Custom
item lists are strict: they must be non-empty, distinct, within the supported value ranges, and contain the
current selected value. If an app can restore or request values outside a custom list, clamp or reject that
app state before rendering the picker.

`onSelectedTimeChange`, `onSelectedDateChange`, and `onSelectedYearMonthChange` are called for
user-driven picker changes. Programmatic `state.select*` calls update the state directly; update your
app-owned value in the same event handler when you trigger programmatic changes.

### TimePicker

| Parameter | Description | Default |
| :--- | :--- | :--- |
| `state` | The state object to control the picker. | `rememberTimePickerState()` |
| `onSelectedTimeChange` | Called after user interaction changes the selected `LocalTime`. | `{}` |
| `items` | Selectable minute, 24-hour hour, 12-hour display-hour, and AM/PM item lists. | `PickerDefaults.timePickerItems()` |
| `style` | Visual and layout styling for each picker column. | `PickerDefaults.style()` |
| `spacingBetweenPickers` | Horizontal spacing between picker columns. | `0.dp` |
| `accessibility` | Accessibility labels, item descriptions, and custom action labels for each picker column. | `PickerDefaults.timePickerAccessibility()` |

**TimePickerState Properties:**

- `selectedHour`: The selected hour shown by the picker.
- `selectedMinute`: The selected minute (0-59).
- `selectedPeriod`: The selected AM/PM period when using 12-hour format.
- `selectedHourOfDay`: The selected hour converted to 24-hour clock time (0-23).
- `selectedTime`: The selected value as `kotlinx.datetime.LocalTime`.

`rememberTimePickerState` uses saveable state. On Android, selected values can be restored across Activity recreation when the platform saveable registry is available.

For initial values, use either `rememberTimePickerState(initialTime = LocalTime(...))` or the explicit `initialHour`/`initialMinute` parameters.

To change the selection after state creation, call `state.selectTime(LocalTime(...))`.

Invalid custom item values, duplicate items, empty required lists, or current selections missing from custom lists throw `IllegalArgumentException` during composition. In 12-hour mode, `PickerDefaults.timePickerItems(hour12Items = ...)` uses display-hour values (`1..12`): `initialHour = 13` becomes `state.selectedHour == 1` with `PM`.

### DatePicker

| Parameter           | Description                             | Default                     |
|:--------------------|:----------------------------------------|:----------------------------|
| `state`             | The state object to control the picker. | `rememberDatePickerState()` |
| `onSelectedDateChange` | Called after user interaction changes the selected `LocalDate`. | `{}` |
| `items`             | Selectable year and month item lists. Values must be in `1000..9999` and `1..12`. | `PickerDefaults.datePickerItems()` |
| `style`             | Visual and layout styling for each picker column. | `PickerDefaults.style()` |
| `spacingBetweenPickers` | Horizontal spacing between picker columns. | `0.dp` |
| `accessibility` | Accessibility labels, item descriptions, and custom action labels for each picker column. | `PickerDefaults.datePickerAccessibility()` |

**DatePickerState Properties:**

- `selectedYear`: The currently selected year.
- `selectedMonth`: The currently selected month (1-12).
- `selectedDay`: The currently selected day (1-31, auto-adjusted based on month).
- `selectedDate`: The selected value as `kotlinx.datetime.LocalDate`.
- `maxDay`: The maximum valid day for the selected year and month.

`rememberDatePickerState` uses saveable state. On Android, selected values can be restored across Activity recreation when the platform saveable registry is available.

For initial values, use either `rememberDatePickerState(initialDate = LocalDate(...))` or the
explicit `initialYear`/`initialMonth`/`initialDay` parameters. Initial years must be in
`1000..9999`, months in `1..12`, and days must be at least `1`. If `initialDay` is greater than
the maximum valid day for the initial year/month, it is clamped to that maximum.

To change the selection after state creation, call `state.selectDate(LocalDate(...))`.

Invalid custom item values, duplicate items, empty lists, or current selected year/month values missing from custom lists throw `IllegalArgumentException` during composition.

### YearMonthPicker

| Parameter | Description | Default |
| :--- | :--- | :--- |
| `state` | The state object to control the picker. | `rememberYearMonthPickerState()` |
| `onSelectedYearMonthChange` | Called after user interaction changes the selected `YearMonth`. | `{}` |
| `items` | Selectable year and month item lists. Values must be in `1000..9999` and `1..12`. | `PickerDefaults.yearMonthPickerItems()` |
| `style` | Visual and layout styling for each picker column. | `PickerDefaults.style()` |
| `spacingBetweenPickers` | Horizontal spacing between picker columns. | `0.dp` |
| `accessibility` | Accessibility labels, item descriptions, and custom action labels for each picker column. | `PickerDefaults.yearMonthPickerAccessibility()` |

**YearMonthPickerState Properties:**

- `selectedYear`: The currently selected year.
- `selectedMonth`: The currently selected month (1-12).
- `selectedYearMonth`: The selected value as `date.YearMonth`.
- `selectedMonthDate`: The selected year/month represented as the first day of that month.

`rememberYearMonthPickerState` uses saveable state. On Android, selected values can be restored across Activity recreation when the platform saveable registry is available.

For initial values, use either `rememberYearMonthPickerState(initialDate = LocalDate(...))` or the explicit `initialYear`/`initialMonth` parameters. Initial years must be in `1000..9999`.

To change the selection after state creation, call `state.selectYearMonth(YearMonth(...))`,
`state.selectYearMonth(year, month)`, or `state.selectDate(LocalDate(...))`.

Invalid custom item values, duplicate items, empty lists, or current selected year/month values missing from custom lists throw `IllegalArgumentException` during composition.

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
