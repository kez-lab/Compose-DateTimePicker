# Constraint-aware Wheel Picker Engine 장기 목표

이 문서는 Compose-DateTimePicker의 장기 Codex goal을 다시 시작할 때 사용하는 기준 프롬프트다. 제품 판단과
시장 근거는 [`docs/product/wheel-picker-engine-direction.md`](../product/wheel-picker-engine-direction.md)를 먼저
읽는다.

```text
Goal: Compose-DateTimePicker를 date/time 전용 component 모음에서, 서로 의존하는 여러 열을 하나의 유효한
논리 상태로 유지하는 constraint-aware Compose Multiplatform wheel selection engine으로 발전시킨다.

기존 TimePicker, DatePicker, YearMonthPicker, DateRangePicker와 공개 API를 버리고 다시 만들지 않는다. 이들은
engine의 temporal presets이자 회귀 기준으로 유지하고 점진적으로 공통 core 위로 이전한다. generic single-column
WheelPicker<T>, atomic/dependent MultiWheelPicker<State>, DurationPicker, QuantityUnitPicker를 단계적으로 검증한다.
단순 wheel rendering이나 이름 변경은 차별화로 간주하지 않는다.

작업 시작:
- 한국어로 소통한다.
- AGENTS.md, git status, branch/worktree topology, open PR, README/CHANGELOG/API dump, repo-local skills,
  docs/product/wheel-picker-engine-direction.md를 확인한다.
- current checkout에 unrelated dirty work가 있으면 origin/main에서 별도 worktree와 feature/* branch를 만든다.
- main은 integration branch이며 develop은 사용하지 않는다.

실행 원칙:
- 한 번에 하나의 PR-sized slice만 활성화한다.
- 각 slice는 local verification, commit, push, PR, GitHub mergeable=MERGEABLE 확인, merge attempt까지 수행한다.
- hosted PR automation은 기본 gate가 아니다. 명시 요청이 있을 때만 manual workflow_dispatch를 실행한다.
- Maven Central publish, GitHub Release, artifact/package/repository 이름 변경은 별도 명시 승인 없이는 수행하지 않는다.
- framework primitive로 해결 가능한 문제에 bespoke cache, invalidation, synchronization을 추가하지 않는다.
- public API 변경은 KDoc, README.md, README_KO.md, CHANGELOG.md, ABI dump, focused tests를 같은 slice에서 갱신한다.
- substantial implementation 뒤에는 가능한 여섯 agent feedback loop, 수정, 재검증, red-team을 수행한다.

우선순위:
1. 시장/경쟁/baseline 문서와 10/100/10,000 item 및 9,000-year Android benchmark를 유지하고 물리 기기
   성능 증거를 수집한다. emulator dry run을 성능 수치로 주장하지 않는다.
2. WheelPicker<T>의 live change/settled callback과 기존 Picker<T>의 settled-only 호환 계약, programmatic sync,
   item identity 변경, bounded/infinite mode, semantics를 test로 유지한다.
3. WheelPicker<T>와 Picker<T>가 callback 의미를 섞지 않으면서 rendering/validation/style/semantics 내부 경로를
   공유하도록 유지하고 ABI·양언어 문서·sample을 함께 검증한다.
4. Date/Time에서 반복되는 dependent item filtering, constraint, deterministic repair, atomic logical callback 규칙을
   추출한다. non-default column order, source identity, restored value, intermediate scroll overwrite를 검증한다.
5. MultiWheelPicker<State>는 처음부터 거대한 graph DSL로 공개하지 않는다. 최소 두 concrete preset에서 같은
   모델이 반복되고 검증된 뒤 public surface를 확정한다.
6. DurationPicker로 여러 열이 하나의 scalar min/max를 지키는 계약을, QuantityUnitPicker로 unit 변경이 item,
   step, format을 바꾸는 계약을 검증한다. 날짜가 아닌 representative sample, docs, tests를 포함한다.
7. Android, Desktop, Wasm, iOS compile, accessibility semantics, state restoration, exact constraints, ABI gate를
   통과한다.
8. 외부 first-use와 pilot evidence를 수집한다. repo 내부 sample, maintainer 사용, AI review는 외부 수요 증거로
   계산하지 않는다.

완료 조건:
- generic single-column과 dependent multi-column 계약이 temporal/non-temporal presets에서 일관되다.
- representative samples, KDoc, README/README_KO, CHANGELOG, ABI dump, targeted unit/UI tests가 동기화돼 있다.
- 관련 Android/Desktop/Wasm/iOS local verification이 통과한다.
- 서로 독립적인 Compose 개발자 3명이 문서만으로 first-use task를 수행한 기록이 있다.
- 외부 앱/프로젝트 1곳의 pilot 또는 adoption 기록이 있다.
- 각 외부 증거에는 성공뿐 아니라 혼동, 실패, 경쟁 대안 대비 선택 이유가 기록돼 있다.
- 독립 개발자는 maintainer, API 설계 참여자, AI agent가 아니며, 최소 2/3명이 60분 안에 지정 task를
  maintainer의 코드 수정 없이 compile하고 state/callback 계약을 설명한다.
- pilot은 이 저장소 sample 복제가 아니라 외부 소유 앱/프로젝트의 실제 화면에서 사용되고 build된 기록이다.

마지막 두 외부 증거가 없으면 goal을 complete로 표시하지 않는다. 기술 구현이 끝났더라도 정확한 남은 blocker를
기록하고 계속 actionable slice를 진행한다.
```

## 권장 PR 순서

1. 시장/제품 방향 기준선과 benchmark harness
2. single-column `WheelPicker<T>` live/settled contract
3. dependent column internal contract
4. `DurationPicker` vertical slice
5. `QuantityUnitPicker` vertical slice
6. combined `DateTimePicker` vertical slice
7. 반복이 증명된 최소 `MultiWheelPicker<State>` public API
8. external first-use task kit와 pilot evidence

각 단계는 앞 단계에서 발견한 반증에 따라 축소하거나 중단할 수 있다. 목표는 많은 API를 공개하는 것이 아니라,
실제 앱이 직접 구현하기 어려운 제약과 동기화를 더 적은 glue code로 안전하게 해결하는 것이다.
