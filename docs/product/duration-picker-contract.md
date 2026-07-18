# DurationPicker scalar constraint 계약

> 상태: Unreleased 공개 contract 기준선
> 작성일: 2026-07-18
> 제품 방향: [`wheel-picker-engine-direction.md`](wheel-picker-engine-direction.md)

## 설계 가설

`DurationPicker`는 단순히 hour/minute wheel 두 개를 나란히 놓는 component가 아니다. 앱이 두 column을
직접 동기화하지 않아도 하나의 non-negative, whole-minute `Duration`이 exact inclusive min/max와 custom
hour/minute item source를 항상 만족하도록 만드는 첫 non-date scalar preset이다.

이 slice가 유효하려면 기존 `TimePicker` 코드를 이름만 바꾸지 않고 다음 차이를 증명해야 한다.

- hour는 time-of-day의 `0..23`이 아니라 elapsed whole hours다.
- hour 변경은 현재 minute를 가능한 한 보존하되 scalar bound를 벗어나면 결정적으로 가장 가까운 minute로
  repair한다.
- app-driven selection과 user-settled selection callback을 구분한다.
- 저장·복원 값도 현재 item/constraint source로 normalize할 수 있다.

## 이번 slice의 public surface

- `DurationPicker`와 `DurationPickerState`
- `rememberDurationPickerState(...)` 및 items-aware overload
- `DurationPickerItems`, `DurationPickerConstraints`, `contains`, `coerceDuration`
- `DurationPickerColumn`, `DurationPickerLayout`, `DurationPickerFormat`, `DurationPickerSemantics`
- 대응하는 `PickerDefaults` factory

논리 값은 `kotlin.time.Duration`을 사용한다. State와 constraint boundary는 finite, non-negative,
whole-minute 값만 받는다. 이번 hour/minute preset에서 음수, sub-minute precision, infinite duration을
조용히 잘라내지 않고 `IllegalArgumentException`으로 거부한다.

## 선택과 repair 계약

1. `DurationPickerItems.contains(value)`는 value의 whole-hour/minute parts가 item source에 있고 전체
   duration이 inclusive constraint 안에 있을 때만 `true`다.
2. `coerceDuration(value)`는 전체 selectable scalar 중 절대 거리가 가장 가까운 값을 반환하고, 거리가 같으면
   더 작은 duration을 선택한다.
3. hour column이 settle되면 요청 hour를 유지하고 기존 minute를 우선한다. 그 minute가 새 hour에서 불가능하면
   같은 hour의 가장 가까운 selectable minute로 repair한다.
4. minute column은 현재 hour에서 실제로 selectable인 값만 commit한다.
5. repair된 logical value를 state에 먼저 commit한 뒤 `onSelectedDurationChange`를 정확히 한 번 호출한다.
6. app이 `state.selectDuration(...)`을 호출하거나 복원된 값을 normalize할 때 user callback을 호출하지 않는다.
7. active item source에서 사라진 late child value는 현재 logical value를 덮어쓰지 않는다.
8. app-driven logical selection 또는 upstream repair가 진행 중인 child interaction을 대체하면 이전
   interaction generation을 폐기해 late settle이 새 state를 덮어쓰거나 callback을 추가로 내지 않는다.

## 명시적 non-goals

- day/second/millisecond column
- negative duration 또는 countdown direction
- 임의 dependency graph를 받는 public `MultiWheelPicker<State>`
- composite column의 scroll 중 live logical callback
- 현재 공개 artifact 또는 repository rename

이 항목들은 이번 slice를 불완전하게 만드는 누락이 아니라, scalar contract를 먼저 검증하기 위한 경계다.
Second precision이나 추가 column은 실제 first-use 요구와 반복 가능한 core 모델이 확인된 뒤 별도 slice에서
평가한다.

## Acceptance criteria

- default 및 custom hour/minute item source의 empty, duplicate, range, unsatisfiable constraint를 검증한다.
- exact inclusive min/max 경계, `contains`, `coerceDuration`, equal-distance tie-break를 common test로 고정한다.
- hour/minute 변경 repair가 항상 selectable `Duration`을 반환하고 stale value를 no-op 처리한다.
- `DurationPickerState`의 programmatic selection과 items-aware selection/restore를 검증한다.
- Android Compose test에서 user settle이 repaired state commit 뒤 callback을 한 번만 호출함을 검증한다.
- in-flight user animation과 programmatic selection/upstream repair가 겹쳐도 최종 logical value와 callback
  count가 결정적임을 manual-clock Android Compose test로 검증한다.
- disabled picker는 선택값과 disabled semantics를 유지하면서 user action을 막는다.
- sample은 전체 90분 상한, 5분 step, preset programmatic selection을 보여준다.
- KDoc, README.md, README_KO.md, CHANGELOG.md, sample, ABI dump가 같은 계약을 설명한다.
- `:datetimepicker:check`, `:datetimepicker:checkLegacyAbi`, Desktop/iOS compile, Wasm distribution,
  sample Android test packaging과 `git diff --check`를 통과한다.
