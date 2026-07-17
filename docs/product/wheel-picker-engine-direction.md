# Constraint-aware Wheel Picker Engine 제품 방향

> 상태: 제품 가설 및 구현 순서 기준선
> 근거 스냅샷: 2026-07-17
> 범위: 저장소 이름, Maven 좌표, 공개 API를 즉시 변경하는 결정이 아니라 다음 구현 slice의 방향을 고정한다.

## 결론

이 프로젝트를 폐기하고 새 generic wheel picker로 다시 만드는 것은 권장하지 않는다. 기존의 date/time API,
정확한 min/max constraint, saveable state, programmatic selection sync, semantics 경험을 보존하면서 그 아래에
**constraint-aware multi-column wheel selection engine**을 추출하는 편이 낫다.

단순히 `Picker<T>`를 `WheelPicker<T>`로 바꾸거나 임의의 item을 표시하게 만드는 것만으로는 시장성이 충분하지
않다. 이 저장소에는 이미 controlled generic `Picker<T>`가 있고, 외부에도 더 알려진 Android wheel picker와
활발한 KMP generic wheel picker가 존재한다. 제품이 이길 수 있는 가설은 다음 한 문장이다.

> 서로 의존하는 여러 선택 열을 앱이 직접 동기화하지 않아도, 제약에 맞는 하나의 논리 상태로 안전하게
> 유지하는 Compose Multiplatform wheel selection engine.

기존 `TimePicker`, `DatePicker`, `YearMonthPicker`, `DateRangePicker`는 버릴 대상이 아니라 이 engine의 첫
presets이자 회귀 검증 기준이다. 이후 `DurationPicker`와 `QuantityUnitPicker`처럼 날짜가 아닌 실제 사용 사례를
추가해 범용성을 증명한다.

## 냉정한 시장 판정

### 확인된 사실

- Android의 공식 Material Compose date picker 문서는 calendar/input 계열을, time picker 문서는 dial/input
  계열을 안내한다. 이 두 공식 component 문서에는 generic wheel selection 대안이 없다.
- Compose Multiplatform은 Android, iOS, Desktop, Web을 지원하므로 공통 selection UI를 원하는 앱에는
  KMP 라이브러리의 구조적 이점이 있다.
- `commandiron/WheelPickerCompose`는 Android에서 date/time wheel 수요가 존재했음을 보여주지만 최근 개발
  활동은 오래됐다.
- `darkokoa/compose-datetime-wheel-picker`는 KMP date/time wheel 영역에 이미 활발한 경쟁자가 있음을 보여준다.
- `software-mansion-labs/kmp-wheel-picker`는 modular generic KMP wheel core도 이미 경쟁 영역임을 보여준다.
- `zj565061763/compose-wheel-picker`는 scroll 중 snapshot과 settled selection을 구분하는 Android generic
  API를 제공한다. 따라서 callback 단계 구분도 그 자체로 독점적 차별점은 아니다.

2026-07-17 GitHub 공개 지표 스냅샷은 다음과 같다. star, fork, issue 수는 설치 수나 지불 의사를 증명하지
않는 **인지도·활동성 proxy**일 뿐이다.

| 저장소 | 범위 | stars / forks | 최근 push 또는 release | 해석 |
|---|---|---:|---|---|
| `kez-lab/Compose-DateTimePicker` | KMP temporal + generic `Picker<T>` | 24 / 2 | push 2026-07-17, public release 0.4.0 (2025-11-12) | 기술 기반은 있으나 외부 인지도는 낮다. |
| `commandiron/WheelPickerCompose` | Android temporal wheel | 620 / 80 | release 1.1.11 (2023-06-05), push 2024-05-16 | Android wheel 수요의 강한 과거 proxy다. |
| `darkokoa/compose-datetime-wheel-picker` | KMP temporal wheel | 259 / 24 | release v1.3.3 (2026-07-01) | 현재 temporal 정면 경쟁자다. |
| `software-mansion-labs/kmp-wheel-picker` | KMP generic wheel core | 85 / 1 | release v0.3.0 (2026-01-08) | 단순 generic core 전략의 직접 경쟁자다. |
| `zj565061763/compose-wheel-picker` | Android generic wheel | 165 / 12 | release 1.0.0-rc02 (2024-11-27) | live/settled selection을 포함한 Android 대안이다. |

### 아직 증명되지 않은 추론

다음은 공개 지표와 API 비교에서 나온 제품 가설이지, 실제 사용자 인터뷰로 확인된 사실이 아니다.

1. 앱 개발자는 wheel의 시각 효과보다 **dependent column constraint**를 직접 구현하지 않는 데 더 큰 가치를
   느낄 수 있다.
2. temporal picker에서 축적한 exact bounds, coercion, restoration 계약은 duration, quantity/unit,
   configuration picker에도 이전될 수 있다.
