# Changelog

This project tracks notable user-facing and maintainer-facing changes here. The repository version is `0.6.0`; the `Unreleased` section documents changes queued for the next release.

## Unreleased

### Added

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
- Added `DateRangePickerState` and `rememberDateRangePickerState` overloads that accept a `DateRange`
  value directly.
- Added value-first state constructors: `TimePickerState(LocalTime, ...)`,
  `DatePickerState(LocalDate)`, `YearMonthPickerState(YearMonth)`, and
  `YearMonthPickerState(LocalDate)`.
- Added `contains(...)` predicates to picker item option objects so apps can check whether a value
  object or primitive parts are already directly selectable before deciding to reject or coerce them.
- Added `PickerDefaults.itemText(...)` plus `TimePickerDisplay`, `DatePickerDisplay`, and
  `YearMonthPickerDisplay` option objects so apps can customize visible item text separately from
  accessibility descriptions.
- Fixed sample picker interaction issues where long month labels were clipped, dependent date columns
  could refresh while another column was still scrolling, and the date range sample started from a
  one-day range that made end-date movement look like a reset on the first start-date change.
- Fixed picker row height calculation and default vertical item padding so selected text no longer
  appears clipped against the selection dividers.
- Fixed picker row height consistency across mixed Korean, numeric, and Latin font fallback so
  composite picker dividers stay aligned and constrained day columns do not disappear during item
  list changes.
- Simplified the basic date, date range, and year/month samples so the picker state is the single
  source of truth for displayed selections.
- Added inclusive `DatePicker` bounds through `DatePickerConstraints` and
  `PickerDefaults.datePickerItems(minDate = ..., maxDate = ...)`.
- Added inclusive `TimePicker` bounds through `TimePickerConstraints` and
  `PickerDefaults.timePickerItems(minTime = ..., maxTime = ...)`.
- Added inclusive `YearMonthPicker` bounds through `YearMonthPickerConstraints` and
  `PickerDefaults.yearMonthPickerItems(minYearMonth = ..., maxYearMonth = ...)`.
- Added `enabled` parameters to `Picker`, `TimePicker`, `DatePicker`, and `YearMonthPicker` so apps
  can show a selected value while preventing user scroll, click, and accessibility selection actions.
- Added disabled color slots to `PickerColors` / `PickerDefaults.colors(...)` so disabled pickers can
  keep selected values visible while clearly communicating their disabled state.
- Added `PickerItemScope` for generic `Picker` custom content so row UI can read the formatted text,
  selected state, enabled state, distance fraction, text style, and content color.
- Added `TimePickerLayout`, `DatePickerLayout`, and `YearMonthPickerLayout` with
  `PickerDefaults.*Layout(...)` factories so apps can tune or opt out of composite picker column
  weights.
- Added `TimePickerColumn`, `DatePickerColumn`, and `YearMonthPickerColumn` order options so apps can
  render picker columns in locale- or form-specific order.
- Added picker accessibility descriptions and localized item description hooks so Android apps can provide clearer TalkBack output.
- Added previous/next accessibility actions for picker columns, with public labels that apps can localize.

### Changed

- Ensured `DateRangePicker` dispatches `onSelectedDateRangeChange` when a child date picker changes
  the start or end date.
- Clarified `DateRangePicker` callback and spacing parameter documentation in both READMEs.
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
- Disabled pickers now expose disabled semantics, omit previous/next accessibility selection actions,
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
- Picker accessibility parameters such as `*PickerLabel`, `*ItemContentDescription`,
  `previousItemActionLabel`, and `nextItemActionLabel` moved under
  `accessibility = PickerDefaults.*Accessibility(...)`.
- Picker custom item-list parameters such as `minuteItems`, `hourItems`, `periodItems`, `yearItems`,
  and `monthItems` moved under `items = PickerDefaults.*Items(...)`.
- Composite picker function signatures now include `display` after `items`. Prefer named arguments
  when configuring `style`, `spacingBetweenPickers`, `accessibility`, or `display`.
- Composite picker function signatures now include user-selection callbacks immediately after `state`.
- `TimePickerItems` now includes a `constraints` property. Kotlin callers that use named/default
  arguments usually do not need source changes, but direct Java or binary constructor calls must pass
  the new argument after recompilation.
- `YearMonthPickerItems` now includes a `constraints` property. Kotlin callers that use named/default
  arguments usually do not need source changes, but direct Java or binary constructor calls must pass
  the new argument after recompilation.
- Picker function signatures now include `enabled` after the user-selection callback. Prefer named
  arguments when configuring `items`, `display`, `style`, `spacingBetweenPickers`, or `accessibility`.
  Named-argument call sites are straightforward to migrate; positional call sites may need argument
  reordering.
- Composite picker function signatures now include `layout` after `style`. Prefer named arguments
  when configuring `spacingBetweenPickers` or `accessibility`.
- Generic `Picker.content` now receives `PickerItemScope<T>` instead of `T`. Replace direct item usage
  with `scope.item`, and use `scope.text`, `scope.textStyle`, or `scope.contentColor` when rendering
  custom rows.
- Generic `Picker` now accepts `display = PickerDefaults.itemText(...)` instead of a raw `itemText`
  lambda, matching the composite picker display option pattern.
- Generic `Picker` optional parameters are ordered as `enabled`, `display`, `style`, `accessibility`,
  `isInfinity`, then `content` to match the composite picker option flow more closely. Prefer named
  arguments for optional configuration.
- `DatePickerItems` now includes `dayItems`. Direct `DatePickerItems(...)` construction must pass
  day values; `PickerDefaults.datePickerItems(...)` remains the preferred factory.
- `DatePickerItems` now includes `constraints`. Kotlin callers can omit it because it has a default
  value; direct Java or binary call sites need to pass a `DatePickerConstraints` instance.

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
