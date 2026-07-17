# Changelog

This project tracks notable user-facing and maintainer-facing changes here. The repository version is `0.6.0`; the `Unreleased` section documents changes queued for the next release.

## Unreleased

### Added

- Add a reproducible Android Macrobenchmark harness for generic 10/100/10,000-item picker startup and bounded-versus-exact 9,000-year `DatePicker` rendering comparisons.
- Added `PickerDividerWidth` (`Fill`, `Fraction`, `Fixed`) and the `dividerWidth` parameter to
  `PickerStyle` / `PickerDefaults.style(...)` so the selection divider length can be a fraction of
  the column width or a fixed `Dp` instead of always filling the column. The divider stays centered
  horizontally.
- Added `PickerSelectionIndicator` and `PickerDefaults.selectionIndicator(...)` plus a
  `selectionIndicator` parameter on `TimePicker`, `DatePicker`, `YearMonthPicker`, and
  `DateRangePicker`. Composite pickers now draw a single selection band spanning the whole picker
  (with an optional `horizontalInset`) instead of one divider per column, so the selection lines stay
  aligned regardless of column widths and column spacing. The default indicator is derived from
  `style`, so existing `dividerColor` / `dividerThickness` / `isDividerVisible` customizations still
  apply.
- Added `PickerSelectionIndicator.disabledColor` and a style-free
  `PickerDefaults.selectionIndicator(...)` overload for apps that only need to customize the shared
  composite selection band.
- Added programmatic selection APIs for picker state objects:
  `TimePickerState.selectTime`, `DatePickerState.selectDate`,
  `YearMonthPickerState.selectYearMonth`, and `YearMonthPickerState.selectDate`.
- Added user-selection callbacks to composite pickers:
  `onSelectedTimeChange`, `onSelectedDateChange`, and `onSelectedYearMonthChange`.
- Added `date.YearMonth` as the primary value model for year/month-only selections.
- Added `dayItems` to `DatePickerItems` / `PickerDefaults.datePickerItems(...)` so apps can
  constrain selectable days as well as years and months.
- Added `TimePickerItems.coerceTime(...)`, `DatePickerItems.coerceDate(...)`, and
  `YearMonthPickerItems.coerceYearMonth(...)` plus matching state-selection overloads so apps can
  move restored or preset values to the closest selectable value before rendering custom item lists.
- Added `DatePickerItems.coerceDate(year, month, day)` and
  `rememberDatePickerState(items = ..., initialYear = ..., initialMonth = ..., initialDay = ...)`
  for apps that store date form state as primitive fields.
- Added `TimePickerItems.coerceTime(hour, minute, timeFormat)` and
  `rememberTimePickerState(items = ..., initialHour = ..., initialMinute = ...)` for apps that store
  time form state as primitive fields.
- Added `TimePickerItems.coerceTime(displayHour, minute, period)`,
  `TimePickerItems.contains(displayHour, minute, period)`, and matching
  `TimePickerState.selectTime(displayHour, minute, period, ...)` overloads for apps that store
  12-hour form state as display-hour plus AM/PM values.
- Added `remember*State(items = ..., initial... = ...)` overloads so initial picker state can be
  coerced by the same custom item lists before first composition.
- Added `rememberYearMonthPickerState(initialYearMonth = ...)` and the matching `items` overload so
  year/month-only state can be initialized without routing through `LocalDate`.
- Added `TimePickerState.selectTime(hour, minute)` and `DatePickerState.selectDate(year, month, day)`
  overloads for apps that store primitive form values.
- Added `DateRange`, `DateRangePickerState`, `rememberDateRangePickerState`, and `DateRangePicker`
  for ordered start/end date selection without manually wiring two `DatePicker` instances. Date
  range values and state also support year/month/day overloads for apps that store primitive form
  values.
- Added `DateRange.ordered(...)` factories so apps can normalize unordered start/end inputs from
  forms, presets, or restored state before passing them to `DateRangePickerState`.
