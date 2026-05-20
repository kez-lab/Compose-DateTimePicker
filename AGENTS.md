# AGENTS.md

This file provides guidance to Codex (Codex.ai/code) when working with code in this repository.

## Language Preference

**Always respond in Korean (한국어)** when interacting with this codebase. All explanations, summaries, documentation, and guidance should be written in Korean. Code identifiers, file/folder names, and technical terms should remain in English, but conceptual explanations and context should be in Korean.

## Project Overview

Compose-DateTimePicker is a Kotlin Multiplatform library providing date and time picker components for Compose. It targets Android, iOS, Desktop (JVM), and Web with a shared codebase, published to Maven Central as `io.github.kez-lab:compose-date-time-picker`.

**Current version**: 0.6.0
**License**: Apache 2.0

## Architecture

### Component Hierarchy

The library follows a **composition-based architecture** with a single generic `Picker<T>` component as the foundation:

```
Picker<T> (generic scrollable picker)
  ↓ composed into
TimePicker (hour + minute + optional AM/PM)
YearMonthPicker (year + month)
DatePicker (year + month + day)
```

**Key pattern**: Higher-level components (`TimePicker`, `YearMonthPicker`) are compositions of multiple `Picker` instances, not subclasses. Each `Picker` instance manages its own `PickerState<T>`.

### Core Components

**`Picker.kt`** (~440 lines)
- Generic scrollable picker using `LazyColumn` with snap behavior
- Supports infinite (`isInfinity=true`) and bounded scrolling
- Uses a bounded cyclic buffer (`items.size * 1000`) for infinite mode
- Fading edge effect via `graphicsLayer` + `BlendMode.DstIn`
- State updates via `snapshotFlow { firstVisibleItemIndex }` in `LaunchedEffect`

**`PickerState.kt`**
- Shared state holders for `Picker`, `TimePicker`, and `YearMonthPicker`
- Created via `rememberPickerState`, `rememberTimePickerState`, and `rememberYearMonthPickerState`
- `TimePickerState` exposes `selectedTime: LocalTime` and `selectedHourOfDay`
- `YearMonthPickerState` exposes `selectedMonthDate: LocalDate`
- Specialized picker states use saveable state; generic `PickerState<T>` uses regular `remember` because arbitrary `T` is not guaranteed saveable

**`DatePicker.kt`** / **`DatePickerState.kt`**
- Compose year, month, and day pickers
- Clamp selected day when the selected year/month has fewer days
- `DatePickerState` exposes `selectedDate: LocalDate`

**`TimePicker.kt`** / **`YearMonthPicker.kt`**
- Compose multiple `Picker` instances in a `Row`
- Handle 12/24-hour format conversion (TimePicker only)
- Use `kotlinx-datetime` for date/time utilities

### State Management Flow

```
User scrolls LazyColumn
  → LazyListState.firstVisibleItemIndex changes
  → snapshotFlow emits new index
  → state.selectedItem updated
  → Recomposition shows updated UI
```

### Multiplatform Structure

```
datetimepicker/src/
├── commonMain/kotlin/    # Shared UI and logic
├── androidMain/          # Android-specific (UI tooling preview)
├── iosMain/              # iOS-specific (currently minimal)
├── desktopMain/          # Desktop-specific (currently minimal)
├── jsMain/               # Web-specific source set directory, currently minimal
└── commonTest/           # Shared unit tests
```

Most logic lives in `commonMain`. Platform-specific code is minimal.

## Development Commands

## Maintainer Workflow Preferences

- Use `feature/*` branch names for new implementation work in this repository.
- Treat `main` as the integration branch. There is currently no `develop` branch on the remote.
- After a substantial implementation step, run a six-agent feedback loop when the maintainer asks for autonomous improvement work: collect feedback, fix actionable issues, verify again, then open or update the PR.
- Merge PRs only after relevant local verification and GitHub Actions checks pass.
- Keep improving toward Android developer ergonomics first: state APIs, sample usability, documentation clarity, accessibility, and predictable behavior in real app lifecycles.
- When public picker APIs accept custom item lists, validate value ranges before composing the underlying `Picker`. Prefer normalizing a valid-but-missing current state value through existing picker behavior over crashing during composition.
- When public validation rules change, update KDoc plus `README.md` and `README_KO.md` in the same PR, including failure mode and normalization behavior.
- Do not repurpose legacy `startTime` or `startLocalDate` component parameters for new initialization behavior. Preserve source compatibility, document that they are compatibility no-ops, and prefer explicit `remember*State` overloads for initial values.
- Treat `remember*State` initial parameters as first-composition defaults. Avoid resetting picker state from changing clock/date expressions during recomposition unless the API explicitly models a reset, and keep internal picker scroll state/effects keyed with state resets.
- When higher-level components pass accessibility labels to `Picker`, expose those labels and item content descriptions as public parameters with sensible defaults so Android apps can localize TalkBack output. Update KDoc and both READMEs in the same PR.
- Keep repository guidance up to date in this `AGENTS.md` when the maintainer gives durable process feedback.
- Do not include local agent/tooling folders such as `.agents/` or `.claude/` in product PRs unless the change is explicitly about agent workflow assets.

