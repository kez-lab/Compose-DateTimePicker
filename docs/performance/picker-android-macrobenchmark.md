# Picker Android Macrobenchmark

## 목적과 가설

이 benchmark는 `Picker`의 초기 Compose 렌더링 비용이 item 수와 높이 측정 경로에 따라 어떻게 달라지는지 end-to-end Android 화면 수준에서 측정한다. 독립 `benchmark-app` target과 전용 `PickerBenchmarkActivity`를 사용하므로 일반 sample APK와 `MainActivity` startup 경로에는 benchmark 코드나 분기가 포함되지 않는다.

- generic `Picker<Int>`는 10, 100, 10,000 item에서 현재 exact text measurement 경로의 증가 비용을 드러내야 한다.
- 기본 `DatePicker`의 9,000개 연도는 library-owned numeric probe를 사용하므로, 같은 문자열을 반환하지만 custom formatter를 사용해 exact path로 강제한 경우보다 빨라야 한다.
- benchmark는 제품 public API를 추가하거나 benchmark 전용 최적화 flag를 노출하지 않는다.

이것은 사용자 화면의 cold-start end-to-end benchmark다. generic 시나리오에는 호출자가 `(1..itemCount).toList()`를 만드는 비용도 포함되므로 내부 text measurement 함수만의 microbenchmark로 해석하면 안 된다. 내부 경로의 formatter 호출 수는 common test로 별도 검증하고, 여기서는 실제 앱이 list를 준비해 첫 frame을 표시하기까지의 합성 비용을 비교한다.

## 시나리오

| Test | 화면 | 높이 측정 경로 |
|---|---|---|
| `generic10Items` | generic `Picker<Int>`, 10 items | exact |
| `generic100Items` | generic `Picker<Int>`, 100 items | exact |
| `generic10000Items` | generic `Picker<Int>`, 10,000 items | exact |
| `dateDefault9000Years` | 기본 `DatePicker`, 1000..9999 | bounded internal numeric probe |
| `dateCustomExact9000Years` | 동일 `DatePicker`, custom year formatter | exact |

각 test는 release 기반·non-debuggable·profileable `benchmark-app` APK를 대상으로 cold start를 10회 수행하고 `StartupTimingMetric`을 기록한다. `dateCustomExact9000Years`는 사용자가 custom formatter를 제공했을 때 fallback glyph를 안전하게 보존하는 현재 계약의 비교군이다. 스크롤 중 frame timing과 jank는 초기 렌더링과 다른 사용자 journey이므로 후속 interaction benchmark에서 다룬다.

## Acceptance criteria

1. `:benchmark:assembleBenchmark`와 `:benchmark-app:assembleBenchmark`가 컴파일된다.
2. emulator dry run으로 모든 scenario가 launch되고 benchmark 결과 artifact가 생성된다.
3. 최종 성능 주장은 API 34 이상의 물리 Android 기기에서 같은 commit과 명령으로 얻은 JSON을 근거로 한다.
4. 결과에는 device model, Android version, build fingerprint, commit SHA, thermal 상태 및 JSON 경로를 함께 기록한다.
5. emulator 결과는 harness 진단용으로만 쓰고 사용자 체감 성능이나 회귀 임계값으로 인용하지 않는다.

## 실행

물리 기기에서 대표 결과를 수집한다.

```bash
./gradlew :benchmark:connectedBenchmarkAndroidTest --no-daemon
```

연결 기기 없이 harness만 검증할 때는 기존 Gradle Managed Device를 dry-run으로 사용한다.

```bash
./gradlew :benchmark:pixel2Api35BenchmarkAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.androidx.benchmark.dryRunMode.enable=true \
  -Pandroid.testInstrumentationRunnerArguments.androidx.benchmark.suppressErrors=EMULATOR \
  --no-daemon
```

특정 scenario만 실행하려면 class argument를 추가한다.

```bash
./gradlew :benchmark:connectedBenchmarkAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=com.kez.picker.benchmark.PickerStartupBenchmark#generic10000Items \
  --no-daemon
```

호스트로 복사된 JSON과 Perfetto trace는 다음 디렉터리 아래에서 확인한다.

```text
benchmark/build/outputs/connected_android_test_additional_output/benchmarkAndroidTest/connected/<device>/
```

Gradle Managed Device dry run artifact는 다음 경로에 생성된다.

```text
benchmark/build/outputs/managed_device_android_test_additional_output/benchmark/pixel2Api35/
```

## 현재 결과

물리 기기 결과는 아직 없다. 이 항목은 emulator smoke 결과로 대체하지 않는다. 물리 기기가 연결되면 아래 표를 실제 JSON 값과 artifact 경로로 갱신한다.

| Commit | Device / Android | generic 10 / 100 / 10,000 | date bounded / exact | Evidence |
|---|---|---|---|---|
| 미측정 | 연결된 물리 기기 없음 | 미측정 | 미측정 | 미측정 |

2026-07-17에 API 35 Gradle Managed Device에서 `dryRunMode`로 5개 scenario가 모두 통과했고 scenario별 Perfetto trace가 생성됐다. 이 결과는 설치·launch·artifact 경로 검증이며 성능 수치가 아니다.

## 참고

- [Android Macrobenchmark overview](https://developer.android.com/topic/performance/benchmarking/macrobenchmark-overview)
- [Android Benchmark library releases](https://developer.android.com/jetpack/androidx/releases/benchmark)