- Added `DatePickerItems.contains(DateRange)` and matching start/end overloads so apps can check
  whether date range preset boundaries are directly selectable before deciding to reject or coerce
  the preset.
- Added `DatePickerItems.coerceDateRange(...)` so apps can normalize app-owned date range presets
  with the same custom item lists and `minDate`/`maxDate` bounds used by `DateRangePicker`.
- Added `DateRange.dayCount` so apps can display the inclusive number of calendar days in the
  selected range without recalculating epoch-day differences.
- Added `DateRange.isSingleDay`, `DateRange.contains(DateRange)`, and `DateRange.overlaps(...)` so
  apps can check selected ranges for one-day, containment, and overlap cases without duplicating
  inclusive boundary logic.
- Added `DateRange.intersection(...)` so apps can retrieve the shared inclusive sub-range between
  two date ranges, or `null` when no calendar day overlaps.
- Added `DateRangePickerState` and `rememberDateRangePickerState` overloads that accept a `DateRange`
  value directly.
- Added value-first state constructors: `TimePickerState(LocalTime, ...)`,
  `DatePickerState(LocalDate)`, `YearMonthPickerState(YearMonth)`, and
  `YearMonthPickerState(LocalDate)`.
- Added `contains(...)` predicates to picker item option objects so apps can check whether a value
  object or primitive parts are already directly selectable before deciding to reject or coerce them.
- Added `PickerDefaults.itemFormat(...)` plus `TimePickerFormat`, `DatePickerFormat`, and
  `YearMonthPickerFormat` option objects so apps can customize visible item text separately from
  semantics descriptions.
- Fixed sample picker interaction issues where long month labels were clipped, dependent date columns
  could refresh while another column was still scrolling, and the date range sample started from a
  one-day range that made end-date movement look like a reset on the first start-date change.
- `rememberDateRangePickerState(items = ..., initialStartDate = ..., initialEndDate = ...)` now
  accepts unordered initial boundaries, coerces each boundary with the provided date picker items,
  and creates an ordered initial range.
- `DateRangePickerState.selectDateRange(..., items)` now accepts unordered boundaries, coerces each
  boundary with the provided date picker items, and selects an ordered range.
- Fixed picker row height calculation and default vertical item padding so selected text no longer
  appears clipped against the selection dividers.
- Fixed picker row height consistency across mixed Korean, numeric, and Latin font fallback so
  composite picker dividers stay aligned and constrained day columns do not disappear during item
  list changes.
- Simplified the basic date, date range, and year/month samples so the picker state is the single
  source of truth for formatted selections.
- Added inclusive `DatePicker` bounds through `DatePickerConstraints` and
  `PickerDefaults.datePickerItems(minDate = ..., maxDate = ...)`.
- Added inclusive `TimePicker` bounds through `TimePickerConstraints` and
  `PickerDefaults.timePickerItems(minTime = ..., maxTime = ...)`.
- Added inclusive `YearMonthPicker` bounds through `YearMonthPickerConstraints` and
  `PickerDefaults.yearMonthPickerItems(minYearMonth = ..., maxYearMonth = ...)`.
- Added `enabled` parameters to `Picker`, `TimePicker`, `DatePicker`, and `YearMonthPicker` so apps
  can show a selected value while preventing user scroll, click, and semantics selection actions.
- Added disabled color slots to `PickerColors` / `PickerDefaults.colors(...)` so disabled pickers can
  keep selected values visible while clearly communicating their disabled state.
- Added `PickerItemScope` for generic `Picker` custom content so row UI can read the formatted text,
  selected state, enabled state, distance fraction, text style, and content color.
- Added `TimePickerLayout`, `DatePickerLayout`, and `YearMonthPickerLayout` with
  `PickerDefaults.*Layout(...)` factories so apps can tune or opt out of composite picker column
  weights.
- Added `TimePickerColumn`, `DatePickerColumn`, and `YearMonthPickerColumn` order options so apps can
  render picker columns in locale- or form-specific order.
- Added picker semantics descriptions and localized item description hooks so Android apps can provide clearer TalkBack output.
- Added previous/next semantics actions for picker columns, with public labels that apps can localize.