### Build

```bash
# Build library module only
./gradlew :datetimepicker:assemble --no-daemon

# Build all targets (Android, iOS, Desktop, JS)
./gradlew build --no-daemon

# Compile specific targets
./gradlew :datetimepicker:compileKotlinMetadata --no-daemon  # Common
./gradlew :datetimepicker:compileKotlinDesktop --no-daemon   # Desktop
./gradlew :datetimepicker:compileDebugKotlinAndroid --no-daemon
```

### Testing

```bash
# Run all tests
./gradlew :datetimepicker:test --no-daemon

# Run specific test suites
./gradlew :datetimepicker:testDebugUnitTest --no-daemon
./gradlew :datetimepicker:testReleaseUnitTest --no-daemon

# Compile Android instrumented tests
./gradlew :datetimepicker:assembleDebugAndroidTest --no-daemon

# Run Android instrumented tests on a connected device or emulator
./gradlew :datetimepicker:connectedDebugAndroidTest --no-daemon

# Run checks (tests + lint)
./gradlew :datetimepicker:check --no-daemon

# Verify sample app compilation
./gradlew :sample:compileKotlinDesktop --no-daemon
./gradlew :sample:wasmJsBrowserDistribution --no-daemon
```

**Run a single test**: Use `--tests` filter:
```bash
./gradlew :datetimepicker:testDebugUnitTest --tests "com.kez.picker.PickerStateTest.pickerState_initialValue_isCorrect" --no-daemon
```

### Sample App

```bash
# Run sample on Desktop
./gradlew :sample:desktopRun

# Install sample on Android
./gradlew :sample:installDebug
adb shell am start -n com.kez.picker.sample/.MainActivity

# iOS: open iosApp/iosApp.xcodeproj in Xcode
```

### Publishing

```bash
# Check version
./gradlew printVersion

# Test local publish
./gradlew :datetimepicker:publishToMavenLocal

# Production deploy: manually triggered by GitHub Actions workflow_dispatch
# See .github/workflows/maven-central-deploy.yml
```

## Key Implementation Details

### Infinite Scrolling

The `Picker` uses a bounded cyclic buffer for infinite mode:

```kotlin
private const val INFINITE_SCROLL_MULTIPLIER = 1000
val listScrollCount = adjustedItems.size * INFINITE_SCROLL_MULTIPLIER
fun getItem(index: Int) = adjustedItems[index.mod(adjustedItems.size)]
```

This keeps the picker virtually cyclic without exposing `Int.MAX_VALUE` list sizes.

### Snap Behavior

Uses `rememberSnapFlingBehavior(lazyListState)` to snap items to the center position. The selected item is determined by:

```kotlin
listState.firstVisibleItemIndex + visibleItemsMiddle
```

### Text Scaling Animation

The `Picker` interpolates font size and color based on item offset from center:

```kotlin
val fraction = abs(offset / itemHeight).coerceIn(0f, 1f)
fontSize = lerp(selectedTextStyle.fontSize, textStyle.fontSize, fraction)
color = lerp(selectedTextStyle.color, textStyle.color, fraction)
```

## Testing Strategy

**Current coverage** (estimated):
- Unit tests: picker states, date validation, time calculation, picker index utility behavior, and accessibility description formatting
- Android instrumented tests: picker accessibility semantics for selected values, custom labels, state descriptions, and higher-level picker forwarding
- Missing: broader UI interaction tests, screenshot tests, full TalkBack/readout validation, and sample smoke tests

**When adding tests**:
- Unit tests → `datetimepicker/src/commonTest/kotlin/`
- Android UI/instrumented tests → `datetimepicker/src/androidInstrumentedTest/kotlin/`
- Use `:datetimepicker:assembleDebugAndroidTest` to verify Android test APK compilation/packaging in CI-friendly environments, and `:datetimepicker:connectedDebugAndroidTest` when a device or emulator is available.
- Follow naming: `<ComponentName>Test.kt` or `<ComponentName>AndroidTest.kt`

## Code Style

- **Kotlin official style**: `kotlin.code.style=official`
- **Naming**: PascalCase for composables (`TimePicker`), camelCase for functions (`rememberPickerState`)
- **Documentation**: KDoc for all public APIs with `@param` descriptions
- **Formatting**: Handled by Kotlin plugin (no explicit ktlint/detekt config found)

## Version Management

Version is set in `gradle.properties`:
```properties
VERSION_NAME=0.6.0
```

Follow **Semantic Versioning**: MAJOR.MINOR.PATCH

## CI/CD

GitHub Actions workflows:
- **`integration-build-test.yml`**: Runs multiplatform library checks on pull requests to `main`
- **`maven-central-deploy.yml`**: Publishes releases to Maven Central

Build matrix: Ubuntu latest for Android/Desktop/Wasm and macOS 14 for iOS, using JDK 17 (Temurin)
