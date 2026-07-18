# Compose DateTimePicker

[![Read in Korean](https://img.shields.io/badge/README-Korean-green)](./README_KO.md)

A controlled wheel selection and temporal picker library for Compose Multiplatform. It provides a
generic `WheelPicker<T>` plus date/time presets with consistent APIs across Android, iOS, Desktop
(JVM), and Web (Wasm).

## Features

*   **Multiplatform Support**: seamless integration for Android, iOS, Desktop (JVM), and Web (Wasm).
* **WheelPicker**: A controlled generic wheel with live value changes and a separate settled callback.
*   **TimePicker**: Supports both 12-hour (AM/PM) and 24-hour formats.
* **DurationPicker**: Selects one bounded elapsed duration through dependent hour/minute columns.
* **Quantity + Unit repository sample only — not artifact API**: Demonstrates a non-temporal unit
  change replacing quantity items, step, formatting, and semantics while committing one repaired
  logical value.
* **Date + Time repository sample only — not artifact API**: Demonstrates five dependent columns
  committing one exact `LocalDateTime` across a midnight boundary.
* **DatePicker**: A complete date picker for selecting year, month, and day with automatic day
  validation.
* **DateRangePicker**: An ordered start/end date picker for booking, filtering, and reporting flows.
*   **YearMonthPicker**: A dedicated component for selecting years and months.
*   **Customizable**: Extensible API with `PickerStyle` and format options for reusable UI configuration.
* **State Management**: simplified state handling with `rememberTimePickerState`,
  `rememberDurationPickerState`, `rememberDatePickerState`, `rememberDateRangePickerState`, and
  `rememberYearMonthPickerState`.
*   **Accessibility**: Built with accessibility semantics in mind, supporting screen readers and navigation.

## Sample App

The repository includes a Compose Multiplatform sample app with inspectable generic wheel,
dependent quantity/unit, combined date-time, date, time, duration, and bottom sheet flows.

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

> **Release status:** `0.6.0` is the latest public Maven Central version. GitHub Releases may still show `0.4.0` as the latest tagged release. This README is maintained from `main`, so Usage and API Reference sections can include APIs that are not in the public `0.6.0` artifact yet. To test `main` locally, run `./gradlew :datetimepicker:publishToMavenLocal`, add `mavenLocal()` to your consuming build, and depend on the repository `VERSION_NAME`.

For release notes and upgrade-impact details, see [CHANGELOG.md](CHANGELOG.md).

## Usage

> The examples below target the current `main` branch API. They may require post-`0.6.0` APIs that are not available in the public Maven Central artifact yet.

### State and Callback Pattern

For `TimePicker`, `DurationPicker`, `DatePicker`, `DateRangePicker`, and `YearMonthPicker`, create the picker state
once with `remember*State(...)` and pass that same state object to the composable. Inside the picker
UI, that state object is the source of truth for the currently selected value.

- Read the current value from `state.selectedTime`, `state.selectedDuration`, `state.selectedDate`,
  `state.selectedDateRange`, or `state.selectedYearMonth`.
- Use `onSelected*Change` when you need to mirror user-driven picker changes into app state,
  a `ViewModel`, or form data.
- `TimePicker`, `DurationPicker`, and `DatePicker` dispatch that callback once after the changed column settles and all
  dependent values are repaired. The callback receives the final selectable logical value, which is
  already committed to the picker state; visual `columnOrder` does not change that transition.
- A changed column value is preserved only while it still belongs to the active constrained source.
  A late value that is no longer selectable in that source is ignored. If an upstream settle replaces
  a dependent source while that child wheel is moving, the invalidated child interaction is cancelled
  without a callback.
- `onSelected*Change` is not called when your app calls `state.select*` programmatically. If a
  button, preset, or external value changes the picker, call `state.select*(...)` and update your
  app-owned value in the same handler.
- Do not recreate `remember*State` just to reset the picker. Keep the state instance and call its
  public `select*` method.

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

#### 3. Constrained Hours

Use `PickerDefaults.timePickerItems(minTime = ..., maxTime = ...)` when a form should only allow an
inclusive time range. Create state with the same `items` object so restored or preset values are
coerced before the picker renders.

```kotlin
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.kez.picker.PickerDefaults
import com.kez.picker.time.TimePicker
import com.kez.picker.time.rememberTimePickerState
import kotlinx.datetime.LocalTime

@Composable
fun BusinessHoursTimePickerExample() {
    val items = remember {
        PickerDefaults.timePickerItems(
            minuteItems = listOf(0, 15, 30, 45),
            minTime = LocalTime(8, 0),
            maxTime = LocalTime(18, 0)
        )
    }
    val state = rememberTimePickerState(
        items = items,
        initialHour = 7,
        initialMinute = 30
    )

    TimePicker(
        state = state,
        items = items
    )
}
```

### DurationPicker

Use `DurationPicker` when hour and minute columns must commit one bounded elapsed duration. This
example permits `0..90` minutes in five-minute steps. Create state with the same `items` object so
initial and restored values use the same scalar coercion rules. For an app-driven preset, call
`state.selectDuration(value, items)` to apply those rules before rendering.

```kotlin
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.kez.picker.PickerDefaults
import com.kez.picker.duration.DurationPicker
import com.kez.picker.duration.rememberDurationPickerState
import kotlin.time.Duration.Companion.minutes

@Composable
fun WorkoutDurationExample() {
    val items = remember {
        PickerDefaults.durationPickerItems(
            hourItems = listOf(0, 1),
            minuteItems = (0..55 step 5).toList(),
            minDuration = 0.minutes,
            maxDuration = 90.minutes
        )
    }
    val state = rememberDurationPickerState(
        items = items,
        initialDuration = 45.minutes
    )

    DurationPicker(
        state = state,
        items = items,
        onSelectedDurationChange = { duration ->
            // The repaired value is already committed to state.selectedDuration.
        }
    )
}
```

`DurationPicker` uses finite, non-negative, whole-minute `kotlin.time.Duration` values. Its hour
column represents elapsed whole hours rather than time-of-day. A changed hour preserves the current
minute when possible, then repairs it to the closest selectable minute inside the inclusive scalar
bounds. Numeric ties prefer the smaller duration. Negative, infinite, or sub-minute state and bound
values throw `IllegalArgumentException` instead of being silently truncated.

### Quantity + Unit dependent sample

The sample app includes a sample-local `QuantityUnitPicker` vertical slice for coarse allowed-weight
buckets. It is not yet a supported library API. The slice intentionally validates the
core source-repair and state-first callback path before the repository proposes a generic
multi-column wheel engine API or a domain-specific quantity preset.

- grams use `100..5000` in 100 g steps;
- kilograms use `1..5` in 1 kg steps;
- changing the unit replaces the quantity source, value formatting, and accessibility description;
- `2500 g` repairs to `2 kg` because equal-distance ties choose the smaller normalized mass;
- user-settled changes commit state before one callback, while programmatic presets dispatch none.

The deliberately coarse integer grids make repair behavior easy to inspect; this is a constrained
selection example, not a precision unit converter. See the reference implementation's
[state and constraint contract](sample/src/commonMain/kotlin/com/kez/picker/sample/ui/screen/quantity/QuantityUnitPickerContract.kt),
[picker composable](sample/src/commonMain/kotlin/com/kez/picker/sample/ui/screen/quantity/QuantityUnitPicker.kt),
and [sample screen](sample/src/commonMain/kotlin/com/kez/picker/sample/ui/screen/QuantityUnitPickerSampleScreen.kt).
The first two files contain the core reference; the screen uses repository-specific presentation
components and is not a standalone copy target.
This sample is evidence for the future engine API, not a promise that mass conversion belongs in the
core picker library.

### Date + Time dependent sample

The sample app also contains a sample-local five-column `DateTimePicker`. It is not a supported
artifact API. Its six exact whole-minute candidates cross midnight from `2026-02-28 23:00` to
`2026-03-01 01:30`.

- year, month, day, hour, and minute are derived from one `LocalDateTime`;
- changing February to March replaces the downstream day/hour/minute sources;
- `2026-02-28 23:30` repairs to the closest candidate `2026-03-01 00:00` in one state commit and
  callback;
- programmatic selection and restore coerce without dispatching a user callback;
- an Android race test verifies that an in-flight, still-selectable minute target cannot overwrite
  a later programmatic selection.

See the core reference's
[state and exact-candidate contract](sample/src/commonMain/kotlin/com/kez/picker/sample/ui/screen/datetime/DateTimePickerContract.kt),
[picker composable](sample/src/commonMain/kotlin/com/kez/picker/sample/ui/screen/datetime/DateTimePicker.kt),
and the repository-specific [sample screen](sample/src/commonMain/kotlin/com/kez/picker/sample/ui/screen/DateTimePickerSampleScreen.kt).
The first two files are the core reference; the screen depends on repository-specific presentation
components and is not a standalone copy target.
This bounded stress case adds repository evidence for a future multi-column engine. It is not a
production date-time API, timezone model, first-use result, or market-demand proof.

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
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

@Composable
fun DatePickerExample() {
    val initialDate = remember { currentDate() }
    val minDate = remember(initialDate.year) {
        LocalDate(initialDate.year, 1, 1)
    }
    val maxDate = remember(initialDate.year) {
        LocalDate(initialDate.year + 1, 12, 31)
    }
    val selectableYears = remember(minDate.year, maxDate.year) {
        (minDate.year..maxDate.year).toList()
    }
    val selectableDays = remember(initialDate.day) {
        listOf(1, 15, initialDate.day).distinct().sorted()
    }
    val items = remember(selectableYears, selectableDays, minDate, maxDate) {
        PickerDefaults.datePickerItems(
            yearItems = selectableYears,
            dayItems = selectableDays,
            minDate = minDate,
            maxDate = maxDate
        )
    }
    val state = rememberDatePickerState(
        items = items,
        initialYear = initialDate.year,
        initialMonth = initialDate.month.number,
        initialDay = initialDate.day
    )

    DatePicker(
        state = state,
        onSelectedDateChange = { selectedDate ->
            // Update app state, ViewModel, or form data here.
        },
        items = items
    )

    // Use state.selectedDate when passing the result to app logic.
}
```

When you restrict selectable item lists or date bounds with `PickerDefaults.*Items(...)`, keep the
remembered initial or restored state value inside those rules, or create state with
`rememberDatePickerState(items = items, initialDate = value)` or
`rememberDatePickerState(items = items, initialYear = year, initialMonth = month, initialDay = day)`
to coerce it before first composition. If an external date changes after composition, call
`state.selectDate(newDate, items)` or `state.selectDate(year, month, day, items)` instead of relying
on new initial arguments.

### DateRangePicker

Use `DateRangePicker` when users need to select an ordered start and end date.

```kotlin
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.kez.picker.PickerDefaults
import com.kez.picker.date.DateRange
import com.kez.picker.date.DateRangePicker
import com.kez.picker.date.rememberDateRangePickerState
import com.kez.picker.util.currentDate
import kotlinx.datetime.LocalDate

@Composable
fun DateRangePickerExample() {
    val today = remember { currentDate() }
    val todayRange = remember(today) {
        DateRange(startDate = today, endDate = today)
    }
    val items = remember(today.year) {
        PickerDefaults.datePickerItems(
            yearItems = listOf(today.year),
            minDate = LocalDate(today.year, 1, 1),
            maxDate = LocalDate(today.year, 12, 31)
        )
    }
    val state = rememberDateRangePickerState(
        items = items,
        initialDateRange = todayRange
    )

    DateRangePicker(
        state = state,
        items = items,
        onSelectedDateRangeChange = { selectedRange: DateRange ->
            // Update app state, ViewModel, or form data here.
        }
    )
}
```

### YearMonthPicker

Use `YearMonthPicker` for selecting a specific month in a year.

```kotlin
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.kez.picker.PickerDefaults
import com.kez.picker.date.YearMonth
import com.kez.picker.date.YearMonthPicker
import com.kez.picker.date.rememberYearMonthPickerState
import com.kez.picker.util.currentDate

@Composable
fun YearMonthPickerExample() {
    val initialYearMonth = remember { YearMonth.from(currentDate()) }
    val minYearMonth = initialYearMonth
    val maxYearMonth = remember {
        YearMonth(year = initialYearMonth.year + 1, month = initialYearMonth.month)
    }
    val items = remember {
        PickerDefaults.yearMonthPickerItems(
            minYearMonth = minYearMonth,
            maxYearMonth = maxYearMonth
        )
    }
    val state = rememberYearMonthPickerState(
        items = items,
        initialYearMonth = initialYearMonth
    )

    YearMonthPicker(
        state = state,
        items = items,
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
import androidx.compose.runtime.saveable.rememberSaveable
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

> This reference describes the current `main` branch API. Check [CHANGELOG.md](CHANGELOG.md) before copying API examples into a project that depends on the public `0.6.0` artifact.

Public state APIs live beside their components: `TimePicker`, `TimePickerState`, and
`rememberTimePickerState` are in `com.kez.picker.time`; `DurationPicker`, `DurationPickerState`, and
`rememberDurationPickerState` are in `com.kez.picker.duration`; `DatePicker`, `DatePickerState`,
`YearMonthPicker`, `YearMonthPickerState`, and their `remember*State` functions are in
`com.kez.picker.date`.

Format options customize visible item text and optional accessibility value descriptions. If an
item-specific content description is omitted, the picker uses the visible item text as the default
accessibility value. This prevents visible text and screen-reader values from silently diverging, but
apps should still provide explicit descriptions when TalkBack should read a more natural phrase such
as "1 hour", "January", or "PM".

Semantics options customize the structural picker-column label and previous/next action labels.
Selection is exposed through Compose `selected` semantics rather than appended as a hardcoded English
phrase. Use `PickerDefaults.itemFormat(...)` on a generic `Picker<T>`, or
`PickerDefaults.timePickerFormat(...)`, `durationPickerFormat(...)`, `datePickerFormat(...)`, and
`yearMonthPickerFormat(...)` for composite picker values. Use `PickerDefaults.semantics(...)`,
`timePickerSemantics(...)`, `durationPickerSemantics(...)`, `datePickerSemantics(...)`, or
`yearMonthPickerSemantics(...)` for reusable localized labels and actions.

```kotlin
TimePicker(
    state = state,
    format = PickerDefaults.timePickerFormat(
        hourItemText = { it.toString().padStart(2, '0') },
        minuteItemText = { it.toString().padStart(2, '0') },
        hourItemContentDescription = { "$it hour" },
        minuteItemContentDescription = { "$it minute" }
    ),
    semantics = PickerDefaults.timePickerSemantics(
        hourPickerLabel = "Hour",
        minutePickerLabel = "Minute",
        previousItemActionLabel = "Select previous value",
        nextItemActionLabel = "Select next value"
    )
)
```

### Generic WheelPicker

Use `WheelPicker<T>` when you need a controlled single custom wheel column.

```kotlin
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.kez.picker.PickerDefaults
import com.kez.picker.WheelPicker

@Composable
fun SizePickerExample() {
    val items = listOf("Small", "Medium", "Large")
    var selectedSize by rememberSaveable { mutableStateOf("Medium") }

    WheelPicker(
        items = items,
        selectedItem = selectedSize,
        onSelectedItemChange = { selectedSize = it },
        onSelectionSettled = { size ->
            // Commit the preview or start expensive work with size.
        },
        enabled = true,
        isInfinity = false,
        format = PickerDefaults.itemFormat(
            itemText = { size -> size.uppercase() },
            itemContentDescription = { it }
        ),
        semantics = PickerDefaults.semantics(
            pickerLabel = "Size"
        )
    )
}
```

`WheelPicker<T>` is a controlled component. Keep the selected value in app state, pass it through
`selectedItem`, and update it synchronously from `onSelectedItemChange`. That callback runs live as
user scrolling, item clicks, or semantics actions move a different item into the center.
`onSelectionSettled` runs once after an interaction that changed the centered item stops, so it is
the appropriate place to start an expensive query or commit a preview. App-driven `selectedItem`
changes move the wheel without invoking either callback.

The existing `Picker<T>` remains available for compatibility. Its `onSelectedItemChange` callback
runs only after interaction settles; it does not expose live centered-item changes. Composite
temporal pickers continue to use this settled contract so dependent columns do not rebuild while a
neighboring column is still scrolling.

For both APIs, `items` must be non-empty and distinct, and `selectedItem` must exist in `items`. If
`items` can change, update or coerce the
app-owned `selectedItem` to one of the new values before rendering the picker. Treat `items` as
immutable while the picker is composed; create and pass a new list when available values change. If `T` is not
saveable, store a saveable key in your app state and map that key back to an item before rendering
the picker.
Pass `enabled = false` to prevent user scroll, click, and semantics selection actions while still
showing the current value. Disabled pickers use the disabled slots from `PickerDefaults.colors(...)`
for default text, dividers, and selected-item backgrounds.

Custom `content` receives `PickerItemScope<T>` so custom rows can reuse the default formatted text,
selected/enabled state, distance fraction, text style, and content color:

```kotlin
WheelPicker(
    items = items,
    selectedItem = selectedSize,
    onSelectedItemChange = { selectedSize = it },
    content = { item ->
        Text(
            text = if (item.isSelected) "[${item.text}]" else item.text,
            style = item.textStyle,
            color = item.contentColor
        )
    }
)
```

Use `style = PickerDefaults.style(...)` to customize visible item count, colors, text styles,
dividers, item padding, selected item background, and fading edge behavior with one reusable object.
Use `format.itemText` for visible text and `format.itemContentDescription` for screen-reader value
text when those two strings should differ.

`PickerStyle` groups the visual settings that can be shared across `WheelPicker`, `Picker`, and
composite pickers:

| Option | Use it for |
| :--- | :--- |
| `visibleItemsCount` | Number of rows visible in the wheel. |
| `colors` | Default/selected/disabled text colors, divider colors, and selected-item background colors. |
| `textStyles` | Default and selected text styles. |
| `selectedItemBackgroundShape` | Shape of the selected item background. |
| `itemPadding` | Padding applied around each rendered item. |
| `fadingEdgeGradient` | Top/bottom fading edge mask. |
| `horizontalAlignment` | Horizontal alignment of item content inside each column. |
| `dividerThickness`, `dividerShape`, `dividerWidth`, `isDividerVisible` | Standalone `WheelPicker` / `Picker` selection divider settings. Composite pickers use `selectionIndicator` for the shared band. |

For a standalone `Picker`, control the selection divider length with `dividerWidth`. Use
`PickerDividerWidth.Fill` (default) to span the full column width, `PickerDividerWidth.Fraction(0f..1f)`
for a proportional length, or `PickerDividerWidth.Fixed(Dp)` for an absolute length. The divider stays
centered horizontally. `Fraction` accepts only values in `0f..1f`; `Fixed` width must be a finite,
non-negative `Dp`.

```kotlin
WheelPicker(
    items = items,
    selectedItem = selectedItem,
    onSelectedItemChange = { selectedItem = it },
    style = PickerDefaults.style(dividerWidth = PickerDividerWidth.Fraction(0.6f))
)
```

Composite pickers (`TimePicker`, `DatePicker`, `YearMonthPicker`, `DateRangePicker`) draw a **single
selection band** spanning the whole picker instead of one divider per column, so the selection lines
stay aligned regardless of column widths and column spacing. Control it with `selectionIndicator`
rather than the per-column `style` divider settings (which do not apply to composites). The default
`selectionIndicator` is derived from `style`, so existing `dividerColor` / `dividerThickness` /
`disabledDividerColor` / `isDividerVisible` customizations still take effect. Use `horizontalInset`
to inset the band from the picker edges. `thickness` and `horizontalInset` must be finite,
non-negative `Dp` values.

`PickerSelectionIndicator` keeps the composite band settings separate from per-column item styling:

| Option | Use it for |
| :--- | :--- |
| `color` | Selection band line color while the picker is enabled. |
| `disabledColor` | Selection band line color while the picker is disabled. |
| `thickness` | Thickness of each band line. Must be a finite, non-negative `Dp`. |
| `shape` | Shape of each band line. |
| `horizontalInset` | Inset applied to both horizontal edges of the band. Must be a finite, non-negative `Dp`. |
| `isVisible` | Whether the band is drawn. |

```kotlin
TimePicker(
    selectionIndicator = PickerDefaults.selectionIndicator(horizontalInset = 16.dp),
)
```

When a custom `PickerStyle` should drive the band defaults, pass that style explicitly:

```kotlin
val style = PickerDefaults.style(
    colors = PickerDefaults.colors(
        dividerColor = Color(0xFF1565C0),
        disabledDividerColor = Color(0x551565C0),
    ),
)

TimePicker(
    style = style,
    selectionIndicator = PickerDefaults.selectionIndicator(style = style),
)
```

### Programmatic Selection

Create picker state with `remember*State`, pass it to the picker, then call the public selection method
from an event handler or a `LaunchedEffect(externalValue)`. `LaunchedEffect` is suitable while the
active item source still contains the current value. When replacing an item source or constraints,
coerce the state with the replacement source before publishing that source to the picker. Do not
recreate the state just to reset the selection.

| State | Method |
| :--- | :--- |
| Generic `WheelPicker<T>` / `Picker<T>` | Update the app-owned `selectedItem` value |
| `time.TimePickerState` | `selectTime(LocalTime(...))`, `selectTime(hour, minute)`, or the matching `items` overloads |
| `duration.DurationPickerState` | `selectDuration(Duration)`, `selectDuration(hours, minutes)`, or the matching `items` overloads |
| `date.DatePickerState` | `selectDate(LocalDate(...))`, `selectDate(year, month, day)`, or the matching `items` overloads |
| `date.DateRangePickerState` | `selectDateRange(DateRange(...))`, `selectDateRange(startDate, endDate)`, `selectDateRange(startYear, startMonth, startDay, endYear, endMonth, endDay)`, `selectStartDate(...)`, `selectEndDate(...)`, or the matching `items` overloads |
| `date.YearMonthPickerState` | `selectYearMonth(YearMonth(...))`, `selectYearMonth(year, month)`, `selectDate(LocalDate(...))`, or the matching `items` overloads |

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
        Button(onClick = { state.selectTime(hour = 9, minute = 30) }) {
            Text("Set 09:30")
        }

        TimePicker(state = state)
    }
}
```

For a dynamic item-source replacement, update the logical state and source in that order in the same
event handler:

```kotlin
fun replaceItems(newItems: TimePickerItems, requestedTime: LocalTime) {
    state.selectTime(requestedTime, newItems)
    items = newItems
}
```

The picker scroll position is synchronized when the current item lists contain the requested values. Custom
item lists are strict: they must be non-empty, distinct, within the supported value ranges, and contain the
current selected value. `TimePicker` filters hour, minute, and AM/PM columns through optional
`minTime`/`maxTime` bounds. `DurationPicker` filters hour/minute combinations through optional scalar
`minDuration`/`maxDuration` bounds. `DatePicker` filters `dayItems` by the selected year/month maximum day and
optional `minDate`/`maxDate` bounds. If an app can restore or request values outside a custom list or
configured bounds, call `items.contains(...)` to check primitive or value objects before rejecting a
value, or call the `state.select*(value, items)` overload or `items.coerce*` helper to move to the
closest selectable value before rendering the picker.
For first composition, use `remember*State(items = items, initial... = value)` to apply the same coercion
before the picker is rendered. These items-aware state overloads also coerce a saved value against the
items supplied by the recreated composition, so changed constraints cannot restore an invalid logical
value.

`onSelectedTimeChange`, `onSelectedDurationChange`, `onSelectedDateChange`,
`onSelectedDateRangeChange`, and `onSelectedYearMonthChange` are called for user-driven picker
changes. Programmatic `state.select*`
calls update the state directly; update your app-owned value in the same event handler when you
trigger programmatic changes. `TimePicker`, `DurationPicker`, and `DatePicker` wait for the changed child wheel to
settle, repair dependent columns against the active constraints, commit one logical value, and then
invoke the callback once. A compatible item-source replacement is app-driven and invokes no callback;
if the new source excludes the current value, coerce the state with that new source before composing.
Date repair preserves the accepted changed column and then repairs year/month/day dependencies; Time
repair does the same in period/hour/minute dependency order. Duration repair treats hour and minute
columns as one scalar elapsed value. Numeric ties choose the smaller value.

Use `PickerDefaults.timePickerLayout(...)`, `durationPickerLayout(...)`, `datePickerLayout(...)`, or
`yearMonthPickerLayout(...)`
when a composite picker needs different column proportions. Pass `null` for a column weight to let
`pickerModifier` provide an explicit width for that column. Use `columnOrder` when locale, product,
or form conventions need a different order, such as month/day/year:

```kotlin
DatePicker(
    state = state,
    layout = PickerDefaults.datePickerLayout(
        columnOrder = listOf(
            DatePickerColumn.MONTH,
            DatePickerColumn.DAY,
            DatePickerColumn.YEAR
        )
    )
)
```

`columnOrder` must contain each column exactly once. For `TimePicker`, `TimePickerColumn.PERIOD`
is rendered only in 12-hour mode and ignored in 24-hour mode.

### TimePicker

| Parameter | Description | Default |
| :--- | :--- | :--- |
| `state` | The state object to control the picker. | `rememberTimePickerState()` |
| `onSelectedTimeChange` | Called once with the committed selectable `LocalTime` after a changed column settles and dependent values are repaired. | `{}` |
| `enabled` | Whether user scroll, click, and semantics selection actions are enabled. | `true` |
| `items` | Selectable minute, 24-hour hour, 12-hour format-hour, and AM/PM item lists plus optional inclusive `minTime`/`maxTime` bounds. | `PickerDefaults.timePickerItems()` |
| `format` | Visible item text and optional accessibility value descriptions for each picker column. | `PickerDefaults.timePickerFormat()` |
| `style` | Visual and layout styling for each picker column. | `PickerDefaults.style()` |
| `selectionIndicator` | Shared selection band drawn across the whole picker. | `PickerDefaults.selectionIndicator(style)` |
| `layout` | Column weights and visual order for period, hour, and minute picker columns. Use `null` weights for explicit-width columns. | `PickerDefaults.timePickerLayout()` |
| `spacingBetweenPickers` | Horizontal spacing between picker columns. | `0.dp` |
| `semantics` | Picker labels and custom action labels for each picker column. | `PickerDefaults.timePickerSemantics()` |

**TimePickerState Properties:**

- `selectedHour`: The selected hour shown by the picker.
- `selectedMinute`: The selected minute (0-59).
- `selectedPeriod`: The selected AM/PM period when using 12-hour format.
- `selectedHourOfDay`: The selected hour converted to 24-hour clock time (0-23).
- `selectedTime`: The selected value as `kotlinx.datetime.LocalTime`.

`rememberTimePickerState` uses saveable state. On Android, selected values can be restored across Activity recreation when the platform saveable registry is available.

For initial values, use either `rememberTimePickerState(initialTime = LocalTime(...))` or the
explicit `initialHour`/`initialMinute` parameters. Pass the same `items` object when the initial time
or parts should be coerced before first composition.

To change the selection after state creation, call `state.selectTime(LocalTime(...))` or
`state.selectTime(hour, minute)`. Use the overloads that accept `items` when custom item lists or
time bounds should be applied at the same time. The integer hour is interpreted as hour-of-day in
`0..23`. If app-owned 12-hour form or preset values are stored as a display hour plus AM/PM, use
`items.coerceTime(displayHour = ..., minute = ..., period = ...)` or
`state.selectTime(displayHour = ..., minute = ..., period = ..., items = ...)`; `displayHour` is in
`1..12`.

Invalid custom item values, duplicate items, empty required lists, or current selections missing from custom lists or time bounds throw `IllegalArgumentException` during composition. Treat custom item lists as immutable after passing them to the picker; create a new `items` object when available values change. In 12-hour mode, `PickerDefaults.timePickerItems(hour12Items = ...)` uses format-hour values (`1..12`): `initialHour = 13` becomes `state.selectedHour == 1` with `PM`.

### DurationPicker

| Parameter | Description | Default |
| :--- | :--- | :--- |
| `state` | The state object holding one elapsed `Duration`. | `rememberDurationPickerState()` |
| `onSelectedDurationChange` | Called once with the committed selectable `Duration` after a changed column settles and dependent values are repaired. | `{}` |
| `enabled` | Whether user scroll, click, and semantics selection actions are enabled. | `true` |
| `items` | Non-negative elapsed-hour and minute-within-hour lists plus optional inclusive scalar `minDuration`/`maxDuration` bounds. The default hour source is `0..23`; pass custom hours for durations of 24 hours or longer. | `PickerDefaults.durationPickerItems()` |
| `format` | Visible item text and optional accessibility value descriptions for both columns. | `PickerDefaults.durationPickerFormat()` |
| `style` | Visual and layout styling for each picker column. | `PickerDefaults.style()` |
| `selectionIndicator` | Shared selection band drawn across the whole picker. | `PickerDefaults.selectionIndicator(style)` |
| `layout` | Column weights and visual order for elapsed-hour and minute columns. Use `null` weights for explicit-width columns. | `PickerDefaults.durationPickerLayout()` |
| `spacingBetweenPickers` | Horizontal spacing between picker columns. | `0.dp` |
| `semantics` | Picker labels and custom action labels for both columns. | `PickerDefaults.durationPickerSemantics()` |

**DurationPickerState Properties:**

- `selectedDuration`: The selected finite, non-negative, whole-minute `kotlin.time.Duration`.
- `selectedHours`: The elapsed whole-hour component. It is not limited to time-of-day values.
- `selectedMinutes`: The minute-within-hour component (`0..59`).

Use `rememberDurationPickerState(items = items, initialDuration = value)` when the initial or restored
value must be coerced before first composition. Call `state.selectDuration(value)` for an already
selectable app-driven value, or `state.selectDuration(value, items)` to coerce it through custom item
sources and scalar bounds. Programmatic calls do not invoke `onSelectedDurationChange`.

Durations must be finite, non-negative, and aligned to a whole minute. Hour items are elapsed hours,
so custom sources may include values above `23`; minute items must remain in `0..59`. Invalid,
duplicate, empty, or unsatisfiable item sources throw `IllegalArgumentException`. Equal-distance
coercion chooses the smaller duration.

### DatePicker

| Parameter           | Description                             | Default                     |
|:--------------------|:----------------------------------------|:----------------------------|
| `state`             | The state object to control the picker. | `rememberDatePickerState()` |
| `onSelectedDateChange` | Called once with the committed selectable `LocalDate` after a changed column settles and dependent values are repaired. | `{}` |
| `enabled` | Whether user scroll, click, and semantics selection actions are enabled. | `true` |
| `items`             | Selectable year/month/day item lists plus optional inclusive `minDate`/`maxDate` bounds. Values must be in `1000..9999`, `1..12`, and `1..31`. | `PickerDefaults.datePickerItems()` |
| `format` | Visible item text and optional accessibility value descriptions for each picker column. | `PickerDefaults.datePickerFormat()` |
| `style`             | Visual and layout styling for each picker column. | `PickerDefaults.style()` |
| `selectionIndicator` | Shared selection band drawn across the whole picker. | `PickerDefaults.selectionIndicator(style)` |
| `layout` | Column weights and visual order for year, month, and day picker columns. Use `null` weights for explicit-width columns. | `PickerDefaults.datePickerLayout()` |
| `spacingBetweenPickers` | Horizontal spacing between picker columns. | `0.dp` |
| `semantics` | Picker labels and custom action labels for each picker column. | `PickerDefaults.datePickerSemantics()` |

**DatePickerState Properties:**

- `selectedYear`: The currently selected year.
- `selectedMonth`: The currently selected month (1-12).
- `selectedDay`: The currently selected day (1-31, auto-adjusted based on month).
- `selectedDate`: The selected value as `kotlinx.datetime.LocalDate`.
- `maxDay`: The maximum valid day for the selected year and month.

`rememberDatePickerState` uses saveable state. On Android, selected values can be restored across Activity recreation when the platform saveable registry is available.

For initial values, use either `rememberDatePickerState(initialDate = LocalDate(...))` or the
explicit `initialYear`/`initialMonth`/`initialDay` parameters. Pass the same `items` object when the
initial value or parts should be coerced before first composition. Initial years must be in
`1000..9999`, months in `1..12`, and days must be at least `1`. If `initialDay` is greater than the
maximum valid day for the initial year/month, it is clamped to that maximum.

To change the selection after state creation, call `state.selectDate(LocalDate(...))` or
`state.selectDate(year, month, day)`. Use the overloads that accept `items` when custom item lists
or date bounds should be applied at the same time.

Invalid custom item values, duplicate items, empty lists, or current selected year/month/day values missing from custom lists or date bounds throw `IllegalArgumentException` during composition. Treat custom item lists as immutable after passing them to the picker; create a new `items` object when available values change. If a year/month change makes the selected month or day unavailable, the picker selects the closest available value for the configured constraints.

### DateRangePicker

| Parameter | Description | Default |
| :--- | :--- | :--- |
| `state` | The state object to control the selected start and end dates. | `rememberDateRangePickerState()` |
| `onSelectedDateRangeChange` | Called after user interaction changes the selected `DateRange`. | `{}` |
| `enabled` | Whether user scroll, click, and semantics selection actions are enabled. | `true` |
| `items` | Shared selectable year/month/day item lists plus optional inclusive `minDate`/`maxDate` bounds. | `PickerDefaults.datePickerItems()` |
| `format` | Visible item text and optional accessibility value descriptions for each picker column. | `PickerDefaults.datePickerFormat()` |
| `style` | Visual and layout styling for each picker column. | `PickerDefaults.style()` |
| `selectionIndicator` | Shared selection band drawn across each child `DatePicker`. | `PickerDefaults.selectionIndicator(style)` |
| `layout` | Column weights and visual order for each child `DatePicker`. | `PickerDefaults.datePickerLayout()` |
| `spacingBetweenPickers` | Horizontal spacing between columns inside each child `DatePicker`. | `0.dp` |
| `spacingBetweenDatePickers` | Vertical spacing between the start and end child pickers. | `16.dp` |
| `startLabel` / `endLabel` | Optional visible labels above each child picker. | `"Start date"` / `"End date"` |
| `semantics` | Picker labels and custom action labels for the start and end child pickers. | `PickerDefaults.dateRangePickerSemantics()` |

`DateRangePickerState` keeps `selectedStartDate <= selectedEndDate`. If a user moves the start after
the current end, the end moves to the same date. If a user moves the end before the current start,
the start moves to the same date.

For initial values, use `rememberDateRangePickerState(initialDateRange = DateRange(...))`,
`rememberDateRangePickerState(initialStartDate = ..., initialEndDate = ...)`, or the explicit
`initialStartYear`/`initialStartMonth`/`initialStartDay` and matching end-date parameters. To change
the selection after state creation, call `state.selectDateRange(...)`, `state.selectStartDate(...)`,
or `state.selectEndDate(...)` with `DateRange`, `LocalDate`, or explicit year/month/day values.
`DateRange` can also be created from explicit start/end year, month, and day parts. If app-owned
start/end fields may be entered in either order, use `DateRange.ordered(startDate, endDate)` or the
matching year/month/day overload before passing the value to state. When custom `items` or
`minDate`/`maxDate` bounds should normalize app-owned presets, use `items.coerceDateRange(...)`,
`rememberDateRangePickerState(items = ..., initialStartDate = ..., initialEndDate = ...)`, or the
`state.selectDateRange(..., items)` overloads; they coerce both boundaries to the closest selectable
dates and return, create, or select an ordered `DateRange`. If a preset should be rejected instead
of coerced, call `items.contains(DateRange(...))` or the matching start/end overload first; these
helpers check the selectable start and end boundaries only, not every date inside the range. Use
`range.contains(year, month, day)` when app-owned form fields need an inclusive range check before
creating a `LocalDate`. Use `date in range`, `childRange in range`, and
`range.overlaps(blockedRange)` for inclusive date and range checks. Use
`range.intersection(blockedRange)` when apps need the shared sub-range itself. Use
`range.isSingleDay` for one-day selections and `range.dayCount` to display the inclusive number of
calendar days in the selected range.

### YearMonthPicker

| Parameter | Description | Default |
| :--- | :--- | :--- |
| `state` | The state object to control the picker. | `rememberYearMonthPickerState()` |
| `onSelectedYearMonthChange` | Called after user interaction changes the selected `YearMonth`. | `{}` |
| `enabled` | Whether user scroll, click, and semantics selection actions are enabled. | `true` |
| `items` | Selectable year/month item lists plus optional inclusive `minYearMonth`/`maxYearMonth` bounds. Values must be in `1000..9999` and `1..12`. | `PickerDefaults.yearMonthPickerItems()` |
| `format` | Visible item text and optional accessibility value descriptions for each picker column. | `PickerDefaults.yearMonthPickerFormat()` |
| `style` | Visual and layout styling for each picker column. | `PickerDefaults.style()` |
| `selectionIndicator` | Shared selection band drawn across the whole picker. | `PickerDefaults.selectionIndicator(style)` |
| `layout` | Column weights and visual order for year and month picker columns. Use `null` weights for explicit-width columns. | `PickerDefaults.yearMonthPickerLayout()` |
| `spacingBetweenPickers` | Horizontal spacing between picker columns. | `0.dp` |
| `semantics` | Picker labels and custom action labels for each picker column. | `PickerDefaults.yearMonthPickerSemantics()` |

**YearMonthPickerState Properties:**

- `selectedYear`: The currently selected year.
- `selectedMonth`: The currently selected month (1-12).
- `selectedYearMonth`: The selected value as `date.YearMonth`.
- `selectedMonthDate`: The selected year/month represented as the first day of that month.

`rememberYearMonthPickerState` uses saveable state. On Android, selected values can be restored across Activity recreation when the platform saveable registry is available.

For initial values, prefer `rememberYearMonthPickerState(initialYearMonth = YearMonth(...))` for year/month-only selections. You can also initialize from `initialDate = LocalDate(...)` for date interop, or use the explicit `initialYear`/`initialMonth` parameters. Initial years must be in `1000..9999`.

To change the selection after state creation, call `state.selectYearMonth(YearMonth(...))`,
`state.selectYearMonth(year, month)`, or `state.selectDate(LocalDate(...))`.

Invalid custom item values, duplicate items, empty lists, or current selected year/month values missing from custom lists or year/month bounds throw `IllegalArgumentException` during composition. Treat custom item lists as immutable after passing them to the picker; create a new `items` object when available values change. If a year change makes the current month unavailable, the picker moves to the closest available `YearMonth`.

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