### Changed

- Built-in numeric date/time formats now use bounded internal height probes, avoiding per-item text
  layouts for large default columns such as the 9,000-year range. Generic and custom formatters keep
  exact per-item measurement so arbitrary fallback-font glyphs are not sampled away.

- Changed the default `PickerDefaults.colors(...)` `dividerColor` from the full-strength
  `LocalContentColor` to `LocalContentColor.copy(alpha = 0.2f)` for a lighter default selection
  divider. Pass an explicit `dividerColor` to restore the previous appearance.
- Added `dividerWidth` to the `PickerStyle` primary constructor (between `dividerShape` and
  `isDividerVisible`), shifting its positional parameters and generated `componentN`/`copy`
  signatures. Code that constructs `PickerStyle` via `PickerDefaults.style(...)` is unaffected.
- Added a `selectionIndicator` parameter to `TimePicker`, `DatePicker`, `YearMonthPicker`, and
  `DateRangePicker` (after `style`), changing those composable signatures. Per-column `style` divider
  settings no longer render inside composite pickers; the shared `selectionIndicator` band draws the
  selection lines instead. Callers using named arguments are unaffected.
- Made composite picker selection bands use the same measured item height as child picker columns
  instead of deriving band height from the final content height, so custom `pickerModifier` heights
  do not misalign the selected-row indicator.
- Made `PickerDividerWidth.Fixed.width`, `PickerSelectionIndicator.thickness`, and
  `PickerSelectionIndicator.horizontalInset` reject infinite or unspecified `Dp` values in addition
  to negative values.
- Made `DatePickerItems.coerceDate(...)` compare whole selectable `LocalDate` values, matching the
  `coerceTime(...)` and `coerceYearMonth(...)` behavior instead of independently coercing year,
  month, and day.
- Made `YearMonthPickerItems.coerceYearMonth(year, month)` reject primitive values outside
  `1000..9999` and `1..12`, matching the state APIs and date primitive coercion overload.
- Documented that picker item lists should be treated as immutable after they are passed to a picker;
  create a new `items` object when available values change.
- Made `YearMonthPickerItems.contains(year, month)` return `false` for raw year/month values outside
  the supported ranges instead of constructing an invalid `YearMonth`.
- Made picker item-list and constraint construction errors explain which min/max bounds or custom
  item lists to adjust when no selectable value remains.
- Made generic `Picker<T>` validation errors explain how to recover from empty lists, duplicate
  values, invalid `selectedItem`, and invalid `visibleItemsCount` configuration.
- Clarified the state and callback usage pattern in README and picker KDoc so apps can distinguish
  user-driven `onSelected*Change` callbacks from programmatic `state.select*` changes.
- Made custom item-list and constraint validation errors for date, time, and year/month pickers
  include the matching `remember*State(items = ...)`, `items.coerce*`, and `state.select*(..., items)`
  recovery paths.
- Ensured `DateRangePicker` dispatches `onSelectedDateRangeChange` when a child date picker changes
  the start or end date.
- Clarified `DateRangePicker` callback and spacing parameter documentation in both READMEs.
- Updated the sample app to show programmatic selection buttons, a `DatePicker` example that derives
  selectable years from the current year and a nearby leap-day target, and Korean localized picker
  semantics labels across the `TimePicker`, `BackgroundStyle`, `Integrated`, and `BottomSheet`
  samples.
- Refined sample screens with shared result cards, picker panels, real reset/confirm actions, and
  bottom-sheet draft state separate from committed selection values.
- Improved sample accessibility semantics for selected-value cards and home-screen navigation items.
- Reworked picker value text APIs from `display` to `format`. Value content descriptions now live
  beside visible item text in `PickerItemFormat` and component `*Format` objects.
- Reworked picker label/action APIs from generic `PickerAccessibility<T>` to non-generic
  `PickerSemantics`. Semantics objects now describe structural labels and actions only.
- When a format does not provide `itemContentDescription`, picker accessibility values now fall back
  to the visible item text. Apps should still provide explicit descriptions when a screen reader
  should read a richer phrase than the visible label.
