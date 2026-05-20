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

- Updated the sample app to show localized accessibility labels, programmatic selection buttons, and a `DatePicker` example that passes selectable year values through `yearItems = 2024..2026`.
- Made library `@Preview` composables private tooling code so preview functions do not appear in the supported public API surface.

### Compatibility Notes

- The `*Preview` cleanup removes previously exposed but undocumented tooling symbols from repository ABI dumps. It does not remove supported picker/state APIs. If app code imported those preview functions from a local or snapshot build, remove those calls and use the actual picker composables instead.
- ABI dump updates are now part of public API review. Intentional public API changes should update `datetimepicker/api/` and explain the compatibility impact.

### Maintenance

- Added Android instrumented accessibility semantics coverage and a Gradle Managed Device CI gate.
- Added Kotlin ABI validation with committed Android, Desktop, and KLIB API dumps.
- Pull requests now run Android build/unit, Desktop, Wasm, iOS simulator, Kotlin ABI, and Android managed-device instrumented test gates.
