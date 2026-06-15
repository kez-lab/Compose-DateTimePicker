---
name: compose-picker-api-stabilizer
description: Audit and improve Compose-DateTimePicker public API ergonomics, API stability, ABI dumps, KDoc, README usage, picker state contracts, item constraints, formatting, semantics, and cross-component invariants. Use when working on Picker, TimePicker, DatePicker, YearMonthPicker, DateRangePicker, or any public API change in this repository.
---

# Compose Picker API Stabilizer

## Overview

Use this skill before changing supported picker APIs or reviewing a branch that changes public picker behavior.

## API Principles

- Prefer controlled picker APIs with one source of truth.
- Composite pickers should expose saveable logical state plus optional `onSelected*Change` callbacks for user-driven changes.
- Keep public state APIs colocated with their component package.
- Prefer `YearMonth` for year/month-only values; keep `selectedMonthDate` as interop convenience only.
- Group repeated visual options in `PickerStyle`; group semantics in `PickerSemantics` or component-specific semantics objects; group custom item lists in component-specific item option objects.
- Keep `enabled` as a top-level interaction availability parameter.
- Do not reintroduce `startTime`, `startLocalDate`, or generic `startIndex` component parameters.
- Treat `remember*State` initial parameters as first-composition defaults, not recomposition reset hooks.

## Cross-Component Invariants

When changing one component, audit the counterpart behavior in all relevant components:

- `contains`, `coerce*`, validation, rendered item filtering, and selection repair.
- Exact inclusive min/max constraints for time, date, and year-month values.
- Empty lists, duplicates, unsupported ranges, and current-selection presence validation.
- Programmatic `state.select*` calls and scroll synchronization.
- Visible format vs content description format vs structural semantics labels.
- Disabled behavior: block scroll/click/custom semantics actions while selected values remain visible.

## Required Public API Checklist

For intentional public API changes:

1. Update KDoc on every new or changed public symbol.
2. Update `README.md` and `README_KO.md` with usage, failure mode, and state-clamping guidance.
3. Update `CHANGELOG.md`.
4. Run `./gradlew :datetimepicker:updateLegacyAbi --no-daemon`.
5. Review `datetimepicker/api/` dumps for intended symbols only. Reject accidental `*Preview` or generated resource API churn.
6. Run `./gradlew :datetimepicker:checkLegacyAbi --no-daemon`.

## Test Targets

Prefer focused tests over broad churn:

- Common unit tests for pure date/time coercion, validation, item filtering, and state mutation contracts.
- Robolectric tests for Compose semantics, state restoration, disabled behavior, and custom labels/actions.
- Sample compile or smoke tests only when sample-facing behavior changes.

Do not claim API stabilization is complete unless tests, docs, ABI dumps, and samples agree.
