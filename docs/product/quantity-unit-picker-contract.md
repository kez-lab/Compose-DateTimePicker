# QuantityUnitPicker dependent-source 계약

> 상태: sample vertical slice 설계 기준선
> 작성일: 2026-07-18
> 제품 방향: [`wheel-picker-engine-direction.md`](wheel-picker-engine-direction.md)

## 설계 가설

`QuantityUnitPicker`는 단순히 숫자 wheel 옆에 unit wheel을 놓는 예제가 아니다. unit 변경이 quantity의
item source, step, 표시 형식과 접근성 설명을 함께 바꾸더라도 앱이 하나의 유효한 logical selection만
관찰하게 만드는 첫 fully non-temporal dependent-source proof다.

이번 slice는 일반적인 단위 환산기가 아니라, 허용 weight bucket이 unit별로 다른 constrained selection을
대표 task로 사용한다.

- gram source: `100..5000`, 100 g step
- kilogram source: `1..5`, 1 kg step
- logical scalar: gram으로 정규화한 mass

예를 들어 `2500 g`에서 unit을 `kg`로 바꾸면 2 kg와 3 kg의 거리가 같으므로 더 작은 2 kg를 선택한다.
이 repair는 unit wheel의 시각적 위치가 아니라 scalar distance와 tie-break 규칙으로 결정한다.
따라서 unit 전환이 normalized mass를 바꿀 수 있다. 의도적으로 거친 integer grid로 이 동작을 드러내며,
정밀한 mass conversion 또는 production form UX를 증명한다고 해석하지 않는다.

## 구현 경계

이번 slice는 sample module 안에 다음 계약을 구현하고 검증한다.

- `QuantityUnitSelection`: quantity와 unit을 한 번에 보유하는 logical value
- `QuantityUnitItems`: unit별 quantity source와 validation, `contains`, deterministic coercion
- saveable `QuantityUnitPickerState`: 하나의 selection만 mutable state로 소유
- settled-only `Picker` 두 개를 조합한 sample-local `QuantityUnitPicker`
- state-first atomic user callback과 callback-free programmatic selection/restore
- unit 변경 시 scalar mass를 가장 가까운 새 source로 repair
- dynamic quantity formatting/content description과 unit별 semantics label

`coerceSelection`은 requested unit이 계속 configured되어 있으면 그 unit을 우선 보존한 채 해당 source에서
가장 가까운 scalar를 선택한다. requested unit이 제거된 경우에만 모든 configured unit에서 normalized mass가
가장 가까운 selection을 찾는다. 반면 명시적인 unit wheel 변경은 새 unit source 안에서만 repair한다.

`rememberQuantityUnitPickerState`의 `items`와 `initialSelection`은 state가 처음 생성될 때만 읽는다. 같은
composition에서 source를 교체하는 앱은 새 source를 넘기기 전에 `state.selectSelection(value, newItems)`로
logical value를 repair해야 한다. `QuantityUnitItems`에 전달한 list/map은 생성 뒤 변경하지 않는 immutable
configuration으로 취급한다.

Date/Time/Duration에서 반복된 seam인 `active items -> typed change -> repaired logical value -> state commit
-> one callback`을 sample에서 재현한다. DateTime interaction test까지의 반복은 public surface 논의를 시작하기
위한 필요조건일 뿐 충분조건이 아니다. 별도 private shared prototype과
[`date-time-picker-contract.md`](date-time-picker-contract.md)의 남은 public engine gate를 통과한 뒤에만
`MultiWheelPicker<State>` surface를 결정한다.

## 명시적 non-goals

- 이번 slice에서 library public `QuantityUnitPicker` 또는 `MultiWheelPicker<State>` API 공개
- arbitrary unit-conversion framework, decimal parser 또는 localization engine
- negative quantity, free-form text entry, searchable unit catalog
- live composite logical callback
- 현재 artifact/package/repository 이름 변경
- sample의 whole-subtree `key` cancellation 방식을 public engine primitive로 승격

현재 source는 최대 50개인 correctness-first bounded sample이다. logical selection마다 child subtree를
재생성해 stale interaction을 폐기하는 방식은 큰 source에서 text measurement와 scroll state를 반복 생성할 수
있다. Public engine은 race test와 10/100/10,000-item benchmark를 거쳐 structured cancellation과 measurement
state 보존을 별도로 설계해야 한다.

sample-only로 시작하는 이유는 public preset 수요가 아직 외부 first-use로 검증되지 않았기 때문이다. 계약이
유용하다는 증거 없이 unit별 예외 API를 Maven artifact에 고정하지 않는다.

## Acceptance criteria

- unit source와 각 quantity source의 empty, duplicate, missing mapping, non-positive value를 거부한다.
- `contains`와 coercion이 unit별 source/step을 지키고 scalar tie에서 더 작은 normalized mass를 선택한다.
- unit 변경은 current scalar mass를 가장 가까운 새-unit quantity로 repair한다.
- quantity 변경은 현재 unit의 active source에 있는 값만 commit하고 late invalid value는 no-op 처리한다.
- logical state는 한 개의 selection으로 갱신되고 save/restore 및 items-aware programmatic selection을 지원한다.
- user-settled change는 state commit 뒤 callback을 한 번만 호출하며 programmatic 변경은 callback을 호출하지 않는다.
- unit/programmatic 변경 시 이전 child interaction generation을 폐기한다. Combined DateTime slice는
  programmatic selection이 여전히 유효한 in-flight minute target에 덮어써지지 않는 한 사례를 검증한다.
  upstream/dependent 동시 interaction과 source replacement 충돌은 shared public engine gate로 남긴다.
- sample은 `2500 g -> kg = 2 kg` tie repair와 callback count를 사람이 확인할 수 있게 보여준다.
- Android sample smoke, common/Desktop tests, Desktop/iOS compile, Wasm distribution과 diff hygiene를 통과한다.
- README.md, README_KO.md, CHANGELOG.md는 이 기능이 sample proof이며 아직 library public API가 아님을 명시한다.
