# Compose DateTimePicker

[Android/Compose] Picker 제작 과정기 1부](https://velog.io/@kej_ad/AndroidCompose-Year-Month-DatePicker-%EB%A7%8C%EB%93%A4%EA%B8%B0)

Compose DateTimePicker는 Compose Multiplatform 기반의 날짜 및 시간 선택기 라이브러리입니다. 하나의 코드 베이스로 Android, iOS, Desktop(JVM), Web에서 동일한 UI를 제공하며, 다양한 커스터마이징 옵션을 통해 앱의 스타일에 맞게 쉽게 적용할 수 있습니다.

## 개요

이 라이브러리는 날짜와 시간 선택을 위한 공통 UI 컴포넌트를 제공합니다. 모든 플랫폼에서 동일한 사용성을 목표로 하며, 간결한 API와 폭넓은 커스터마이징 기능을 제공합니다.

### 주요 특징

- **TimePicker**: 12시간제와 24시간제를 모두 지원합니다.
- **YearMonthPicker**: 연도와 월을 간편하게 선택할 수 있습니다.
- **다양한 사용자 정의**: 글꼴, 색상, 표시 항목 수 등을 손쉽게 변경할 수 있습니다.
- **반응형 레이아웃**: 화면 크기에 맞춰 자연스럽게 조정됩니다.
- **Compose 호환성**: 기존 Compose UI에 매끄럽게 통합됩니다.

## 설치

Gradle을 사용한다면 다음과 같이 의존성을 추가합니다. 라이브러리는 Maven Central에 배포되어 있습니다.

```kotlin
dependencies {
    implementation("io.github.kez-lab:compose-date-time-picker:0.2.0")
}
```

## 기본 사용 방법

### TimePicker

```kotlin
// 24시간제 시간 선택기 예시
TimePicker(
    hourPickerState = rememberPickerState(currentHour),
    minutePickerState = rememberPickerState(currentMinute),
    timeFormat = TimeFormat.HOUR_24
)

// 12시간제 시간 선택기 예시
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

### 선택 값 가져오기

`PickerState`는 현재 선택된 값을 `selectedItem` 속성으로 제공합니다. 이를 통해 사용자 입력을 쉽게 처리할 수 있습니다.

```kotlin
val hourState = rememberPickerState(currentHour)
val selectedHour = hourState.selectedItem
```
## 커스터마이징 예시

글꼴 크기, 색상, 표시되는 아이템 수 등 여러 속성을 변경해 원하는 모양으로 조정할 수 있습니다.

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

## 샘플 프로젝트

라이브러리 사용 예제를 직접 확인하고 싶다면 `sample` 모듈을 실행해 보세요. Android, iOS, Desktop, Web 모든 플랫폼에서 동작하도록 구성되어 있습니다.

```bash
./gradlew :sample:androidApp:installDebug   # Android 설치
./gradlew :sample:desktopApp:run            # Desktop 실행
```

기타 플랫폼의 실행 방법은 `sample/` 디렉터리의 README를 참고하세요.

## 프로젝트 구조

```text
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
└── sample/                        # 샘플 애플리케이션
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
