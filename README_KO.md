# Compose DateTimePicker

[![Read in English](https://img.shields.io/badge/README-English-blue)](./README.md)

Compose Multiplatform을 위한 범용적이고 커스터마이징 가능한 날짜 및 시간 선택 라이브러리입니다.
Android, iOS, Desktop (JVM), Web (Wasm) 등 다양한 플랫폼에서 일관된 UI 컴포넌트를 제공합니다.

## 주요 기능

*   **멀티플랫폼 지원**: Android, iOS, Desktop (JVM), Web (Wasm) 환경을 지원하며 원활한 통합이 가능합니다.
*   **TimePicker**: 12시간(오전/오후) 및 24시간 형식을 모두 지원합니다.
*   **DatePicker**: 연도, 월, 일을 함께 선택하고 월/윤년에 맞춰 일을 자동 보정합니다.
*   **YearMonthPicker**: 년도와 월을 선택할 수 있는 전용 컴포넌트를 제공합니다.
*   **커스터마이징**: 커스텀 아이템 렌더링, 스타일링, 구성 변경이 가능한 유연한 API를 제공합니다.
*   **상태 관리**: `rememberTimePickerState`, `rememberDatePickerState`, `rememberYearMonthPickerState`를 통해 간편하게 상태를 관리할 수 있습니다.
*   **접근성**: 스크린 리더 및 내비게이션 지원 등 접근성을 고려하여 설계되었습니다.

## 설치 방법

버전 카탈로그 또는 빌드 파일에 의존성을 추가하여 사용할 수 있습니다.

### 버전 카탈로그 (libs.versions.toml)

```toml
[versions]
composeDateTimePicker = "0.4.0"

[libraries]
compose-date-time-picker = { module = "io.github.kez-lab:compose-date-time-picker", version.ref = "composeDateTimePicker" }
```

### Gradle (build.gradle.kts)

```kotlin
dependencies {
    implementation("io.github.kez-lab:compose-date-time-picker:0.4.0")
}
```

> **릴리스 상태:** `0.4.0`이 현재 Maven Central/GitHub Releases에 공개된 최신 버전입니다. 이 README는 `main` 기준으로 유지되며 아직 릴리스되지 않은 `0.6.0` API 작업도 문서화하므로, 아래 사용법과 API 레퍼런스에는 `0.4.0`에 없는 API가 포함될 수 있습니다. 공개 버전 기준 API는 `0.4.0` release/tag 문서를 확인하세요. `main`을 로컬에서 테스트하려면 `./gradlew :datetimepicker:publishToMavenLocal`을 실행하고, 소비 프로젝트에 `mavenLocal()`을 추가한 뒤 `0.6.0`에 의존하세요.

릴리스 노트와 업그레이드 영향은 영문 [CHANGELOG.md](CHANGELOG.md)를 참고하세요.

## 사용법

> 아래 예제는 현재 `main` 브랜치 API를 기준으로 합니다. 위 설치 스니펫의 공개 `0.4.0` 의존성이 아니라, 아직 릴리스되지 않은 `0.6.0` API가 필요할 수 있습니다.

### TimePicker

시간 선택을 위해 `TimePicker`를 사용합니다. 12시간 및 24시간 형식을 지원합니다.

#### 1. 24시간 형식

```kotlin
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.kez.picker.time.TimePicker
import com.kez.picker.rememberTimePickerState
import com.kez.picker.util.TimeFormat
import com.kez.picker.util.currentDateTime

@Composable
fun TimePicker24hExample() {
    val initialTime = remember { currentDateTime().time }
    val state = rememberTimePickerState(
        initialTime = initialTime,
        timeFormat = TimeFormat.HOUR_24
    )

    TimePicker(
        state = state
    )

    // 앱 로직에 전달할 때는 state.selectedTime을 사용합니다.
}
```

#### 2. 12시간 형식 (오전/오후)

```kotlin
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.kez.picker.time.TimePicker
import com.kez.picker.rememberTimePickerState
import com.kez.picker.util.TimeFormat
import com.kez.picker.util.currentDateTime

@Composable
fun TimePicker12hExample() {
    // 12시간 형식 변환은 이제 state 내부에서 처리됩니다.
    val initialTime = remember { currentDateTime().time }
    val state = rememberTimePickerState(
        initialTime = initialTime,
        timeFormat = TimeFormat.HOUR_12
    )

    TimePicker(
        state = state
    )

    // state.selectedTime은 항상 kotlinx.datetime.LocalTime입니다.
}
```

### DatePicker

연도, 월, 일을 함께 선택할 때 `DatePicker`를 사용합니다. 선택된 월에 유효하지 않은 일이 있으면 자동으로 보정됩니다.

```kotlin
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.kez.picker.date.DatePicker
import com.kez.picker.date.rememberDatePickerState
import com.kez.picker.util.currentDate

@Composable
fun DatePickerExample() {
    val initialDate = remember { currentDate() }
    val selectableYears = remember(initialDate.year) {
        ((initialDate.year - 1)..(initialDate.year + 1)).toList()
    }
    val state = rememberDatePickerState(
        initialDate = initialDate
    )

    DatePicker(
        state = state,
        yearItems = selectableYears
    )

    // 앱 로직에 전달할 때는 state.selectedDate를 사용합니다.
}
```

`yearItems`나 `monthItems`를 제한할 때는 기억된 초기값 또는 복원된 state 값이 해당 목록 안에
들어가도록 함께 설계하세요. composition 이후 외부 날짜가 바뀐다면 새 `initialDate` 인자에
의존하지 말고 `state.selectDate(newDate)`를 호출하세요.

### YearMonthPicker

특정 연도와 월을 선택할 때 `YearMonthPicker`를 사용합니다.

```kotlin
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.kez.picker.date.YearMonthPicker
import com.kez.picker.rememberYearMonthPickerState
import com.kez.picker.util.currentDate

@Composable
fun YearMonthPickerExample() {
    val initialDate = remember { currentDate() }
    val state = rememberYearMonthPickerState(
        initialDate = initialDate
    )

    YearMonthPicker(
        state = state
    )

    // state.selectedMonthDate는 선택된 월의 1일을 나타냅니다.
}
```

### BottomSheet 통합

Picker 컴포넌트는 `ModalBottomSheet`나 다른 다이얼로그 컴포넌트 내에서도 원활하게 작동합니다.
확정된 값과 sheet 내부의 임시 picker state를 분리하면, sheet를 닫거나 취소했을 때 앱 상태가
의도치 않게 바뀌지 않습니다.

```kotlin
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kez.picker.rememberTimePickerState
import com.kez.picker.time.TimePicker
import kotlinx.datetime.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetPickerExample() {
    var committedHour by rememberSaveable { mutableIntStateOf(9) }
    var committedMinute by rememberSaveable { mutableIntStateOf(30) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val committedTime = LocalTime(committedHour, committedMinute)

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("선택된 시간: $committedTime")

        Button(onClick = { showBottomSheet = true }) {
            Text("시간 선택")
        }
    }

    if (showBottomSheet) {
        val draftState = rememberTimePickerState(initialTime = committedTime)

        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TimePicker(state = draftState)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { showBottomSheet = false },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("취소")
                    }

                    Button(
                        onClick = {
                            val selected = draftState.selectedTime
                            committedHour = selected.hour
                            committedMinute = selected.minute
                            showBottomSheet = false
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("적용")
                    }
                }
            }
        }
    }
}
```

위 예제는 Android Activity 재생성에도 보존하기 쉽도록 primitive 값인 hour/minute를
`rememberSaveable`로 저장하고, draft picker state를 만들기 전에 `LocalTime`을 다시 생성합니다.

## API 레퍼런스

> 이 레퍼런스는 현재 `main` 브랜치 API를 설명합니다. 공개 `0.4.0` artifact에 의존하는 프로젝트에 예제를 복사하기 전에는 [CHANGELOG.md](CHANGELOG.md)와 `0.4.0` release/tag 문서를 확인하세요.

접근성 label 파라미터는 semantics에 들어가는 picker column prefix를 바꿉니다. `*ItemContentDescription`
파라미터는 화면에 보이는 텍스트를 바꾸지 않고 접근성 값 설명만 바꿉니다. 선택 상태는 고정된 영어 문구를
붙이지 않고 Compose `selected` semantics로 전달됩니다. Picker는 이전/다음 item을 선택하는 custom
accessibility action도 제공합니다. `previousItemActionLabel`과 `nextItemActionLabel`로 action label을
현지화할 수 있고, `null`이나 blank를 전달하면 해당 action을 생략합니다.

### Generic Picker

단일 custom picker column이 필요하면 `Picker<T>`를 사용하세요.

```kotlin
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberSaveable
import androidx.compose.runtime.setValue
import com.kez.picker.Picker

@Composable
fun SizePickerExample() {
    val items = listOf("Small", "Medium", "Large")
    var selectedSize by rememberSaveable { mutableStateOf("Medium") }

    Picker(
        items = items,
        selectedItem = selectedSize,
        onSelectedItemChange = { selectedSize = it },
        isInfinity = false,
        pickerLabel = "Size",
        itemContentDescription = { it }
    )
}
```

`Picker<T>`는 controlled component입니다. 선택값은 앱 state에 보관하고, 그 값을 `selectedItem`으로
전달하며, `onSelectedItemChange`에서 앱 state를 갱신하세요. `items`는 비어 있으면 안 되고 중복값이
없어야 하며, `selectedItem`은 반드시 `items` 안에 있어야 합니다. `T`가 saveable하지 않다면 앱 state에는
saveable한 key를 저장한 뒤 렌더링 전에 그 key를 item으로 매핑하세요.

### 프로그래밍 방식 선택

`remember*State`로 picker state를 만들고 picker에 전달한 뒤, 이벤트 핸들러나
`LaunchedEffect(externalValue)`에서 공개 선택 메서드를 호출하세요. 선택값을 다시 맞추기 위해
state를 새로 만들 필요는 없습니다.

| State | Method |
| :--- | :--- |
| Generic `Picker<T>` | 앱이 소유한 `selectedItem` 값을 갱신 |
| `TimePickerState` | `selectTime(LocalTime(...))` |
| `DatePickerState` | `selectDate(LocalDate(...))` |
| `YearMonthPickerState` | `selectYearMonth(year, month)` 또는 `selectDate(LocalDate(...))` |

```kotlin
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.kez.picker.rememberTimePickerState
import com.kez.picker.time.TimePicker
import kotlinx.datetime.LocalTime

@Composable
fun ProgrammaticTimePickerExample() {
    val state = rememberTimePickerState(initialTime = LocalTime(8, 0))

    Column {
        Button(onClick = { state.selectTime(LocalTime(9, 30)) }) {
            Text("Set 09:30")
        }

        TimePicker(state = state)
    }
}
```

요청한 값이 현재 item list에 포함되어 있으면 picker 스크롤 위치가 동기화됩니다. custom list는 엄격하게
검증됩니다. 비어 있지 않아야 하고, 중복이 없어야 하며, 지원 범위 안의 값만 포함해야 하고, 현재 선택값도
반드시 포함해야 합니다. 앱이 custom list 밖의 값을 복원하거나 요청할 수 있다면 picker를 렌더링하기 전에
앱 state를 보정하거나 거부하세요.

### TimePicker

| 파라미터 | 설명 | 기본값 |
| :--- | :--- | :--- |
| `state` | Picker를 제어하기 위한 상태 객체입니다. | `rememberTimePickerState()` |
| `minuteItems` | 선택 가능한 분 목록입니다. 값은 `0..59` 범위여야 합니다. | `0..59` |
| `hourItems` | 선택 가능한 시간 목록입니다. 24시간 형식에서는 `0..23`, 12시간 형식에서는 표시 시간 기준 `1..12` 범위여야 합니다. | `0..23` 또는 `1..12` |
| `periodItems` | 12시간 형식에서 선택 가능한 오전/오후 목록입니다. `timeFormat`이 `HOUR_12`일 때 비어 있으면 안 됩니다. | `TimePeriod.entries` |
| `visibleItemsCount` | 리스트에 표시될 아이템의 개수입니다. | `3` |
| `colors` | 텍스트, 선택 텍스트, 구분선, 선택 영역 배경 색상입니다. | `PickerDefaults.colors()` |
| `textStyles` | 선택/비선택 아이템의 텍스트 스타일입니다. | `PickerDefaults.textStyles()` |
| `isDividerVisible` | 선택 영역 구분선 표시 여부입니다. | `true` |
| `hourPickerLabel` | 시간 picker의 접근성 label입니다. `null`을 전달하면 picker label prefix를 생략합니다. | `"Hour"` |
| `minutePickerLabel` | 분 picker의 접근성 label입니다. `null`을 전달하면 picker label prefix를 생략합니다. | `"Minute"` |
| `periodPickerLabel` | 12시간 형식에서 오전/오후 picker의 접근성 label입니다. `null`을 전달하면 picker label prefix를 생략합니다. | `"AM/PM"` |
| `hourItemContentDescription` | 각 시간 값의 접근성 설명입니다. | `it.toString()` |
| `minuteItemContentDescription` | 각 분 값의 접근성 설명입니다. | `it.toString()` |
| `periodItemContentDescription` | 12시간 형식에서 각 오전/오후 값의 접근성 설명입니다. | `it.name` |
| `previousItemActionLabel` | child picker가 이전 item을 선택할 때 쓰는 접근성 action label입니다. `null` 또는 blank를 전달하면 생략합니다. | `"Select previous item"` |
| `nextItemActionLabel` | child picker가 다음 item을 선택할 때 쓰는 접근성 action label입니다. `null` 또는 blank를 전달하면 생략합니다. | `"Select next item"` |

**TimePickerState 속성:**

- `selectedHour`: Picker에 표시되는 선택된 시간입니다.
- `selectedMinute`: 현재 선택된 분입니다. (0-59)
- `selectedPeriod`: 12시간 형식에서 선택된 오전/오후 값입니다.
- `selectedHourOfDay`: 선택된 시간을 24시간 기준(0-23)으로 변환한 값입니다.
- `selectedTime`: 선택된 값을 `kotlinx.datetime.LocalTime`으로 제공합니다.

`rememberTimePickerState`는 saveable state를 사용합니다. Android에서는 플랫폼 saveable registry가 제공될 때 Activity 재생성 이후에도 선택값을 복원할 수 있습니다.

초기값은 `rememberTimePickerState(initialTime = LocalTime(...))` 또는 `initialHour`/`initialMinute` 파라미터로 설정합니다.

상태 생성 이후 선택값을 바꾸려면 `state.selectTime(LocalTime(...))`을 호출합니다.

custom item 값이 유효 범위를 벗어나거나, 중복이 있거나, 필수 목록이 비어 있거나, 현재 선택값이 custom 목록에 없으면 composition 중 `IllegalArgumentException`이 발생합니다. 12시간 형식의 `hourItems`는 표시 시간 기준(`1..12`)입니다. 예를 들어 `initialHour = 13`은 `state.selectedHour == 1`, `PM`으로 변환됩니다.

### DatePicker

| 파라미터 | 설명 | 기본값 |
| :--- | :--- | :--- |
| `state` | Picker를 제어하기 위한 상태 객체입니다. | `rememberDatePickerState()` |
| `yearItems` | 선택 가능한 연도 목록입니다. 값은 `1000..9999` 범위여야 합니다. | `1000..9999` |
| `monthItems` | 선택 가능한 월 목록입니다. 값은 `1..12` 범위여야 합니다. | `1..12` |
| `visibleItemsCount` | 리스트에 표시될 아이템의 개수입니다. | `3` |
| `colors` | 텍스트, 선택 텍스트, 구분선, 선택 영역 배경 색상입니다. | `PickerDefaults.colors()` |
| `textStyles` | 선택/비선택 아이템의 텍스트 스타일입니다. | `PickerDefaults.textStyles()` |
| `yearPickerLabel` | 연도 picker의 접근성 label입니다. `null`을 전달하면 picker label prefix를 생략합니다. | `"Year"` |
| `monthPickerLabel` | 월 picker의 접근성 label입니다. `null`을 전달하면 picker label prefix를 생략합니다. | `"Month"` |
| `dayPickerLabel` | 일 picker의 접근성 label입니다. `null`을 전달하면 picker label prefix를 생략합니다. | `"Day"` |
| `yearItemContentDescription` | 각 연도 값의 접근성 설명입니다. | `it.toString()` |
| `monthItemContentDescription` | 각 월 값의 접근성 설명입니다. | `it.toString()` |
| `dayItemContentDescription` | 각 일 값의 접근성 설명입니다. | `it.toString()` |
| `previousItemActionLabel` | child picker가 이전 item을 선택할 때 쓰는 접근성 action label입니다. `null` 또는 blank를 전달하면 생략합니다. | `"Select previous item"` |
| `nextItemActionLabel` | child picker가 다음 item을 선택할 때 쓰는 접근성 action label입니다. `null` 또는 blank를 전달하면 생략합니다. | `"Select next item"` |

**DatePickerState 속성:**

- `selectedYear`: 현재 선택된 연도입니다.
- `selectedMonth`: 현재 선택된 월입니다. (1-12)
- `selectedDay`: 현재 선택된 일입니다. 선택된 월에 맞게 자동 보정됩니다.
- `selectedDate`: 선택된 값을 `kotlinx.datetime.LocalDate`로 제공합니다.
- `maxDay`: 현재 선택된 연도/월에서 선택 가능한 최대 일입니다.

`rememberDatePickerState`는 saveable state를 사용합니다. Android에서는 플랫폼 saveable registry가 제공될 때 Activity 재생성 이후에도 선택값을 복원할 수 있습니다.

초기값은 `rememberDatePickerState(initialDate = LocalDate(...))` 또는
`initialYear`/`initialMonth`/`initialDay` 파라미터로 설정합니다. 초기 연도는 `1000..9999`,
월은 `1..12` 범위여야 하고 일은 최소 `1`이어야 합니다. `initialDay`가 초기 연/월의 최대 일수보다
크면 그 최대 일수로 보정됩니다.

상태 생성 이후 선택값을 바꾸려면 `state.selectDate(LocalDate(...))`를 호출합니다.

custom item 값이 유효 범위를 벗어나거나, 중복이 있거나, 목록이 비어 있거나, 현재 선택된 연도/월이 custom 목록에 없으면 composition 중 `IllegalArgumentException`이 발생합니다.

### YearMonthPicker

| 파라미터 | 설명 | 기본값 |
| :--- | :--- | :--- |
| `state` | Picker를 제어하기 위한 상태 객체입니다. | `rememberYearMonthPickerState()` |
| `yearItems` | 선택 가능한 연도 목록입니다. 값은 `1000..9999` 범위여야 합니다. | `1000..9999` |
| `monthItems` | 선택 가능한 월 목록입니다. 값은 `1..12` 범위여야 합니다. | `1..12` |
| `visibleItemsCount` | 리스트에 표시될 아이템의 개수입니다. | `3` |
| `colors` | 텍스트, 선택 텍스트, 구분선, 선택 영역 배경 색상입니다. | `PickerDefaults.colors()` |
| `textStyles` | 선택/비선택 아이템의 텍스트 스타일입니다. | `PickerDefaults.textStyles()` |
| `yearPickerLabel` | 연도 picker의 접근성 label입니다. `null`을 전달하면 picker label prefix를 생략합니다. | `"Year"` |
| `monthPickerLabel` | 월 picker의 접근성 label입니다. `null`을 전달하면 picker label prefix를 생략합니다. | `"Month"` |
| `yearItemContentDescription` | 각 연도 값의 접근성 설명입니다. | `it.toString()` |
| `monthItemContentDescription` | 각 월 값의 접근성 설명입니다. | `it.toString()` |
| `previousItemActionLabel` | child picker가 이전 item을 선택할 때 쓰는 접근성 action label입니다. `null` 또는 blank를 전달하면 생략합니다. | `"Select previous item"` |
| `nextItemActionLabel` | child picker가 다음 item을 선택할 때 쓰는 접근성 action label입니다. `null` 또는 blank를 전달하면 생략합니다. | `"Select next item"` |

**YearMonthPickerState 속성:**

- `selectedYear`: 현재 선택된 연도입니다.
- `selectedMonth`: 현재 선택된 월입니다. (1-12)
- `selectedMonthDate`: 선택된 연/월을 해당 월의 1일 `LocalDate`로 제공합니다.

`rememberYearMonthPickerState`는 saveable state를 사용합니다. Android에서는 플랫폼 saveable registry가 제공될 때 Activity 재생성 이후에도 선택값을 복원할 수 있습니다.

초기값은 `rememberYearMonthPickerState(initialDate = LocalDate(...))` 또는 `initialYear`/`initialMonth` 파라미터로 설정합니다. 초기 연도는 `1000..9999` 범위여야 합니다.

상태 생성 이후 선택값을 바꾸려면 `state.selectYearMonth(year, month)` 또는
`state.selectDate(LocalDate(...))`를 호출합니다.

custom item 값이 유효 범위를 벗어나거나, 중복이 있거나, 목록이 비어 있거나, 현재 선택된 연도/월이 custom 목록에 없으면 composition 중 `IllegalArgumentException`이 발생합니다.

## 라이선스

```
Copyright 2024 KEZ Lab

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
