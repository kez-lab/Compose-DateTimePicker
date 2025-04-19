## Picker 제작 과정기
[[Android/Compose] Picker, NumberPicker, DatePicker 제작 과정기 1부](https://velog.io/@kej_ad/AndroidCompose-Year-Month-DatePicker-%EB%A7%8C%EB%93%A4%EA%B8%B0)

# Compose DateTimePicker

Compose Multiplatform용 날짜 및 시간 선택기 라이브러리입니다. Android, iOS, Desktop(JVM) 및 Web을 지원합니다.

## 개요

이 라이브러리는 Compose Multiplatform을 사용하여 개발된 날짜 및 시간 선택 UI 컴포넌트를 제공합니다. 다양한 플랫폼에서 일관된 사용자 경험을 제공하면서도 각 플랫폼의 특성을 고려한 설계가 적용되었습니다.

### 주요 기능

- **TimePicker**: 12시간제 및 24시간제를 지원하는 시간 선택기
- **YearMonthPicker**: 연도와 월을 선택할 수 있는 날짜 선택기
- **다양한 커스터마이징 옵션**: 글꼴, 색상, 크기 등을 사용자 지정 가능
- **반응형 디자인**: 다양한 화면 크기에 대응
- **표준 Compose 컴포넌트 호환**: 기존 Compose UI에 자연스럽게 통합

## 설치 방법

### Gradle

build.gradle.kts (모듈 수준) 파일에 다음 의존성을 추가합니다:

```kotlin
dependencies {
    implementation("io.github.kez-lab:compose-date-time-picker:0.2.0")
}
```

## 사용 방법

### TimePicker

```kotlin
// 24시간제 시간 선택기
TimePicker(
    hourPickerState = rememberPickerState(currentHour),
    minutePickerState = rememberPickerState(currentMinute),
    timeFormat = TimeFormat.HOUR_24
)

// 12시간제 시간 선택기
TimePicker(
    hourPickerState = rememberPickerState(
        if (currentHour > 12) currentHour - 12 else if (currentHour == 0) 12 else currentHour
    ),
    minutePickerState = rememberPickerState(currentMinute),
    periodPickerState = rememberPickerState(if (currentHour >= 12) TimePeriod.PM else TimePeriod.AM),
    timeFormat = TimeFormat.HOUR_12
)
```

### YearMonthPicker

```kotlin
YearMonthPicker(
    yearPickerState = rememberPickerState(currentDate.year),
    monthPickerState = rememberPickerState(currentDate.monthNumber)
)
```

### 상태 관리

```kotlin
// PickerState를 사용하여 상태 관리
val hourState = rememberPickerState(currentHour)
val minuteState = rememberPickerState(currentMinute)

// 선택된 값 접근
val selectedHour = hourState.selectedItem
val selectedMinute = minuteState.selectedItem
```

## 커스터마이징

```kotlin
TimePicker(
    hourPickerState = rememberPickerState(currentHour),
    minutePickerState = rememberPickerState(currentMinute),
    timeFormat = TimeFormat.HOUR_24,
    textStyle = TextStyle(fontSize = 14.sp, color = Color.Gray),
    selectedTextStyle = TextStyle(fontSize = 18.sp, color = Color.Black, fontWeight = FontWeight.Bold),
    dividerColor = Color.Blue,
    visibleItemsCount = 5,
    pickerWidth = 80.dp
)
```

## 프로젝트 구조

```
Compose-DateTimePicker/
├── datetimepicker/                # 라이브러리 모듈
│   └── src/
│       ├── commonMain/            # 공통 코드
│       │   └── kotlin/com/kez/picker/
│       │       ├── date/          # 날짜 선택기
│       │       ├── time/          # 시간 선택기
│       │       └── util/          # 유틸리티 클래스
│       ├── androidMain/           # Android 구현
│       ├── iosMain/               # iOS 구현
│       ├── desktopMain/           # Desktop(JVM) 구현
│       └── jsMain/                # Web 구현
└── sample/                        # 샘플 앱
    └── src/
        ├── commonMain/            # 공통 샘플 코드
        ├── androidMain/           # Android 샘플 진입점
        ├── iosMain/               # iOS 샘플 진입점
        ├── jvmMain/               # Desktop 샘플 진입점
        └── jsMain/                # Web 샘플 진입점
```

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