3. Android뿐 아니라 iOS/Desktop/Web까지 동일한 논리 동작을 원하는 팀은 platform-native 외형보다 공통
   state contract를 선택할 수 있다.

이 가설을 외부 개발자 증거 없이 “시장 검증 완료”로 표현하면 안 된다. 현재 제품성 평가는 **유망하지만
미검증**이다. 특히 지금 인지도 상태에서 이름만 `WheelPicker`로 바꾸면 검색 경쟁은 더 치열해지고 기존
temporal 사용자에게 주던 명확성까지 잃을 수 있다.

## 경쟁 지형에서의 빈자리

경쟁자들이 wheel rendering, item content, animation, programmatic scroll, date/time presets을 제공하므로
다음 항목만으로는 충분한 차별화가 아니다.

- 세로 wheel 렌더링
- infinite/bounded scroll
- selected item style
- custom row composable
- date/time preset
- programmatic scroll
- live/settled callback 하나의 기능

이 프로젝트가 집중할 빈자리는 아래 계약의 결합이다.

| 문제 | 제품 계약 |
|---|---|
| 앞 열 선택에 따라 뒤 열의 item이 달라짐 | 모든 열을 하나의 논리 `State`에서 파생하고 dependency를 명시한다. |
| item 변경 순간 기존 선택이 사라짐 | public `contains` / `coerce`와 같은 결정적 repair rule을 적용한다. |
| 여러 열 callback이 중간 불가능 상태를 노출함 | 한 interaction을 atomic logical selection으로 커밋한다. |
| 앱의 preset/복원 값과 scroll 위치가 충돌함 | programmatic state update가 settle될 때까지 중간 scroll index가 값을 덮지 않는다. |
| scroll 중 값과 확정 값의 용도가 다름 | live selection과 settled selection을 명시적으로 구분한다. |
| process recreation 뒤 선택이 달라짐 | 저장 가능한 논리 값과 표시 열 상태를 분리하고 restore 후 동일 constraint로 repair한다. |
| 접근성 서비스가 열 관계를 알기 어려움 | 열 label, 현재 값, previous/next action, disabled state를 구조적으로 제공한다. |

개별 항목은 경쟁자가 구현할 수 있다. 차별점은 이 계약들을 temporal presets과 non-temporal presets에서
일관되게 검증하고 문서화하는 데 있다.

## 목표 사용자와 사용 사례

### 우선 사용자

- Android/iOS를 Compose Multiplatform으로 함께 개발하는 작은 제품팀
- form 또는 configuration 화면에서 2~4개의 의존하는 열을 사용하는 개발자
- restored/preset 값, 정확한 bounds, 접근성 때문에 단순 wheel snippet 이상이 필요한 개발자

### 우선 사용 사례

1. `DatePicker`, `TimePicker`, `YearMonthPicker`, `DateRangePicker`: 기존 API를 engine 위에서 유지한다.
2. `DurationPicker`: day/hour/minute 또는 hour/minute/second 단위가 전체 min/max duration을 지킨다.
3. `QuantityUnitPicker`: 선택 unit에 따라 허용 quantity/step/format이 달라진다.
4. 앱 전용 dependent picker: 예를 들어 product/category, region/city처럼 앞 열에 따라 뒤 열이 달라진다.

4번은 engine의 표현력을 검증하는 예제이지, 대형 searchable catalog를 wheel로 해결하겠다는 의미가 아니다.

### 명시적 non-goals

- 수천 개의 비정렬 문자열을 탐색하는 search/dropdown 대체재
- country, file, image, contact처럼 검색·미리보기·권한 흐름이 핵심인 selector
- 모든 플랫폼의 native picker 외형을 완전히 복제하는 것
- 외부 증거 전에 Maven artifact, package, repository 이름을 전면 변경하는 것
- 날짜 구현에서 바로 추상화한 거대한 public multi-column DSL을 한 번에 공개하는 것

wheel은 선택지가 짧고 순서가 있으며 인접 값 탐색이 유용할 때 적합하다. 이 조건을 만족하지 않는 문제까지
제품 범위에 포함하면 범용성은 늘지 않고 UX 품질만 떨어진다.

## 제품 구조

```text
wheel rendering + interaction primitives
  └─ controlled single-column WheelPicker<T>
       └─ atomic, constraint-aware MultiWheelPicker<State>
            ├─ temporal presets
            │    ├─ TimePicker
            │    ├─ DatePicker / YearMonthPicker
            │    └─ DateRangePicker
            └─ non-temporal presets
                 ├─ DurationPicker
                 └─ QuantityUnitPicker
```

구조의 방향은 계층이지 상속이 아니다. 상위 picker는 controlled wheel들을 조합하고 논리 state와 constraint
정책을 소유한다.

### 1. single-column core