- `DatePicker` and `YearMonthPicker` year columns are bounded by default instead of wrapping from
  the last supported year back to the first.
- Picker item validation and derived selectable lists are cached by cheap structural signals so
  recomposition no longer re-runs the same validation and list filtering work every frame.
- Improved the integrated sample summary semantics and covered it in the sample Android smoke test.
- Removed the redundant Android sample `MaterialTheme` wrapper so the sample entry point uses the shared `AppTheme`.
- Reworked README bottom-sheet examples to use separate committed and draft picker state.
- Clarified `DatePickerState` initial-day documentation to distinguish invalid values from max-day clamping.
- Documented generic `Picker<T>` controlled usage and app-owned saveable selection state.
- Clarified `DatePicker` README examples for custom year ranges and state synchronization after composition.
- Clarified README installation guidance to distinguish the published Maven Central `0.6.0`
  artifact, the still-latest GitHub Release tag, and unreleased `main` API documentation.
- Made library `@Preview` composables private tooling code so preview functions do not appear in the supported public API surface.
- Reworked generic `Picker<T>` into a controlled component with `selectedItem` and
  `onSelectedItemChange`, removing the old `PickerState<T>` and positional `startIndex` source of truth.
- Removed `startTime` from `TimePicker` and `startLocalDate` from `DatePicker`/`YearMonthPicker`; initial values now belong to `remember*State` APIs.
- Reworked `TimePickerState`, `DatePickerState`, and `YearMonthPickerState` so they own only logical values instead of exposing or coordinating child picker states.
- Moved state APIs into the component packages: `TimePickerState` and `rememberTimePickerState` are now in `com.kez.picker.time`, and `YearMonthPickerState` and `rememberYearMonthPickerState` are now in `com.kez.picker.date`.
- Added `PickerStyle` and `PickerDefaults.style(...)` so repeated picker visual/layout configuration can be passed as one reusable object.
- Added `PickerSemantics` plus component-specific semantics option objects so localized labels,
  item descriptions, and previous/next action labels can be passed as one reusable object.
- Added component-specific item-list option objects so custom selectable values can be passed as one
  reusable `items` object.
- `YearMonthPickerState` now exposes `selectedYearMonth: YearMonth` in addition to
  `selectedMonthDate` for `LocalDate` interoperability.
- `DatePicker` now adjusts to the closest available custom day when a year/month change makes the
  previous day unavailable in `dayItems`.
- `DatePicker` now filters month and day columns through `DatePickerConstraints` when
  `minDate`/`maxDate` bounds are configured.
- `TimePicker` now filters hour, minute, and AM/PM columns through `TimePickerConstraints` when
  `minTime`/`maxTime` bounds are configured.
- `TimePickerItems.coerceTime(...)` now chooses the closest selectable `LocalTime` as a whole value,
  instead of independently coercing hour and minute columns.
- `YearMonth` now implements `Comparable<YearMonth>`, and `YearMonthPickerItems.coerceYearMonth(...)`
  now chooses the closest selectable `YearMonth` as a whole month value.
- `YearMonthPicker` now filters year and month columns through `YearMonthPickerConstraints` when
  `minYearMonth`/`maxYearMonth` bounds are configured.
- Disabled pickers now expose disabled semantics, omit previous/next semantics selection actions,
  and use disabled text, divider, and selected-item background colors.
- Picker row scaling now stays stable while constrained item lists change, and the scroll gesture
  area fills the picker column instead of shrinking to the visible text.
- `DatePicker.spacingBetweenPickers` now applies actual horizontal spacing between year, month, and
  day columns.
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
- Picker semantics parameters such as `*PickerLabel`, `*ItemContentDescription`,
  `previousItemActionLabel`, and `nextItemActionLabel` moved under
  `semantics = PickerDefaults.*Semantics(...)`; item content descriptions now move to
  `format = PickerDefaults.*Format(...)`.
