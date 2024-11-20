
## Picker 제작 과정기
[[Android/Compose] Picker, NumberPicker, DatePicker 제작 과정기 1부](https://velog.io/@kej_ad/AndroidCompose-Year-Month-DatePicker-%EB%A7%8C%EB%93%A4%EA%B8%B0)

# Compose-DateTimePicker
`Compose-DateTimePicker`는 Jetpack Compose를 사용하여 간단하고 유연한 날짜 및 시간 선택기를 제공하는 라이브러리입니다. 이 라이브러리를 사용하면 Android 애플리케이션에서 아름답고 직관적인 사용자 인터페이스를 쉽게 구현할 수 있습니다.

## 기능

- \[x\] 간단한 시간 선택기
- \[x\] 간단한 날짜 선택기 (년/월)
- \[x\] 무한 스크롤 지원
- \[x\] 텍스트 스타일 및 아이템 배치 조정
- \[x\] 커스터마이징 가능한 디자인

## 설치

### Gradle

1. 프로젝트의 `build.gradle` 파일에 Maven Central을 추가합니다:

    ```groovy
    allprojects {
        repositories {
            mavenCentral()
        }
    }
    ```

2. 모듈의 `build.gradle` 파일에 라이브러리를 추가합니다:

    ```kotlin
    dependencies {
        implementation("io.github.kez-lab:compose-date-time-picker:0.0.2")
    }
    ```

## 사용법

### 시간 선택기 다이얼로그(TimePickerDialog)
<img src="https://github.com/user-attachments/assets/90cc3bbb-6e28-40e9-8480-4924b362d7c6" alt="TimePickerDialog" width="500"/>

### 시간 선택기(TimePicker)
간단한 시간 선택기를 사용하려면 `TimePicker` 컴포저블을 사용합니다.

```kotlin
@Composable
fun MyTimePicker() {
    TimePicker(
        initHour = 12,
        initMinute = 30,
        periodPickerState = rememberPickerState(),
        hourPickerState = rememberPickerState(),
        minutePickerState = rememberPickerState()
    )
}
```

#### 매개변수

- `initHour`: 초기 시간 설정 (기본값: `currentHour`)
- `initMinute`: 초기 분 설정 (기본값: `currentMinute`)
- `periodPickerState`: 오전/오후 선택기 상태
- `hourPickerState`: 시간 선택기 상태
- `minutePickerState`: 분 선택기 상태

### 날짜 선택기(YearMonthDatePicker)

년/월 선택기를 사용하려면 `YearMonthDatePicker` 컴포저블을 사용합니다.

```kotlin
@Composable
fun MyDatePicker() {
    YearMonthPicker(
        initYearMonth = YearMonth.now(),
        yearPickerState = rememberPickerState(),
        monthPickerState = rememberPickerState()
    )
}
```

#### 매개변수

- `initYearMonth`: 초기 연/월 설정 (기본값: `YearMonth.now()`)
- `yearPickerState`: 연 선택기 상태
- `monthPickerState`: 월 선택기 상태

## 커스터마이징

모든 컴포저블은 텍스트 스타일, 간격, 색상 및 더 많은 속성을 커스터마이징할 수 있도록 구성되어 있습니다. 예를 들어, 텍스트 스타일을 커스터마이징하려면 다음과 같이 할 수 있습니다:

```kotlin
@Composable
fun CustomTimePicker() {
    TimePicker(
        textStyle = TextStyle(fontSize = 18.sp, color = Color.Gray),
        selectedTextStyle = TextStyle(fontSize = 24.sp, color = Color.Black)
    )
}
```

## 라이선스

`Compose-DateTimePicker`는 [Apache License 2.0](./LICENSE)에 따라 라이선스가 부여됩니다. 자세한 내용은 LICENSE 파일을 참조하십시오.