현재 `Picker<T>`의 검증된 기능을 기준으로 다음을 명확히 한다.

- caller-owned `selectedItem` 단일 source of truth
- immutable item list 계약과 selected item membership 검증
- bounded/infinite mode
- programmatic selection과 내부 scroll synchronization
- live/settled callback 의미
- item equality/identity, duplicate, source replacement의 실패 계약
- `PickerItemFormat`, `PickerStyle`, `PickerSemantics`, custom item content의 역할 분리
- 큰 item list의 materialization/measurement 비용과 지원 범위

`WheelPicker<T>`는 live `onSelectedItemChange`와 별도 `onSelectionSettled`를 제공하는 새 controlled API로
추가한다. 기존 `Picker<T>`는 callback을 settle 뒤에만 전달하는 호환 API로 유지한다. 두 API의 callback
계약이 다르므로 typealias나 단순 이름 변경으로 위장하지 않고 rendering, validation, semantics, style,
programmatic synchronization만 같은 내부 구현을 공유한다. 기존 temporal picker는 dependent column이 scroll
중 재구성되지 않도록 settled-only `Picker` 계약을 계속 사용한다.

### 2. multi-column contract

public API보다 먼저 아래 invariant를 concrete preset에서 검증한다.

- 임의 순간의 logical state는 전체 constraint를 만족한다.
- 한 열의 변경으로 다른 열의 item이 바뀌면 repair 결과는 결정적이다.
- repair 중 발생하는 내부 scroll은 사용자 callback을 중복 발행하지 않는다.
- app-driven state 변경과 user-driven 변경의 callback 계약이 구분된다.
- live 값은 preview에, settled 값은 expensive side effect에 사용할 수 있다.
- column order가 달라도 dependency와 repair 결과는 동일하다.
- 두 열을 동시에 scroll하거나 한 열이 settle되기 전에 다른 열을 조작해도 불가능한 중간 상태를 commit하지
  않는다.
- constraint 적용 뒤 한 열의 selectable item이 비면 구성 오류와 runtime source 변경의 실패 계약이 명확하다.
- save/restore 뒤에도 동일한 logical state 또는 문서화된 coerced state를 얻는다. arbitrary `T`가 자동으로
  saveable하다고 가정하지 않으며 generic API는 caller-owned key/value 또는 명시적 `Saver`가 필요하다.

`MultiWheelPicker<State>`를 처음부터 임의의 graph DSL로 만들지 않는다. Date/Time/Duration/QuantityUnit 네
종류에서 반복되는 최소 모델이 확인된 뒤 public surface를 고정한다.

### 3. presets

기존 temporal API는 source compatibility를 가능한 한 유지한 채 내부 engine으로 점진 이전한다. public
behavior가 달라지는 slice는 README, README_KO, CHANGELOG, KDoc, ABI dump, targeted tests를 함께 갱신한다.

`DurationPicker`와 `QuantityUnitPicker`는 “날짜 아닌 것도 된다”는 데모용 장식이 아니다. 각각 다음 다른
계약을 검증해야 한다.

- `DurationPicker`: 여러 열이 하나의 연속된 scalar bound를 지키는가?
- `QuantityUnitPicker`: category/unit 변경이 item set, step, formatting을 안전하게 바꾸는가?

둘 중 사용자가 설명하기 어렵거나 실제 앱 task에서 필요하지 않은 preset은 public API로 승격하지 않는다.

## 실행 순서와 gate

### Phase 0 — 기준선

- 경쟁 프로젝트와 공식 component gap을 문서화한다.
- 10/100/10,000 generic item 및 9,000-year date startup benchmark harness를 유지한다.
- 물리 Android 기기 측정 전에는 emulator dry run을 성능 수치로 인용하지 않는다.

### Phase 1 — core contract

- 기존 `Picker<T>`의 settled-only selection과 programmatic sync, item source 변경 동작을 test로 고정한다.
- `WheelPicker<T>`의 live change와 settled callback, 기존 `Picker<T>`의 settled-only 호환 계약을 API/ABI와
  interaction test로 고정한다.
- Android, Desktop, Wasm, iOS compile gate와 semantics regression을 통과한다.

### Phase 2 — dependent columns

- Date/Time에서 반복되는 dependency, filtering, coercion, atomic callback 규칙을 추출한다.
- non-default column order, source identity 변경, restore, intermediate scroll overwrite를 red-team한다.
- public generic API는 concrete preset 최소 두 종류에서 같은 모델이 증명된 뒤에만 노출한다.

### Phase 3 — non-temporal proof

- `DurationPicker`와 `QuantityUnitPicker`의 representative sample, KDoc, tests를 만든다.
- 처음 보는 개발자가 문서만으로 state/constraint/callback 계약을 설명하고 사용할 수 있는지 확인한다.

### Phase 4 — 외부 수요 검증

완료 판정에는 다음 증거가 모두 필요하다.

