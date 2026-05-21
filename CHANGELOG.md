# Changelog

This project tracks notable user-facing and maintainer-facing changes here. The repository version is `0.6.0`; the `Unreleased` section documents changes queued for the next release.

## Unreleased

### Added

- Added programmatic selection APIs for picker state objects:
  `PickerState.selectItem`, `TimePickerState.selectTime`, `DatePickerState.selectDate`,
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
- Removed the redundant Android sample `MaterialTheme` wrapper so the sample entry point uses the shared `AppTheme`.
- Documented generic `Picker<T>` usage, initial `startIndex` alignment, and the regular `remember`
  behavior of `rememberPickerState`.
- Clarified `DatePicker` README examples for custom year ranges and state synchronization after composition.
- Clarified README installation guidance to distinguish published `0.4.0` artifacts from unreleased `main`/`0.6.0` API documentation.
- Made library `@Preview` composables private tooling code so preview functions do not appear in the supported public API surface.

### Compatibility Notes

- The `*Preview` cleanup removes previously exposed but undocumented tooling symbols from repository ABI dumps. It does not remove supported picker/state APIs. If app code imported those preview functions from a local or snapshot build, remove those calls and use the actual picker composables instead.
- ABI dump updates are now part of public API review. Intentional public API changes should update `datetimepicker/api/` and explain the compatibility impact.

### Maintenance

- Added Android managed-device smoke coverage for the sample app home screen and `DatePicker` navigation path.
- Added Android instrumented accessibility semantics coverage and a Gradle Managed Device CI gate.
- Extended Android accessibility action coverage for `TimePicker`, `DatePicker`, and `YearMonthPicker`
  child picker state updates.
- Added Android state restoration coverage for `rememberTimePickerState`, including 12-hour noon,
  `rememberDatePickerState`, and `rememberYearMonthPickerState`.
- Added `TimePickerState.Saver` edge-case coverage for 12-hour midnight and noon restoration.
- Added Kotlin ABI validation with committed Android, Desktop, and KLIB API dumps.
- Pull requests now run Android build/unit, Desktop, Wasm, iOS simulator, Kotlin ABI, Android managed-device instrumented test, and PR diff hygiene checks.
