# Changelog

This project tracks notable user-facing and maintainer-facing changes here. The repository version is `0.6.0`; the `Unreleased` section documents changes queued for the next release.

## Unreleased

### Added

- Added programmatic selection APIs for picker state objects:
  `TimePickerState.selectTime`, `DatePickerState.selectDate`,
  `YearMonthPickerState.selectYearMonth`, and `YearMonthPickerState.selectDate`.
- Added picker accessibility descriptions and localized item description hooks so Android apps can provide clearer TalkBack output.
- Added previous/next accessibility actions for picker columns, with public labels that apps can localize.

### Changed

- Updated the sample app to show programmatic selection buttons, a `DatePicker` example that derives
  selectable years from the current year and a nearby leap-day target, and Korean localized picker
  accessibility labels across the `TimePicker`, `BackgroundStyle`, `Integrated`, and `BottomSheet`
  samples.
- Refined sample screens with shared result cards, picker panels, real reset/confirm actions, and
  bottom-sheet draft state separate from committed selection values.
- Improved sample accessibility semantics for selected-value cards and home-screen navigation items.
- Improved the integrated sample summary semantics and covered it in the sample Android smoke test.
- Removed the redundant Android sample `MaterialTheme` wrapper so the sample entry point uses the shared `AppTheme`.
- Reworked README bottom-sheet examples to use separate committed and draft picker state.
- Clarified `DatePickerState` initial-day documentation to distinguish invalid values from max-day clamping.
- Documented generic `Picker<T>` controlled usage and app-owned saveable selection state.
- Clarified `DatePicker` README examples for custom year ranges and state synchronization after composition.
- Clarified README installation guidance to distinguish published `0.4.0` artifacts from unreleased `main`/`0.6.0` API documentation.
- Made library `@Preview` composables private tooling code so preview functions do not appear in the supported public API surface.
- Reworked generic `Picker<T>` into a controlled component with `selectedItem` and
  `onSelectedItemChange`, removing the old `PickerState<T>` and positional `startIndex` source of truth.
- Removed `startTime` from `TimePicker` and `startLocalDate` from `DatePicker`/`YearMonthPicker`; initial values now belong to `remember*State` APIs.
- Reworked `TimePickerState`, `DatePickerState`, and `YearMonthPickerState` so they own only logical values instead of exposing or coordinating child picker states.
- Moved state APIs into the component packages: `TimePickerState` and `rememberTimePickerState` are now in `com.kez.picker.time`, and `YearMonthPickerState` and `rememberYearMonthPickerState` are now in `com.kez.picker.date`.
- Added `PickerStyle` and `PickerDefaults.style(...)` so repeated picker visual/layout configuration can be passed as one reusable object.
- Added `PickerAccessibility` plus component-specific accessibility option objects so localized labels,
  item descriptions, and previous/next action labels can be passed as one reusable object.
- Custom item lists are now strict: required lists must be non-empty and distinct, values must be in range, and the current selected value must be present before composition proceeds.

### Compatibility Notes

- The `*Preview` cleanup removes previously exposed but undocumented tooling symbols from repository ABI dumps. It does not remove supported picker/state APIs. If app code imported those preview functions from a local or snapshot build, remove those calls and use the actual picker composables instead.
- ABI dump updates are now part of public API review. Intentional public API changes should update `datetimepicker/api/` and explain the compatibility impact.
- The controlled picker/state overhaul is a breaking 0.x API change. Replace
  `rememberPickerState(...)` + `Picker(state = ..., startIndex = ...)` with app-owned
  `selectedItem` state, and move all date/time initial values into `remember*State`.
- State API package moves are breaking 0.x import changes. Replace root imports such as
  `com.kez.picker.rememberTimePickerState` and `com.kez.picker.TimePickerState` with
  `com.kez.picker.time.*`; replace root year-month state imports with `com.kez.picker.date.*`.
- Picker visual parameters such as `visibleItemsCount`, `colors`, `textStyles`,
  `selectedItemBackgroundShape`, `itemPadding`, `fadingEdgeGradient`, divider configuration, and item
  alignment moved under `style = PickerDefaults.style(...)`.
- Picker accessibility parameters such as `*PickerLabel`, `*ItemContentDescription`,
  `previousItemActionLabel`, and `nextItemActionLabel` moved under
  `accessibility = PickerDefaults.*Accessibility(...)`.

### Maintenance

- Added Android managed-device smoke coverage for the sample app home screen and `DatePicker` navigation path.
- Added Android instrumented accessibility semantics coverage and a Gradle Managed Device CI gate.
- Added Android coverage for omitting picker custom accessibility actions with null or blank labels.
- Added Android rendered state-restoration coverage for `DatePicker` and `YearMonthPicker`.
- Extended Android accessibility action coverage for `TimePicker`, `DatePicker`, and `YearMonthPicker`
  child picker state updates.
- Added Android state restoration coverage for `rememberTimePickerState`, including 12-hour noon,
  `rememberDatePickerState`, and `rememberYearMonthPickerState`.
- Added `TimePickerState.Saver` edge-case coverage for 12-hour midnight and noon restoration.
- Added Kotlin ABI validation with committed Android, Desktop, and KLIB API dumps.
- Pull requests now run Android build/unit, Desktop, Wasm, iOS simulator, Kotlin ABI, Android managed-device instrumented test, and PR diff hygiene checks.
