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
composeDateTimePicker = "0.6.0"

[libraries]
compose-date-time-picker = { module = "io.github.kez-lab:compose-date-time-picker", version.ref = "composeDateTimePicker" }
```

### Gradle (build.gradle.kts)

```kotlin
dependencies {
    implementation("io.github.kez-lab:compose-date-time-picker:0.6.0")
}
```

## 사용법

### TimePicker

시간 선택을 위해 `TimePicker`를 사용합니다. 12시간 및 24시간 형식을 지원합니다.

#### 1. 24시간 형식

```kotlin
import androidx.compose.runtime.Composable
import com.kez.picker.time.TimePicker
import com.kez.picker.rememberTimePickerState
import com.kez.picker.util.TimeFormat
import com.kez.picker.util.currentHour
import com.kez.picker.util.currentMinute

@Composable
fun TimePicker24hExample() {
    val state = rememberTimePickerState(
        initialHour = currentHour(),
        initialMinute = currentMinute(),
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
import com.kez.picker.time.TimePicker
import com.kez.picker.rememberTimePickerState
import com.kez.picker.util.TimeFormat
import com.kez.picker.util.currentHour
import com.kez.picker.util.currentMinute

@Composable
fun TimePicker12hExample() {
    // 12시간 형식 변환은 이제 state 내부에서 처리됩니다.
    val state = rememberTimePickerState(
        initialHour = currentHour(),
        initialMinute = currentMinute(),
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
import com.kez.picker.date.DatePicker
import com.kez.picker.date.rememberDatePickerState
import com.kez.picker.util.currentDate
import com.kez.picker.util.currentMonth

@Composable
fun DatePickerExample() {
    val state = rememberDatePickerState(
        initialYear = currentDate().year,
        initialMonth = currentMonth(),
        initialDay = currentDate().day
    )

    DatePicker(state = state)

    // 앱 로직에 전달할 때는 state.selectedDate를 사용합니다.
}
```

### YearMonthPicker

특정 연도와 월을 선택할 때 `YearMonthPicker`를 사용합니다.

```kotlin
import androidx.compose.runtime.Composable
import com.kez.picker.date.YearMonthPicker
import com.kez.picker.rememberYearMonthPickerState
import com.kez.picker.util.currentDate
import com.kez.picker.util.currentMonth

@Composable
fun YearMonthPickerExample() {
    val state = rememberYearMonthPickerState(
        initialYear = currentDate().year,
        initialMonth = currentMonth()
    )

    YearMonthPicker(
        state = state
    )

    // state.selectedMonthDate는 선택된 월의 1일을 나타냅니다.
}
```

### BottomSheet 통합

Picker 컴포넌트는 `ModalBottomSheet`나 다른 다이얼로그 컴포넌트 내에서도 원활하게 작동합니다.

```kotlin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.kez.picker.time.TimePicker
import com.kez.picker.rememberTimePickerState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetPickerExample() {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val state = rememberTimePickerState()

    Button(onClick = { showBottomSheet = true }) {
        Text("시간 선택")
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            TimePicker(state = state)
            // 확인 버튼 로직 등 추가 가능
        }
    }
}
```

## API 레퍼런스

### TimePicker

| 파라미터 | 설명 | 기본값 |
| :--- | :--- | :--- |
| `state` | Picker를 제어하기 위한 상태 객체입니다. | `rememberTimePickerState()` |
| `startTime` | 레거시 초기 시간 파라미터입니다. 초기값은 `rememberTimePickerState`에서 설정하는 방식을 권장합니다. | `currentDateTime()` |
| `minuteItems` | 선택 가능한 분 목록입니다. | `0..59` |
| `hourItems` | 선택 가능한 시간 목록입니다. | `0..23` 또는 `1..12` |
| `visibleItemsCount` | 리스트에 표시될 아이템의 개수입니다. | `3` |
| `colors` | 텍스트, 선택 텍스트, 구분선, 선택 영역 배경 색상입니다. | `PickerDefaults.colors()` |
| `textStyles` | 선택/비선택 아이템의 텍스트 스타일입니다. | `PickerDefaults.textStyles()` |
| `isDividerVisible` | 선택 영역 구분선 표시 여부입니다. | `true` |

**TimePickerState 속성:**

- `selectedHour`: Picker에 표시되는 선택된 시간입니다.
- `selectedMinute`: 현재 선택된 분입니다. (0-59)
- `selectedPeriod`: 12시간 형식에서 선택된 오전/오후 값입니다.
- `selectedHourOfDay`: 선택된 시간을 24시간 기준(0-23)으로 변환한 값입니다.
- `selectedTime`: 선택된 값을 `kotlinx.datetime.LocalTime`으로 제공합니다.

`rememberTimePickerState`는 saveable state를 사용합니다. Android에서는 플랫폼 saveable registry가 제공될 때 Activity 재생성 이후에도 선택값을 복원할 수 있습니다.

### DatePicker

| 파라미터 | 설명 | 기본값 |
| :--- | :--- | :--- |
| `state` | Picker를 제어하기 위한 상태 객체입니다. | `rememberDatePickerState()` |
| `startLocalDate` | 레거시 초기 날짜 파라미터입니다. 초기값은 `rememberDatePickerState`에서 설정하는 방식을 권장합니다. | `currentDate()` |
| `yearItems` | 선택 가능한 연도 목록입니다. | `1000..9999` |
| `monthItems` | 선택 가능한 월 목록입니다. | `1..12` |
| `visibleItemsCount` | 리스트에 표시될 아이템의 개수입니다. | `3` |
| `colors` | 텍스트, 선택 텍스트, 구분선, 선택 영역 배경 색상입니다. | `PickerDefaults.colors()` |
| `textStyles` | 선택/비선택 아이템의 텍스트 스타일입니다. | `PickerDefaults.textStyles()` |

**DatePickerState 속성:**

- `selectedYear`: 현재 선택된 연도입니다.
- `selectedMonth`: 현재 선택된 월입니다. (1-12)
- `selectedDay`: 현재 선택된 일입니다. 선택된 월에 맞게 자동 보정됩니다.
- `selectedDate`: 선택된 값을 `kotlinx.datetime.LocalDate`로 제공합니다.
- `maxDay`: 현재 선택된 연도/월에서 선택 가능한 최대 일입니다.

`rememberDatePickerState`는 saveable state를 사용합니다. Android에서는 플랫폼 saveable registry가 제공될 때 Activity 재생성 이후에도 선택값을 복원할 수 있습니다.

### YearMonthPicker

| 파라미터 | 설명 | 기본값 |
| :--- | :--- | :--- |
| `state` | Picker를 제어하기 위한 상태 객체입니다. | `rememberYearMonthPickerState()` |
| `startLocalDate` | 레거시 초기 날짜 파라미터입니다. 초기값은 `rememberYearMonthPickerState`에서 설정하는 방식을 권장합니다. | `currentDate()` |
| `yearItems` | 선택 가능한 연도 목록입니다. | `1000..9999` |
| `monthItems` | 선택 가능한 월 목록입니다. | `1..12` |
| `visibleItemsCount` | 리스트에 표시될 아이템의 개수입니다. | `3` |
| `colors` | 텍스트, 선택 텍스트, 구분선, 선택 영역 배경 색상입니다. | `PickerDefaults.colors()` |
| `textStyles` | 선택/비선택 아이템의 텍스트 스타일입니다. | `PickerDefaults.textStyles()` |

**YearMonthPickerState 속성:**

- `selectedYear`: 현재 선택된 연도입니다.
- `selectedMonth`: 현재 선택된 월입니다. (1-12)
- `selectedMonthDate`: 선택된 연/월을 해당 월의 1일 `LocalDate`로 제공합니다.

`rememberYearMonthPickerState`는 saveable state를 사용합니다. Android에서는 플랫폼 saveable registry가 제공될 때 Activity 재생성 이후에도 선택값을 복원할 수 있습니다.

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
