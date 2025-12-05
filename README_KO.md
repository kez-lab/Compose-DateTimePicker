# Compose DateTimePicker

[![Read in English](https://img.shields.io/badge/README-English-blue)](./README.md)

Compose Multiplatform을 위한 범용적이고 커스터마이징 가능한 날짜 및 시간 선택 라이브러리입니다.
Android, iOS, Desktop (JVM), Web (Wasm) 등 다양한 플랫폼에서 일관된 UI 컴포넌트를 제공합니다.

## 주요 기능

*   **멀티플랫폼 지원**: Android, iOS, Desktop (JVM), Web (Wasm) 환경을 지원하며 원활한 통합이 가능합니다.
*   **TimePicker**: 12시간(오전/오후) 및 24시간 형식을 모두 지원합니다.
*   **YearMonthPicker**: 년도와 월을 선택할 수 있는 전용 컴포넌트를 제공합니다.
*   **커스터마이징**: 커스텀 아이템 렌더링, 스타일링, 구성 변경이 가능한 유연한 API를 제공합니다.
*   **상태 관리**: `rememberTimePickerState` 및 `rememberYearMonthPickerState`를 통해 간편하게 상태를 관리할 수 있습니다.
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
        initialHour = currentHour,
        initialMinute = currentMinute
    )

    TimePicker(
        state = state,
        timeFormat = TimeFormat.HOUR_24
    )
}
```

#### 2. 12시간 형식 (오전/오후)

```kotlin
import androidx.compose.runtime.Composable
import com.kez.picker.time.TimePicker
import com.kez.picker.rememberTimePickerState
import com.kez.picker.util.TimeFormat
import com.kez.picker.util.TimePeriod
import com.kez.picker.util.currentHour
import com.kez.picker.util.currentMinute

@Composable
fun TimePicker12hExample() {
    val currentPeriod = if (currentHour >= 12) TimePeriod.PM else TimePeriod.AM
    val currentHour12 = if (currentHour > 12) currentHour - 12 else if (currentHour == 0) 12 else currentHour

    val state = rememberTimePickerState(
        initialHour = currentHour12,
        initialMinute = currentMinute,
        initialPeriod = currentPeriod
    )

    TimePicker(
        state = state,
        timeFormat = TimeFormat.HOUR_12
    )
}
```

### YearMonthPicker

특정 연도와 월을 선택할 때 `YearMonthPicker`를 사용합니다.

```kotlin
import androidx.compose.runtime.Composable
import com.kez.picker.date.YearMonthPicker
import com.kez.picker.rememberYearMonthPickerState
import com.kez.picker.util.currentDate

@Composable
fun YearMonthPickerExample() {
    val state = rememberYearMonthPickerState(
        initialYear = currentDate.year,
        initialMonth = currentDate.monthNumber
    )

    YearMonthPicker(
        state = state
    )
}
```

### BottomSheet 통합

Picker 컴포넌트는 `ModalBottomSheet`나 다른 다이얼로그 컴포넌트 내에서도 원활하게 작동합니다.

```kotlin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.kez.picker.time.TimePicker
import com.kez.picker.rememberTimePickerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetPickerExample() {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val state = rememberTimePickerState()
    val scope = rememberCoroutineScope()

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
| `timeFormat` | 표시할 시간 형식입니다 (`HOUR_12` 또는 `HOUR_24`). | `HOUR_24` |
| `startTime` | Picker에 설정될 초기 시간입니다. | `currentDateTime` |
| `visibleItemsCount` | 리스트에 표시될 아이템의 개수입니다. | `3` |
| `textStyle` | 선택되지 않은 아이템의 텍스트 스타일입니다. | `16.sp` |
| `selectedTextStyle` | 선택된 아이템의 텍스트 스타일입니다. | `22.sp` |
| `dividerColor` | 구분선의 색상입니다. | `LocalContentColor.current` |

### YearMonthPicker

| 파라미터 | 설명 | 기본값 |
| :--- | :--- | :--- |
| `state` | Picker를 제어하기 위한 상태 객체입니다. | `rememberYearMonthPickerState()` |
| `startLocalDate` | Picker에 설정될 초기 날짜입니다. | `currentDate` |
| `yearItems` | 선택 가능한 연도 목록입니다. | `1900..2100` |
| `monthItems` | 선택 가능한 월 목록입니다. | `1..12` |
| `visibleItemsCount` | 리스트에 표시될 아이템의 개수입니다. | `3` |

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
