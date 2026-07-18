# DateTimePicker 5-column transition 계약

> 상태: sample vertical slice 설계 기준선
> 작성일: 2026-07-18
> 제품 방향: [`wheel-picker-engine-direction.md`](wheel-picker-engine-direction.md)

## 설계 가설

기존 `Integrated` sample처럼 `YearMonthPicker`와 `TimePicker`를 별도 탭과 별도 state로 배치하는 것은
combined date-time picker 계약을 증명하지 않는다. 이번 slice는 `year/month/day/hour/minute` 다섯 열을 하나의
`LocalDateTime` state와 exact candidate source로 묶는다.

대표 source는 자정 경계를 가로지르는 다음 30분 단위 후보다.

- `2026-02-28 23:00`, `2026-02-28 23:30`
- `2026-03-01 00:00`, `2026-03-01 00:30`
- `2026-03-01 01:00`, `2026-03-01 01:30`

따라서 `2026-02-28 23:30`에서 month를 March로 바꾸면 day/hour/minute source가 함께 바뀌고, 시간상 가장
가까운 earlier tie-break 후보인 `2026-03-01 00:00` 하나가 commit되어야 한다. 시각적 column order는 이
repair 규칙에 영향을 주지 않는다.

## 구현 경계

이번 slice는 sample module 안에 다음 계약을 구현하고 검증한다.

- `DateTimePickerItems`: 정렬된 exact `LocalDateTime` 후보와 column별 active source
- `DateTimePickerState`: saveable logical `LocalDateTime` 하나만 소유
- typed `DateTimePickerColumn` change와 deterministic downstream repair
- state-first user callback 한 번과 callback-free programmatic selection/restore
- 다섯 개의 settled-only `Picker<Int>` column 및 state-dependent format/semantics
- logical selection, candidate content, enabled 변경 시 stale child interaction generation 폐기

source는 whole-minute, distinct, strictly increasing 후보만 허용한다. `coerceDateTime`은 timezone 의미가 없는
local civil-minute ordinal 거리가 가장 가까운 값을 고르고, tie는 더 이른 값을 선택한다. 직접 column change는 현재 active
source에 남아 있는 값만 받아들인다. Direct edit는 변경 column 뒤의 field를 순서대로 exact 보존할 수 있는
동안 candidate를 좁힌다. 첫 보존 불가능 field를 만나면 남은 후보 중 civil-minute 거리가 가장 가까운 값을
선택한다. 따라서 hour `00:30 -> 01`은 minute를 보존한 `01:30`이 되고, month 변경처럼 day부터 보존할 수
없으면 전체 matching candidate 중 가장 가까운 값을 선택한다.

`rememberDateTimePickerState`의 `items`와 `initialDateTime`은 state가 처음 생성될 때만 읽는다. 같은
composition에서 source를 교체할 때는 새 source를 compose하기 전에
`state.selectDateTime(value, newItems)`로 logical value를 repair해야 한다. 전달한 candidate list는 생성 뒤
변경하지 않는 immutable configuration으로 취급한다.

Kotlin `List` 타입 자체는 immutable snapshot을 보장하지 않는다. 이 sample은 caller 규칙으로 mutation을
금지하지만, future shared prototype은 생성 시 source snapshot을 소유하거나 immutable collection 계약을
채택해야 한다. 같은 candidate content를 가진 새 wrapper instance는 새 interaction generation으로 간주하지
않는다.

## 명시적 non-goals

- 이번 slice에서 library public `DateTimePicker` 또는 generic multi-column engine API 공개
- timezone, daylight-saving transition, `Instant`, calendar-system 또는 locale framework
- arbitrary recurrence/rule DSL이나 모든 분을 materialize하는 production source
- live composite callback 또는 all-columns-settled public event 이름 확정
- sample의 whole-subtree `key` cancellation 방식을 public engine primitive로 승격
- artifact/package/repository migration

exact candidate source는 6개뿐인 correctness-first stress case다. 이 구현은 외부 first-use, adoption, market
demand 또는 production date-time API 완성 증거가 아니다.

## Acceptance criteria

- empty, duplicate, non-whole-minute, non-increasing candidate source를 거부한다.
- active year/month/day/hour/minute source가 현재 logical prefix와 exact candidate 집합에서 파생된다.
- month change가 day/hour/minute를 함께 repair해 selectable `LocalDateTime` 하나만 commit한다.
- hour change가 minute source를 repair하며 global coercion의 numeric tie는 earlier civil-minute ordinal을 선택한다.
- late invalid column value와 no-op change는 state/callback을 바꾸지 않는다.
- user-settled change는 state commit 뒤 callback 한 번을 호출한다.
- programmatic selection과 save/restore는 callback 없이 selectable value로 coerce한다.
- non-default visual column order도 동일한 logical transition을 만든다.
- in-flight child animation 뒤 programmatic selection이 이전 settle에 덮어써지지 않는다.
- Android journey 및 race test, common/Desktop tests, Desktop/iOS compile, Wasm distribution, ABI와 diff
  hygiene가 통과한다.
- README 양 언어, CHANGELOG와 product contract가 sample-only 경계 및 남은 public engine gate를 동일하게
  설명한다.

## Public engine gate

이 slice가 통과해도 public engine을 즉시 공개하지 않는다. YearMonth/DateRange atomic audit, disabled
in-flight behavior, single-value infinite semantics, live-versus-settled event proposal과 large-source
measurement/cancellation 설계가 계속 gate로 남는다. 또한 public surface를 고정하기 전에 비공개 shared
prototype 하나로 기존 slice 최소 두 개를 preset별 예외나 glue 증가 없이 재구현하고, v1이 ordered dependency
chain만 지원할지 arbitrary dependency graph까지 지원할지 명시적으로 결정해야 한다. 이 prototype은 다음
interaction/UX 증거도 통과해야 한다.

- 모든 prototype symbol은 `internal`이고 `datetimepicker/api/` public symbol delta는 0이어야 한다.
- 최소 한 temporal slice와 한 non-temporal slice가 동일한 core reducer/generation 구현과 공통 contract test
  harness를 사용해야 한다.
- core는 preset type을 검사하는 `when`, cast 또는 preset별 예외 adapter를 두지 않으며 기존 public preset
  signature를 바꾸지 않는다.
- 최소 한 slice에서 live item event와 repaired logical settled event를 실제로 분리하고, programmatic sync,
  source replacement와 stale settle이 잘못된 event를 발행하지 않는 interaction test를 통과해야 한다.

- animation 중 `enabled=false` 전환 뒤 controlled value와 visual/semantics selection이 다시 일치하는가
- container와 selected item이 screen reader에서 중복 focus/announcement를 만들지 않는가
- 실제 swipe/snap gesture가 outer scroll 및 compact width에서도 올바른 한 번의 commit으로 끝나는가
- large font와 좁은 viewport에서 다섯 열의 header, value, horizontal navigation이 사용 가능한가
- source replacement 및 upstream/dependent 동시 interaction이 stale commit을 만들지 않는가
