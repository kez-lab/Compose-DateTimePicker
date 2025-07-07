# Compose DateTimePicker

[Android/Compose Picker 제작 과정기 1부](https://velog.io/@kej_ad/AndroidCompose-Year-Month-DatePicker-%EB%A7%8C%EB%93%A4%EA%B8%B0)

Compose DateTimePicker는 Compose Multiplatform 기반의 날짜 및 시간 선택기 라이브러리입니다. 하나의 코드 베이스로 Android, iOS, Desktop(JVM), Web에서 동일한 UI를 제공하며, 다양한 커스터마이징 옵션을 통해 앱의 스타일에 맞게 쉽게 적용할 수 있습니다.

## 스크린샷

| 통합 샘플(날짜) | 통합 샘플(시간) | 날짜 | 시간 |
| :---: | :---: | :---: | :---: |
| ![Screenshot_20250704_233155](https://github.com/user-attachments/assets/4093bca6-3831-4a68-8abc-7e954cf5fabd) | ![Screenshot_20250704_233221](https://github.com/user-attachments/assets/8468b4e4-6acd-4084-9ea8-6ab394fb43bc) |![화면 기록 2025-07-04 오후 11 33 16](https://github.com/user-attachments/assets/21b1a482-9951-4e06-92d4-86b956cf3a26) | ![화면 기록 2025-07-04 오후 11 32 51](https://github.com/user-attachments/assets/6cafac1f-b95a-44a1-88af-fb5fbac81e63)|






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
    implementation("io.github.kez-lab:compose-date-time-picker:0.3.0")
}
```

## 사용 예제

### TimePicker

`TimePicker`를 사용하여 시간을 선택하고, 선택된 값을 `Text`로 표시하는 전체 예제입니다.

```kotlin
@Composable
fun TimePickerExample() {
    // 현재 시간을 가져오기 위해 Calendar 인스턴스를 사용합니다.
    val calendar = java.util.Calendar.getInstance()
    val currentHour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
    val currentMinute = calendar.get(java.util.Calendar.MINUTE)

    // Picker의 상태를 기억하기 위해 rememberPickerState를 사용합니다.
    val hourState = rememberPickerState(initialItem = currentHour)
    val minuteState = rememberPickerState(initialItem = currentMinute)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        TimePicker(
            hourPickerState = hourState,
            minutePickerState = minuteState,
            timeFormat = TimeFormat.HOUR_24
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "선택된 시간: %02d:%02d".format(hourState.selectedItem, minuteState.selectedItem),
            style = MaterialTheme.typography.h6
        )
    }
}
```

### YearMonthPicker

`YearMonthPicker`를 사용하여 연도와 월을 선택하고, 선택된 값을 `Text`로 표시하는 전체 예제입니다.

```kotlin
@Composable
fun YearMonthPickerExample() {
    // 현재 날짜를 가져옵니다.
    val currentDate = kotlinx.datetime.Clock.System.todayIn(kotlinx.datetime.TimeZone.currentSystemDefault())

    // Picker의 상태를 기억합니다.
    val yearState = rememberPickerState(initialItem = currentDate.year)
    val monthState = rememberPickerState(initialItem = currentDate.monthNumber)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        YearMonthPicker(
            yearPickerState = yearState,
            monthPickerState = monthState
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "선택된 날짜: ${yearState.selectedItem}년 ${monthState.selectedItem}월",
            style = MaterialTheme.typography.h6
        )
    }
}
```

## API 레퍼런스

### TimePicker 파라미터

| 파라미터 | 타입 | 기본값 | 설명 |
| --- | --- | --- | --- |
| `modifier` | `Modifier` | `Modifier` | 컴포넌트에 적용할 Modifier |
| `hourPickerState` | `PickerState<Int>` | `remember { PickerState(currentHour) }` | 시간 선택기의 상태 |
| `minutePickerState` | `PickerState<Int>` | `remember { PickerState(currentMinute) }` | 분 선택기의 상태 |
| `periodPickerState` | `PickerState<TimePeriod>` | `remember { PickerState(TimePeriod.AM) }` | 오전/오후 선택기 상태 (12시간제) |
| `timeFormat` | `TimeFormat` | `TimeFormat.HOUR_24` | 시간 형식 (12시간제 또는 24시간제) |
| `startTime` | `LocalDateTime` | `currentDateTime` | 초기 시간 값 |
| `minuteItems` | `List<Int>` | `0..59` | 분으로 표시할 아이템 리스트 |
| `hourItems` | `List<Int>` | `1..12` 또는 `0..23` | 시간으로 표시할 아이템 리스트 |
| `periodItems` | `List<TimePeriod>` | `AM, PM` | 오전/오후 아이템 리스트 |
| `visibleItemsCount` | `Int` | `3` | 한 번에 보여줄 아이템 개수 |
| `itemPadding` | `PaddingValues` | `PaddingValues(8.dp)` | 각 아이템의 패딩 |
| `textStyle` | `TextStyle` | `TextStyle(fontSize = 16.sp)` | 선택되지 않은 아이템의 텍스트 스타일 |
| `selectedTextStyle` | `TextStyle` | `TextStyle(fontSize = 22.sp)` | 선택된 아이템의 텍스트 스타일 |
| `dividerColor` | `Color` | `LocalContentColor.current` | 구분선 색상 |
| `fadingEdgeGradient` | `Brush` | (기본 그래디언트) | 가장자리 흐림 효과에 사용할 그래디언트 |
| `dividerThickness` | `Dp` | `1.dp` | 구분선 두께 |
| `pickerWidth` | `Dp` | `80.dp` | 각 선택기의 너비 |

### YearMonthPicker 파라미터

| 파라미터 | 타입 | 기본값 | 설명 |
| --- | --- | --- | --- |
| `modifier` | `Modifier` | `Modifier` | 컴포넌트에 적용할 Modifier |
| `yearPickerState` | `PickerState<Int>` | `rememberPickerState(currentDate.year)` | 연도 선택기의 상태 |
| `monthPickerState` | `PickerState<Int>` | `rememberPickerState(currentDate.monthNumber)` | 월 선택기의 상태 |
| `startLocalDate` | `LocalDate` | `currentDate` | 초기 날짜 값 |
| `yearItems` | `List<Int>` | `1900..2100` | 연도로 표시할 아이템 리스트 |
| `monthItems` | `List<Int>` | `1..12` | 월로 표시할 아이템 리스트 |
| `visibleItemsCount` | `Int` | `3` | 한 번에 보여줄 아이템 개수 |
| `itemPadding` | `PaddingValues` | `PaddingValues(8.dp)` | 각 아이템의 패딩 |
| `textStyle` | `TextStyle` | `TextStyle(fontSize = 16.sp)` | 선택되지 않은 아이템의 텍스트 스타일 |
| `selectedTextStyle` | `TextStyle` | `TextStyle(fontSize = 24.sp)` | 선택된 아이템의 텍스트 스타일 |
| `dividerColor` | `Color` | `LocalContentColor.current` | 구분선 색상 |
| `fadingEdgeGradient` | `Brush` | (기본 그래디언트) | 가장자리 흐림 효과에 사용할 그래디언트 |
| `dividerThickness` | `Dp` | `2.dp` | 구분선 두께 |
| `pickerWidth` | `Dp` | `100.dp` | 각 선택기의 너비 |

### Custom Picker
만약 필요한 Custom Picker가 있다면 `Picker`를 사용하여 직접 확장하여 사용하세요.

## 샘플 프로젝트

라이브러리 사용 예제를 직접 확인하고 싶다면 `sample` 모듈을 실행해 보세요. Android, iOS, Desktop, Web 모든 플랫폼에서 동작하도록 구성되어 있습니다.

```bash
./gradlew :sample:androidApp:installDebug   # Android 설치
./gradlew :sample:desktopApp:run            # Desktop 실행
```

기타 플랫폼의 실행 방법은 `sample/` 디렉터리의 README를 참고하세요.

## 기여 방법 (Contributing)

이 프로젝트에 기여하고 싶으시다면 언제든지 환영합니다! 버그를 발견했거나 새로운 기능을 제안하고 싶다면 [Issues](https://github.com/kez-lab/Compose-DateTimePicker/issues)를 통해 알려주세요. 직접 코드를 수정하여 기여하고 싶다면 다음 단계를 따라주세요.

1.  이 저장소를 Fork합니다.
2.  새로운 기능이나 버그 수정을 위한 브랜치를 생성합니다. (`git checkout -b feature/amazing-feature`)
3.  코드를 수정하고 커밋합니다. (`git commit -m 'Add some amazing feature'`)
4.  Fork한 저장소의 브랜치로 Push합니다. (`git push origin feature/amazing-feature`)
5.  Pull Request를 생성합니다.

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
