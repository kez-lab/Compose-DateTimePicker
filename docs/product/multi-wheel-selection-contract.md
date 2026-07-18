# Multi-column selection contract

## Slice hypothesis

The minimum reusable model shared by `DatePicker` and `TimePicker` is not a public dependency-graph
DSL. It is a deterministic logical-value transition:

```text
current valid logical value + typed column change
  -> preset constraint repair
  -> next valid logical value
  -> one state commit
  -> one user callback with the committed value
```

The transition owns dependency and repair rules. Visual column order does not participate in the
transition. Child wheels keep the settled-only `Picker` contract for this slice. If an upstream
column settles while a dependent wheel is still moving, the dependent item source and its
`LazyListState` are replaced; that invalidated gesture is cancelled without a callback. If the
dependent wheel settles first, its valid value commits first and the later upstream commit repairs
from that value. Settlement order therefore decides which valid transitions commit, but no
impossible intermediate logical value is emitted.

A direct column change is accepted only when that value still belongs to the active constrained
source. A late value that is no longer selectable in the active source is a no-op; this slice does
not track source provenance or reject an old-source value that is also valid in the new source. Once
accepted, Date repair preserves the changed column and repairs `year -> month -> day`; Time repair
preserves the changed column and repairs `period -> hour -> minute`. Numeric dependent values use
nearest distance and prefer the smaller value on an equal-distance tie.

Programmatic state changes do not enter the user-transition path and therefore dispatch no user
callback. Replacing item sources is also app-driven: the app must coerce its logical value with the
new item object before composition. Empty, duplicate, out-of-range, or selection-omitting sources
remain configuration errors and fail before child wheels are composed.

## Acceptance criteria

- A Date column change and its dependent month/day repair produce one selectable `LocalDate` and one
  callback.
- A Time column change and its dependent period/hour/minute repair produce one selectable
  `LocalTime` and one callback.
- The state is committed before the callback, so reading the state from the callback returns the
  callback value.
- A no-op user change, app-driven state change, or compatible item-source replacement dispatches no
  callback.
- Replacing the state object or item-source semantics recreates the child-wheel generation, so an
  in-flight interaction from the previous generation cannot commit into the replacement.
- Programmatic multi-field state updates are applied in one Compose mutable snapshot, preventing
  observers from receiving a partially updated logical value.
- Non-default visual column order yields the same repaired logical value.
- Sequential completion of changes from different columns never commits a value rejected by the
  active item constraints.
- Normal month/day repair does not rescan, sort, or materialize the 9,000-year source. Full item
  configuration validation is remembered by `items`; selection validation can still perform a
  linear current-year membership check before inspecting dependent month/day lists. Component-level
  startup and interaction cost therefore remains an Android benchmark question rather than a claim
  inferred from the pure helper test.
- Empty dependent selections have an explicit configuration-error path covered by focused tests.
- KDoc, both READMEs, and `CHANGELOG.md` describe the atomic callback in one sentence.

## Public API gate

This slice deliberately adds no public `MultiWheelPicker<State>` surface. Date and Time must first
pass the same transition contract without erased item types, a graph DSL, manual invalidation, or
custom synchronization machinery. The next API proposal must reuse this proven model and also
explain how live preview and all-columns-settled events work when dependent sources change.

## Follow-up audit

- `YearMonthPickerState` and `DateRangePickerState` still need the same one-snapshot logical update
  audit before they can claim this contract.
- Generic `Picker` still needs a focused slice for disabling during an active animation; the wheel
  must recenter to its controlled value rather than leaving its visual center and semantics state
  apart.
- Infinite mode with a one-value source currently renders repeated visible rows whose equal values
  all satisfy selected semantics. Audit single-value rendering so accessibility exposes one logical
  selection rather than several selected duplicates.
- A public engine remains gated on Duration and Quantity/Unit proving the same late-invalid-value,
  dependency, restore, and settled-event model without preset-specific exceptions.
