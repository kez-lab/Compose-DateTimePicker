## 📦 **설치 방법**
```groovy
// groovy
dependencies {
    implementation 'io.github.kez-lab:compose-datepicker:0.0.1'
}
```
```kotlin
// kotlin dsl
dependencies {
    implementation("io.github.kez-lab:compose-datepicker:0.0.1")
} 
```

## Picker 제작 과정기
[[Android/Compose] Picker, NumberPicker, DatePicker 제작 과정기 1부](https://velog.io/@kej_ad/AndroidCompose-Year-Month-DatePicker-%EB%A7%8C%EB%93%A4%EA%B8%B0)

## Picker
기본 Picker로 Number, Date, 카테고리 등등 모든 분야에 활용 가능
```kotlin
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Picker(
    items: List<String>,
    modifier: Modifier = Modifier,
    state: PickerState = rememberPickerState(),
    startIndex: Int = 0,
    visibleItemsCount: Int = 3,
    textModifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current,
    selectedTextStyle: TextStyle = LocalTextStyle.current,
    dividerColor: Color = LocalContentColor.current,
    itemPadding: PaddingValues = PaddingValues(8.dp),
    fadingEdgeGradient: Brush = Brush.verticalGradient(
        0f to Color.Transparent,
        0.5f to Color.Black,
        1f to Color.Transparent
    ),
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    dividerThickness: Dp = 2.dp,
    dividerShape: Shape = RoundedCornerShape(10.dp),
)
```

## YearMonthDatePicker
년도, 월을 선택할 수 있는 Picker
```kotlin
@Composable
fun YearMonthDatePicker(
    modifier: Modifier = Modifier,
    yearPickerState: PickerState = rememberPickerState(),
    monthPickerState: PickerState = rememberPickerState(),
    initYearMonth: YearMonth = YearMonth.now(),
    yearItems: List<String> = YEAR_RANGE,
    monthItems: List<String> = MONTH_RANGE,
    visibleItemsCount: Int = 3,
    itemPadding: PaddingValues = PaddingValues(8.dp),
    textStyle: TextStyle = TextStyle(fontSize = 16.sp),
    selectedTextStyle: TextStyle = TextStyle(fontSize = 24.sp),
    dividerColor: Color = LocalContentColor.current,
    fadingEdgeGradient: Brush = Brush.verticalGradient(
        0f to Color.Transparent,
        0.5f to Color.Black,
        1f to Color.Transparent
    ),
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    dividerThickness: Dp = 2.dp,
    dividerShape: Shape = RoundedCornerShape(10.dp),
    spacingBetweenPickers: Dp = 20.dp,
    pickerWidth: Dp = 100.dp
)
```


![Screen_recording_20240814_235459-ezgif com-video-to-gif-converter (1)](https://github.com/user-attachments/assets/e9cf797b-1bf3-41c8-b32c-4562247c8693)