- Picker custom item-list parameters such as `minuteItems`, `hourItems`, `periodItems`, `yearItems`,
  and `monthItems` moved under `items = PickerDefaults.*Items(...)`.
- Composite picker function signatures now include `format` after `items`. Prefer named arguments
  when configuring `style`, `layout`, `spacingBetweenPickers`, `semantics`, or `format`.
- Composite picker function signatures now include user-selection callbacks immediately after `state`.
- `TimePickerItems` now includes a `constraints` property. Kotlin callers that use named/default
  arguments usually do not need source changes, but direct Java or binary constructor calls must pass
  the new argument after recompilation.
- `YearMonthPickerItems` now includes a `constraints` property. Kotlin callers that use named/default
  arguments usually do not need source changes, but direct Java or binary constructor calls must pass
  the new argument after recompilation.
- Picker function signatures now include `enabled` after the user-selection callback. Prefer named
  arguments when configuring `items`, `format`, `style`, `layout`, `spacingBetweenPickers`, or `semantics`.
  Named-argument call sites are straightforward to migrate; positional call sites may need argument
  reordering.
- Composite picker function signatures now include `layout` after `style`. Prefer named arguments
  when configuring `spacingBetweenPickers` or `semantics`.
- Generic `Picker.content` now receives `PickerItemScope<T>` instead of `T`. Replace direct item usage
  with `scope.item`, and use `scope.text`, `scope.textStyle`, or `scope.contentColor` when rendering
  custom rows.
- Generic `Picker` now accepts `format = PickerDefaults.itemFormat(...)` instead of a raw `itemText`
  lambda, matching the composite picker format option pattern.
- Generic `Picker` optional parameters are ordered as `enabled`, `format`, `style`, `semantics`,
  `isInfinity`, then `content` to match the composite picker option flow more closely. Prefer named
  arguments for optional configuration.
- Rename `PickerItemText` to `PickerItemFormat`, `TimePickerDisplay` to `TimePickerFormat`,
  `DatePickerDisplay` to `DatePickerFormat`, and `YearMonthPickerDisplay` to
  `YearMonthPickerFormat`.
- Rename `PickerAccessibility` to `PickerSemantics`, `TimePickerAccessibility` to
  `TimePickerSemantics`, `DatePickerAccessibility` to `DatePickerSemantics`,
  `YearMonthPickerAccessibility` to `YearMonthPickerSemantics`, and
  `DateRangePickerAccessibility` to `DateRangePickerSemantics`.
- `PickerStyle.verticalAlignment` and the public `calculateTime(...)` utility were removed before
  the 0.6.0 release. Use item content or `PickerStyle.itemAlignment` for row alignment and
  `TimePickerState.selectedTime` for the selected value.
- `DatePickerItems` now includes `dayItems`. Direct `DatePickerItems(...)` construction must pass
  day values; `PickerDefaults.datePickerItems(...)` remains the preferred factory.
- `DatePickerItems` now includes `constraints`. Kotlin callers can omit it because it has a default
  value; direct Java or binary call sites need to pass a `DatePickerConstraints` instance.

### Maintenance

- Added Android managed-device smoke coverage for the sample app home screen and `DatePicker` navigation path.
- Added Android instrumented accessibility semantics coverage and a Gradle Managed Device CI gate.
- Added Android coverage for omitting picker custom semantics actions with null or blank labels.
- Added Android rendered state-restoration coverage for `DatePicker` and `YearMonthPicker`.
- Extended Android semantics action coverage for `TimePicker`, `DatePicker`, and `YearMonthPicker`
  child picker state updates.
- Added Android state restoration coverage for `rememberTimePickerState`, including 12-hour noon,
  `rememberDatePickerState`, and `rememberYearMonthPickerState`.
- Added `TimePickerState.Saver` edge-case coverage for 12-hour midnight and noon restoration.
- Added Kotlin ABI validation with committed Android, Desktop, and KLIB API dumps.
- Pull requests now run Android build/unit, Desktop, Wasm, iOS simulator, Kotlin ABI, Android managed-device instrumented test, and PR diff hygiene checks.