1. 서로 독립적인 Compose 개발자 3명이 first-use task를 수행한 기록
2. 외부 앱/샘플/프로젝트 1곳에서 pilot 또는 adoption한 기록
3. 각 참여자에게 “왜 기존 경쟁자나 직접 구현 대신 이것을 선택했는가”를 묻고 답을 보존
4. 실패, 혼동, 포기 사유도 성공 사례와 함께 기록

독립 개발자는 maintainer, 이 API 설계 참여자, AI agent가 아닌 사람을 뜻한다. 각 개발자는 문서와 배포 가능한
artifact 또는 명시된 commit만 받아 60분 안에 지정 task 하나를 compile하고 state/callback 계약을 설명한다.
최소 2/3명이 maintainer의 코드 수정 없이 성공해야 usability gate를 통과한다.

pilot은 이 저장소의 sample 복제가 아니라 외부 소유 앱/프로젝트의 실제 form 또는 configuration 화면에서 한
preset이나 dependent constraint를 사용하고 build된 기록이어야 한다. commit/version, 사용 사례, 선택 이유,
발견한 문제를 남긴다. 공개 저장소일 필요는 없지만 참여자 동의 없이 개인·회사 식별 정보나 소스 코드를
저장하지 않는다. 정리된 증거는 `docs/product/evidence/` 아래에 보존한다.

maintainer, AI review, sample app, repository 내부 test는 품질 증거지만 외부 수요 증거를 대체하지 않는다.

## 검증 질문과 중단 기준

### 인터뷰보다 강한 task

외부 개발자에게 기능 목록을 보여주고 호감을 묻지 않는다. 다음 작업을 문서만 제공한 상태로 수행하게 한다.

- min/max와 5분 step이 있는 시간 picker를 preset/restore와 함께 구성한다.
- 전체 90분을 넘지 않는 hour/minute duration picker를 구성한다.
- unit 변경 시 quantity step과 표시가 바뀌는 picker를 구성한다.
- scroll 중 preview와 settle 후 network/query side effect를 분리한다.

측정값은 task 성공 여부, 첫 compile까지 시간, 잘못 이해한 callback/state contract, 작성한 glue code 양,
접근성 label 누락 여부다.

### pivot 또는 중단 조건

다음 중 하나가 반복되면 public multi-column engine 확대를 멈추고 범위를 재검토한다.

- 개발자 3명 중 2명 이상이 generic competitor + 앱 glue code를 더 단순하다고 판단한다.
- dependent constraint 모델을 설명하기 위해 preset별 예외 API가 계속 늘어난다.
- app-driven update와 user interaction을 안정적으로 구분하려면 custom synchronization machinery가 과도하게
  필요하다.
- non-temporal 사례가 wheel UX에 부적합하거나 temporal code를 억지로 일반화해야만 구현된다.
- 물리 기기 benchmark에서 큰 item set의 초기 렌더링 비용이 문서화 가능한 수준을 넘고 standard Compose
  primitive로 해결되지 않는다.

## 다음 구현 slice의 acceptance criteria

다음 multi-column slice는 새 public graph DSL을 바로 추가하는 작업이 아니다. 먼저 Date/Time concrete
picker에서 atomic/dependent selection 계약을 다음 증거로 고정한다.

- 한 열의 user interaction과 repair가 하나의 유효한 logical state callback만 commit하는 test
- programmatic state 변경 중 child scroll index가 요청 값을 되돌리지 않는 test
- item source identity/content 변경과 non-default column order test
- dependent item list가 비는 경우와 동시에 여러 열을 조작하는 경우의 명시적 failure/repair test
- preset의 save/restore와 semantics가 repair 뒤 논리 값을 설명하는 test
- atomic callback 이름과 발생 조건을 README/KDoc에서 한 문장으로 설명 가능
- intentional public API가 있으면 ABI dump를 업데이트하고 review

이 gate를 최소 두 concrete preset에서 통과한 뒤 `MultiWheelPicker<State>` public surface를 결정한다.

## 근거 자료

- [Android Developers — Date pickers](https://developer.android.com/develop/ui/compose/components/datepickers)
- [Android Developers — Time pickers](https://developer.android.com/develop/ui/compose/components/time-pickers)
- [JetBrains — Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform)
- [commandiron/WheelPickerCompose](https://github.com/commandiron/WheelPickerCompose)
- [darkokoa/compose-datetime-wheel-picker](https://github.com/darkokoa/compose-datetime-wheel-picker)
- [software-mansion-labs/kmp-wheel-picker](https://github.com/software-mansion-labs/kmp-wheel-picker)
- [zj565061763/compose-wheel-picker](https://github.com/zj565061763/compose-wheel-picker)
- [KMP Awesome — Compose UI](https://github.com/terrakok/kmp-awesome#-compose-ui)
