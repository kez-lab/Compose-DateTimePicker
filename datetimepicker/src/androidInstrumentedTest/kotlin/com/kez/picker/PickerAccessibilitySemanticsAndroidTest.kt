package com.kez.picker

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.isSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import com.kez.picker.date.DatePicker
import com.kez.picker.date.DatePickerState
import com.kez.picker.date.DateRange
import com.kez.picker.date.DateRangePicker
import com.kez.picker.date.YearMonth
import com.kez.picker.date.YearMonthPicker
import com.kez.picker.date.YearMonthPickerState
import com.kez.picker.date.rememberDateRangePickerState
import com.kez.picker.date.rememberDatePickerState
import com.kez.picker.date.rememberYearMonthPickerState
import com.kez.picker.time.TimePicker
import com.kez.picker.time.TimePickerState
import com.kez.picker.time.rememberTimePickerState
import com.kez.picker.util.TimeFormat
import com.kez.picker.util.TimePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

private const val PREVIOUS_VALUE_ACTION_LABEL = "Previous value"
private const val NEXT_VALUE_ACTION_LABEL = "Next value"

class PickerAccessibilitySemanticsAndroidTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun picker_exposesPickerLabelSelectedValueAndSelectedItemSemantics() {
        composeRule.setContent {
            var selectedItem by remember { mutableStateOf(10) }
            Picker(
                items = listOf(9, 10, 11),
                selectedItem = selectedItem,
                onSelectedItemChange = { selectedItem = it },
                isInfinity = false,
                accessibility = PickerDefaults.accessibility(
                    pickerLabel = "Hour",
                    itemContentDescription = { "$it" }
                )
            )
        }

        composeRule
            .onNode(hasContentDescription("Hour: 10") and hasStateDescription("10"))
            .assertExists()

        composeRule
            .onNode(hasContentDescription("Hour: 10") and isSelected())
            .assertExists()
    }

    @Test
    fun picker_updatesAccessibilitySemanticsWhenSelectionChangesByClick() {
        composeRule.setContent {
            var selectedItem by remember { mutableStateOf(1) }
            Picker(
                items = listOf(1, 2, 3),
                selectedItem = selectedItem,
                onSelectedItemChange = { selectedItem = it },
                style = PickerDefaults.style(visibleItemsCount = 3),
                isInfinity = false,
                accessibility = PickerDefaults.accessibility(
                    pickerLabel = "Day",
                    itemContentDescription = { "$it day" }
                )
            )
        }

        composeRule
            .onNode(hasContentDescription("Day: 2 day"))
            .performClick()

        composeRule.waitForIdle()

        composeRule
            .onNode(hasContentDescription("Day: 2 day") and hasStateDescription("2 day"))
            .assertExists()

        composeRule
            .onNode(hasContentDescription("Day: 2 day") and isSelected())
            .assertIsSelected()
    }

    @Test
    fun picker_customAccessibilityActionsSelectAdjacentItems() {
        lateinit var selectedItemState: MutableState<Int>

        composeRule.setContent {
            selectedItemState = remember { mutableStateOf(2) }
            Picker(
                items = listOf(1, 2, 3),
                selectedItem = selectedItemState.value,
                onSelectedItemChange = { selectedItemState.value = it },
                style = PickerDefaults.style(visibleItemsCount = 3),
                isInfinity = false,
                accessibility = PickerDefaults.accessibility(
                    pickerLabel = "Value",
                    itemContentDescription = { "$it" },
                    previousItemActionLabel = PREVIOUS_VALUE_ACTION_LABEL,
                    nextItemActionLabel = NEXT_VALUE_ACTION_LABEL
                )
            )
        }

        composeRule
            .onNode(
                hasContentDescription("Value: 2") and
                        hasCustomAccessibilityAction(PREVIOUS_VALUE_ACTION_LABEL) and
                        hasCustomAccessibilityAction(NEXT_VALUE_ACTION_LABEL)
            )
            .assertExists()

        performCustomAccessibilityAction(
            contentDescription = "Value: 2",
            actionLabel = NEXT_VALUE_ACTION_LABEL
        )
        waitUntilSelectedItem("Value: 3")

        composeRule.runOnIdle {
            assertEquals(3, selectedItemState.value)
        }

        performCustomAccessibilityAction(
            contentDescription = "Value: 3",
            actionLabel = PREVIOUS_VALUE_ACTION_LABEL
        )
        waitUntilSelectedItem("Value: 2")

        composeRule.runOnIdle {
            assertEquals(2, selectedItemState.value)
        }
    }

    @Test
    fun picker_customAccessibilityActionsRespectBoundedEdges() {
        composeRule.setContent {
            var selectedItem by remember { mutableStateOf(1) }
            Picker(
                items = listOf(1, 2, 3),
                selectedItem = selectedItem,
                onSelectedItemChange = { selectedItem = it },
                style = PickerDefaults.style(visibleItemsCount = 3),
                isInfinity = false,
                accessibility = PickerDefaults.accessibility(
                    pickerLabel = "Value",
                    itemContentDescription = { "$it" },
                    previousItemActionLabel = PREVIOUS_VALUE_ACTION_LABEL,
                    nextItemActionLabel = NEXT_VALUE_ACTION_LABEL
                )
            )
        }

        assertNoCustomAccessibilityAction("Value: 1", PREVIOUS_VALUE_ACTION_LABEL)
        assertCustomAccessibilityAction("Value: 1", NEXT_VALUE_ACTION_LABEL)

        performCustomAccessibilityAction(
            contentDescription = "Value: 1",
            actionLabel = NEXT_VALUE_ACTION_LABEL
        )
        waitUntilSelectedItem("Value: 2")

        performCustomAccessibilityAction(
            contentDescription = "Value: 2",
            actionLabel = NEXT_VALUE_ACTION_LABEL
        )
        waitUntilSelectedItem("Value: 3")

        assertCustomAccessibilityAction("Value: 3", PREVIOUS_VALUE_ACTION_LABEL)
        assertNoCustomAccessibilityAction("Value: 3", NEXT_VALUE_ACTION_LABEL)
    }

    @Test
    fun picker_customAccessibilityActionsWrapInInfiniteMode() {
        composeRule.setContent {
            var selectedItem by remember { mutableStateOf(1) }
            Picker(
                items = listOf(1, 2, 3),
                selectedItem = selectedItem,
                onSelectedItemChange = { selectedItem = it },
                style = PickerDefaults.style(visibleItemsCount = 3),
                isInfinity = true,
                accessibility = PickerDefaults.accessibility(
                    pickerLabel = "Value",
                    itemContentDescription = { "$it" },
                    previousItemActionLabel = PREVIOUS_VALUE_ACTION_LABEL,
                    nextItemActionLabel = NEXT_VALUE_ACTION_LABEL
                )
            )
        }

        performCustomAccessibilityAction(
            contentDescription = "Value: 1",
            actionLabel = PREVIOUS_VALUE_ACTION_LABEL
        )
        waitUntilSelectedItem("Value: 3")

        performCustomAccessibilityAction(
            contentDescription = "Value: 3",
            actionLabel = NEXT_VALUE_ACTION_LABEL
        )
        waitUntilSelectedItem("Value: 1")
    }

    @Test
    fun picker_omitsCustomAccessibilityActionsForSingleItemInfinitePicker() {
        composeRule.setContent {
            var selectedItem by remember { mutableStateOf(1) }
            Picker(
                items = listOf(1),
                selectedItem = selectedItem,
                onSelectedItemChange = { selectedItem = it },
                style = PickerDefaults.style(visibleItemsCount = 3),
                isInfinity = true,
                accessibility = PickerDefaults.accessibility(
                    pickerLabel = "Value",
                    itemContentDescription = { "$it" },
                    previousItemActionLabel = PREVIOUS_VALUE_ACTION_LABEL,
                    nextItemActionLabel = NEXT_VALUE_ACTION_LABEL
                )
            )
        }

        assertNoCustomAccessibilityAction("Value: 1", PREVIOUS_VALUE_ACTION_LABEL)
        assertNoCustomAccessibilityAction("Value: 1", NEXT_VALUE_ACTION_LABEL)
    }

    @Test
    fun picker_customAccessibilityActionsUseValueDescriptionWhenLabelIsOmitted() {
        composeRule.setContent {
            var selectedItem by remember { mutableStateOf(2) }
            Picker(
                items = listOf(1, 2, 3),
                selectedItem = selectedItem,
                onSelectedItemChange = { selectedItem = it },
                style = PickerDefaults.style(visibleItemsCount = 3),
                isInfinity = false,
                accessibility = PickerDefaults.accessibility(
                    pickerLabel = null,
                    itemContentDescription = { "$it item" },
                    previousItemActionLabel = PREVIOUS_VALUE_ACTION_LABEL,
                    nextItemActionLabel = NEXT_VALUE_ACTION_LABEL
                )
            )
        }

        composeRule
            .onNode(
                hasContentDescription("2 item") and
                        hasStateDescription("2 item") and
                        hasCustomAccessibilityAction(PREVIOUS_VALUE_ACTION_LABEL) and
                        hasCustomAccessibilityAction(NEXT_VALUE_ACTION_LABEL)
            )
            .assertExists()
    }

    @Test
    fun picker_disabledOmitsCustomAccessibilityActionsAndExposesDisabledSemantics() {
        composeRule.setContent {
            var selectedItem by remember { mutableStateOf(2) }
            Picker(
                items = listOf(1, 2, 3),
                selectedItem = selectedItem,
                onSelectedItemChange = { selectedItem = it },
                enabled = false,
                style = PickerDefaults.style(visibleItemsCount = 3),
                isInfinity = false,
                accessibility = PickerDefaults.accessibility(
                    pickerLabel = "Value",
                    itemContentDescription = { "$it" },
                    previousItemActionLabel = PREVIOUS_VALUE_ACTION_LABEL,
                    nextItemActionLabel = NEXT_VALUE_ACTION_LABEL
                )
            )
        }

        composeRule
            .onNode(hasContentDescription("Value: 2") and hasStateDescription("2"))
            .assertIsNotEnabled()

        assertNoCustomAccessibilityActions("Value: 2")
    }

    @Test
    fun picker_omitsCustomAccessibilityActionsWhenActionLabelsAreNullOrBlank() {
        composeRule.setContent {
            var selectedItem by remember { mutableStateOf(2) }
            Picker(
                items = listOf(1, 2, 3),
                selectedItem = selectedItem,
                onSelectedItemChange = { selectedItem = it },
                style = PickerDefaults.style(visibleItemsCount = 3),
                isInfinity = false,
                accessibility = PickerDefaults.accessibility(
                    pickerLabel = "Value",
                    itemContentDescription = { "$it" },
                    previousItemActionLabel = null,
                    nextItemActionLabel = " "
                )
            )
        }

        assertNoCustomAccessibilityActions("Value: 2")
    }

    @Test
    fun picker_updatesAccessibilitySemanticsWhenSelectionChangesProgrammatically() {
        lateinit var selectedItemState: MutableState<Int>

        composeRule.setContent {
            selectedItemState = remember { mutableStateOf(1) }
            Picker(
                items = (1..30).toList(),
                selectedItem = selectedItemState.value,
                onSelectedItemChange = { selectedItemState.value = it },
                style = PickerDefaults.style(visibleItemsCount = 3),
                isInfinity = false,
                accessibility = PickerDefaults.accessibility(
                    pickerLabel = "Value",
                    itemContentDescription = { "$it" }
                )
            )
        }

        composeRule.runOnIdle {
            selectedItemState.value = 30
        }

        waitUntilSelectedItem("Value: 30")

        composeRule
            .onNode(hasContentDescription("Value: 30") and hasStateDescription("30"))
            .assertExists()

        composeRule
            .onNode(hasContentDescription("Value: 30") and isSelected())
            .assertIsSelected()
    }

    @Test
    fun timePicker_updatesChildPickerSemanticsWhenSelectionChangesProgrammatically() {
        lateinit var state: TimePickerState

        composeRule.setContent {
            state = rememberTimePickerState(
                initialTime = LocalTime(hour = 1, minute = 0),
                timeFormat = TimeFormat.HOUR_12
            )

            TimePicker(
                state = state,
                items = PickerDefaults.timePickerItems(
                    minuteItems = listOf(0, 30),
                    hour12Items = listOf(1, 2, 3),
                    periodItems = listOf(TimePeriod.AM, TimePeriod.PM)
                ),
                style = PickerDefaults.style(visibleItemsCount = 3),
                accessibility = PickerDefaults.timePickerAccessibility(
                    hourPickerLabel = "시간",
                    minutePickerLabel = "분",
                    periodPickerLabel = "오전/오후",
                    hourItemContentDescription = { "${it}시" },
                    minuteItemContentDescription = { "${it}분" },
                    periodItemContentDescription = {
                        when (it) {
                            TimePeriod.AM -> "오전"
                            TimePeriod.PM -> "오후"
                        }
                    }
                )
            )
        }

        composeRule.runOnIdle {
            state.selectTime(LocalTime(hour = 14, minute = 30))
        }

        waitUntilSelectedItem("시간: 2시")
        waitUntilSelectedItem("분: 30분")
        waitUntilSelectedItem("오전/오후: 오후")
    }

    @Test
    fun datePicker_updatesChildPickerSemanticsWhenSelectionChangesProgrammatically() {
        lateinit var state: DatePickerState

        composeRule.setContent {
            state = rememberDatePickerState(
                initialYear = 2026,
                initialMonth = 1,
                initialDay = 1
            )

            DatePicker(
                state = state,
                items = PickerDefaults.datePickerItems(
                    yearItems = listOf(2026, 2027),
                    monthItems = listOf(1, 5)
                ),
                style = PickerDefaults.style(visibleItemsCount = 3),
                accessibility = PickerDefaults.datePickerAccessibility(
                    yearPickerLabel = "연도",
                    monthPickerLabel = "월",
                    dayPickerLabel = "일",
                    yearItemContentDescription = { "${it}년" },
                    monthItemContentDescription = { "${it}월" },
                    dayItemContentDescription = { "${it}일" }
                )
            )
        }

        composeRule.runOnIdle {
            state.selectDate(LocalDate(2027, 5, 20))
        }

        waitUntilSelectedItem("연도: 2027년")
        waitUntilSelectedItem("월: 5월")
        waitUntilSelectedItem("일: 20일")
    }

    @Test
    fun picker_omitsBlankLabelFromItemContentDescription() {
        composeRule.setContent {
            var selectedItem by remember { mutableStateOf(2) }
            Picker(
                items = listOf(1, 2, 3),
                selectedItem = selectedItem,
                onSelectedItemChange = { selectedItem = it },
                isInfinity = false,
                accessibility = PickerDefaults.accessibility(
                    pickerLabel = "   ",
                    itemContentDescription = { "$it item" }
                )
            )
        }

        composeRule
            .onNode(hasContentDescription("2 item") and isSelected())
            .assertExists()
    }

    @Test
    fun timePicker_forwardsCustomHourAndMinuteAccessibilityDescriptions() {
        composeRule.setContent {
            val state = rememberTimePickerState(
                initialTime = LocalTime(hour = 10, minute = 5),
                timeFormat = TimeFormat.HOUR_24
            )

            TimePicker(
                state = state,
                items = PickerDefaults.timePickerItems(
                    minuteItems = listOf(5, 10),
                    hour24Items = listOf(10, 11)
                ),
                style = PickerDefaults.style(visibleItemsCount = 3),
                accessibility = PickerDefaults.timePickerAccessibility(
                    hourPickerLabel = "시간",
                    minutePickerLabel = "분",
                    hourItemContentDescription = { "${it}시" },
                    minuteItemContentDescription = { "${it}분" }
                )
            )
        }

        composeRule
            .onNode(hasContentDescription("시간: 10시") and hasStateDescription("10시"))
            .assertExists()

        composeRule
            .onNode(hasContentDescription("분: 5분") and hasStateDescription("5분"))
            .assertExists()
    }

    @Test
    fun timePicker_forwardsCustomAccessibilityActionLabelsToChildPickers() {
        composeRule.setContent {
            val state = rememberTimePickerState(
                initialTime = LocalTime(hour = 10, minute = 5),
                timeFormat = TimeFormat.HOUR_24
            )

            TimePicker(
                state = state,
                items = PickerDefaults.timePickerItems(
                    minuteItems = listOf(5, 10),
                    hour24Items = listOf(10, 11)
                ),
                style = PickerDefaults.style(visibleItemsCount = 3),
                accessibility = PickerDefaults.timePickerAccessibility(
                    hourPickerLabel = "시간",
                    minutePickerLabel = "분",
                    hourItemContentDescription = { "${it}시" },
                    minuteItemContentDescription = { "${it}분" },
                    previousItemActionLabel = "이전 항목 선택",
                    nextItemActionLabel = "다음 항목 선택"
                )
            )
        }

        composeRule
            .onNode(
                hasContentDescription("시간: 10시") and
                        hasCustomAccessibilityAction("다음 항목 선택")
            )
            .assertExists()
        composeRule
            .onNode(
                hasContentDescription("분: 5분") and
                        hasCustomAccessibilityAction("다음 항목 선택")
            )
            .assertExists()
    }

    @Test
    fun timePicker_customAccessibilityActionsUpdateChildStates() {
        lateinit var state: TimePickerState

        composeRule.setContent {
            state = rememberTimePickerState(
                initialTime = LocalTime(hour = 10, minute = 5),
                timeFormat = TimeFormat.HOUR_12
            )

            TimePicker(
                state = state,
                items = PickerDefaults.timePickerItems(
                    minuteItems = listOf(5, 10),
                    hour12Items = listOf(10, 11),
                    periodItems = listOf(TimePeriod.AM, TimePeriod.PM)
                ),
                style = PickerDefaults.style(visibleItemsCount = 3),
                accessibility = PickerDefaults.timePickerAccessibility(
                    hourPickerLabel = "시간",
                    minutePickerLabel = "분",
                    periodPickerLabel = "오전/오후",
                    hourItemContentDescription = { "${it}시" },
                    minuteItemContentDescription = { "${it}분" },
                    periodItemContentDescription = {
                        when (it) {
                            TimePeriod.AM -> "오전"
                            TimePeriod.PM -> "오후"
                        }
                    },
                    previousItemActionLabel = PREVIOUS_VALUE_ACTION_LABEL,
                    nextItemActionLabel = NEXT_VALUE_ACTION_LABEL
                )
            )
        }

        performCustomAccessibilityAction(
            contentDescription = "시간: 10시",
            actionLabel = NEXT_VALUE_ACTION_LABEL
        )
        waitUntilSelectedItem("시간: 11시")

        performCustomAccessibilityAction(
            contentDescription = "분: 5분",
            actionLabel = NEXT_VALUE_ACTION_LABEL
        )
        waitUntilSelectedItem("분: 10분")

        performCustomAccessibilityAction(
            contentDescription = "오전/오후: 오전",
            actionLabel = NEXT_VALUE_ACTION_LABEL
        )
        waitUntilSelectedItem("오전/오후: 오후")

        composeRule.runOnIdle {
            assertEquals(TimePeriod.PM, state.selectedPeriod)
        }

        performCustomAccessibilityAction(
            contentDescription = "오전/오후: 오후",
            actionLabel = PREVIOUS_VALUE_ACTION_LABEL
        )
        waitUntilSelectedItem("오전/오후: 오전")

        composeRule.runOnIdle {
            assertEquals(LocalTime(hour = 11, minute = 10), state.selectedTime)
        }
    }

    @Test
    fun timePicker_callsOnSelectedTimeChangeAfterUserSelection() {
        lateinit var changedTimeState: MutableState<LocalTime?>

        composeRule.setContent {
            val state = rememberTimePickerState(
                initialTime = LocalTime(hour = 10, minute = 5),
                timeFormat = TimeFormat.HOUR_24
            )
            changedTimeState = remember { mutableStateOf(null) }

            TimePicker(
                state = state,
                onSelectedTimeChange = { changedTimeState.value = it },
                items = PickerDefaults.timePickerItems(
                    minuteItems = listOf(5, 10),
                    hour24Items = listOf(10, 11)
                ),
                style = PickerDefaults.style(visibleItemsCount = 3),
                accessibility = PickerDefaults.timePickerAccessibility(
                    hourPickerLabel = "시간",
                    minutePickerLabel = "분",
                    hourItemContentDescription = { "${it}시" },
                    minuteItemContentDescription = { "${it}분" },
                    nextItemActionLabel = NEXT_VALUE_ACTION_LABEL
                )
            )
        }

        performCustomAccessibilityAction(
            contentDescription = "시간: 10시",
            actionLabel = NEXT_VALUE_ACTION_LABEL
        )
        waitUntilSelectedItem("시간: 11시")

        composeRule.runOnIdle {
            assertEquals(LocalTime(hour = 11, minute = 5), changedTimeState.value)
        }
    }

    @Test
    fun timePicker_forwardsCustomPeriodAccessibilityDescriptionIn12HourMode() {
        composeRule.setContent {
            val state = rememberTimePickerState(
                initialTime = LocalTime(hour = 22, minute = 5),
                timeFormat = TimeFormat.HOUR_12
            )

            TimePicker(
                state = state,
                items = PickerDefaults.timePickerItems(
                    minuteItems = listOf(5, 10),
                    hour12Items = listOf(10, 11),
                    periodItems = listOf(TimePeriod.AM, TimePeriod.PM)
                ),
                style = PickerDefaults.style(visibleItemsCount = 3),
                accessibility = PickerDefaults.timePickerAccessibility(
                    periodPickerLabel = "오전/오후",
                    periodItemContentDescription = {
                        when (it) {
                            TimePeriod.AM -> "오전"
                            TimePeriod.PM -> "오후"
                        }
                    }
                )
            )
        }

        composeRule
            .onNode(hasContentDescription("오전/오후: 오후") and hasStateDescription("오후"))
            .assertExists()
    }

    @Test
    fun datePicker_forwardsCustomAccessibilityDescriptionsToChildPickers() {
        composeRule.setContent {
            val state = rememberDatePickerState(
                initialYear = 2026,
                initialMonth = 5,
                initialDay = 20
            )

            DatePicker(
                state = state,
                items = PickerDefaults.datePickerItems(
                    yearItems = listOf(2026),
                    monthItems = listOf(5)
                ),
                style = PickerDefaults.style(visibleItemsCount = 3),
                accessibility = PickerDefaults.datePickerAccessibility(
                    yearPickerLabel = "연도",
                    monthPickerLabel = "월",
                    dayPickerLabel = "일",
                    yearItemContentDescription = { "${it}년" },
                    monthItemContentDescription = { "${it}월" },
                    dayItemContentDescription = { "${it}일" }
                )
            )
        }

        composeRule
            .onNode(hasContentDescription("연도: 2026년") and hasStateDescription("2026년"))
            .assertExists()

        composeRule
            .onNode(hasContentDescription("월: 5월") and hasStateDescription("5월"))
            .assertExists()

        composeRule
            .onNode(hasContentDescription("일: 20일") and hasStateDescription("20일"))
            .assertExists()
    }

    @Test
    fun datePicker_customAccessibilityActionsUpdateChildStatesAndClampDay() {
        lateinit var state: DatePickerState

        composeRule.setContent {
            state = rememberDatePickerState(
                initialYear = 2026,
                initialMonth = 1,
                initialDay = 31
            )

            DatePicker(
                state = state,
                items = PickerDefaults.datePickerItems(
                    yearItems = listOf(2026, 2027),
                    monthItems = listOf(1, 2)
                ),
                style = PickerDefaults.style(visibleItemsCount = 3),
                accessibility = PickerDefaults.datePickerAccessibility(
                    yearPickerLabel = "연도",
                    monthPickerLabel = "월",
                    dayPickerLabel = "일",
                    yearItemContentDescription = { "${it}년" },
                    monthItemContentDescription = { "${it}월" },
                    dayItemContentDescription = { "${it}일" },
                    previousItemActionLabel = PREVIOUS_VALUE_ACTION_LABEL,
                    nextItemActionLabel = NEXT_VALUE_ACTION_LABEL
                )
            )
        }

        performCustomAccessibilityAction(
            contentDescription = "연도: 2026년",
            actionLabel = NEXT_VALUE_ACTION_LABEL
        )
        waitUntilSelectedItem("연도: 2027년")

        performCustomAccessibilityAction(
            contentDescription = "월: 1월",
            actionLabel = NEXT_VALUE_ACTION_LABEL
        )
        waitUntilSelectedItem("월: 2월")
        waitUntilSelectedItem("일: 28일")

        composeRule.runOnIdle {
            assertEquals(2027, state.selectedYear)
            assertEquals(2, state.selectedMonth)
            assertEquals(28, state.selectedDay)
            assertEquals(
                LocalDate(year = 2027, month = Month.FEBRUARY, day = 28),
                state.selectedDate
            )
        }

        performCustomAccessibilityAction(
            contentDescription = "일: 28일",
            actionLabel = PREVIOUS_VALUE_ACTION_LABEL
        )
        waitUntilSelectedItem("일: 27일")

        composeRule.runOnIdle {
            assertEquals(27, state.selectedDay)
            assertEquals(
                LocalDate(year = 2027, month = Month.FEBRUARY, day = 27),
                state.selectedDate
            )
        }
    }

    @Test
    fun datePicker_customDayItemsAdjustSelectionWhenMonthChanges() {
        lateinit var state: DatePickerState

        composeRule.setContent {
            state = rememberDatePickerState(
                initialYear = 2026,
                initialMonth = 1,
                initialDay = 31
            )

            DatePicker(
                state = state,
                items = PickerDefaults.datePickerItems(
                    yearItems = listOf(2026),
                    monthItems = listOf(1, 2),
                    dayItems = listOf(15, 31)
                ),
                style = PickerDefaults.style(visibleItemsCount = 3),
                accessibility = PickerDefaults.datePickerAccessibility(
                    monthPickerLabel = "월",
                    dayPickerLabel = "일",
                    monthItemContentDescription = { "${it}월" },
                    dayItemContentDescription = { "${it}일" },
                    nextItemActionLabel = NEXT_VALUE_ACTION_LABEL
                )
            )
        }

        performCustomAccessibilityAction(
            contentDescription = "월: 1월",
            actionLabel = NEXT_VALUE_ACTION_LABEL
        )
        waitUntilSelectedItem("월: 2월")
        waitUntilSelectedItem("일: 15일")

        composeRule.runOnIdle {
            assertEquals(LocalDate(year = 2026, month = Month.FEBRUARY, day = 15), state.selectedDate)
        }
    }

    @Test
    fun datePicker_callsOnSelectedDateChangeAfterUserSelection() {
        lateinit var changedDateState: MutableState<LocalDate?>

        composeRule.setContent {
            val state = rememberDatePickerState(
                initialYear = 2026,
                initialMonth = 1,
                initialDay = 31
            )
            changedDateState = remember { mutableStateOf(null) }

            DatePicker(
                state = state,
                onSelectedDateChange = { changedDateState.value = it },
                items = PickerDefaults.datePickerItems(
                    yearItems = listOf(2026),
                    monthItems = listOf(1, 2)
                ),
                style = PickerDefaults.style(visibleItemsCount = 3),
                accessibility = PickerDefaults.datePickerAccessibility(
                    monthPickerLabel = "월",
                    monthItemContentDescription = { "${it}월" },
                    nextItemActionLabel = NEXT_VALUE_ACTION_LABEL
                )
            )
        }

        performCustomAccessibilityAction(
            contentDescription = "월: 1월",
            actionLabel = NEXT_VALUE_ACTION_LABEL
        )
        waitUntilSelectedItem("월: 2월")

        composeRule.runOnIdle {
            assertEquals(LocalDate(year = 2026, month = Month.FEBRUARY, day = 28), changedDateState.value)
        }
    }

    @Test
    fun dateRangePicker_callsOnSelectedDateRangeChangeAfterStartSelection() {
        lateinit var changedRangeState: MutableState<DateRange?>

        composeRule.setContent {
            val state = rememberDateRangePickerState(
                initialStartDate = LocalDate(2026, 1, 10),
                initialEndDate = LocalDate(2026, 1, 20)
            )
            changedRangeState = remember { mutableStateOf(null) }

            DateRangePicker(
                state = state,
                onSelectedDateRangeChange = { changedRangeState.value = it },
                items = PickerDefaults.datePickerItems(
                    yearItems = listOf(2026),
                    monthItems = listOf(1),
                    dayItems = listOf(10, 11, 20)
                ),
                style = PickerDefaults.style(visibleItemsCount = 3),
                accessibility = PickerDefaults.dateRangePickerAccessibility(
                    start = PickerDefaults.datePickerAccessibility(
                        dayPickerLabel = "시작 일",
                        dayItemContentDescription = { "${it}일" },
                        nextItemActionLabel = NEXT_VALUE_ACTION_LABEL
                    )
                )
            )
        }

        performCustomAccessibilityAction(
            contentDescription = "시작 일: 10일",
            actionLabel = NEXT_VALUE_ACTION_LABEL
        )
        waitUntilSelectedItem("시작 일: 11일")

        composeRule.runOnIdle {
            assertEquals(
                DateRange(
                    startDate = LocalDate(2026, 1, 11),
                    endDate = LocalDate(2026, 1, 20)
                ),
                changedRangeState.value
            )
        }
    }

    @Test
    fun yearMonthPicker_forwardsCustomAccessibilityDescriptionsToChildPickers() {
        composeRule.setContent {
            val state = rememberYearMonthPickerState(
                initialYear = 2026,
                initialMonth = 5
            )

            YearMonthPicker(
                state = state,
                items = PickerDefaults.yearMonthPickerItems(
                    yearItems = listOf(2026),
                    monthItems = listOf(5)
                ),
                style = PickerDefaults.style(visibleItemsCount = 3),
                accessibility = PickerDefaults.yearMonthPickerAccessibility(
                    yearPickerLabel = "연도",
                    monthPickerLabel = "월",
                    yearItemContentDescription = { "${it}년" },
                    monthItemContentDescription = { "${it}월" }
                )
            )
        }

        composeRule
            .onNode(hasContentDescription("연도: 2026년") and hasStateDescription("2026년"))
            .assertExists()

        composeRule
            .onNode(hasContentDescription("월: 5월") and hasStateDescription("5월"))
            .assertExists()
    }

    @Test
    fun yearMonthPicker_customAccessibilityActionsUpdateChildStates() {
        lateinit var state: YearMonthPickerState

        composeRule.setContent {
            state = rememberYearMonthPickerState(
                initialYear = 2026,
                initialMonth = 5
            )

            YearMonthPicker(
                state = state,
                items = PickerDefaults.yearMonthPickerItems(
                    yearItems = listOf(2026, 2027),
                    monthItems = listOf(5, 6)
                ),
                style = PickerDefaults.style(visibleItemsCount = 3),
                accessibility = PickerDefaults.yearMonthPickerAccessibility(
                    yearPickerLabel = "연도",
                    monthPickerLabel = "월",
                    yearItemContentDescription = { "${it}년" },
                    monthItemContentDescription = { "${it}월" },
                    previousItemActionLabel = PREVIOUS_VALUE_ACTION_LABEL,
                    nextItemActionLabel = NEXT_VALUE_ACTION_LABEL
                )
            )
        }

        performCustomAccessibilityAction(
            contentDescription = "연도: 2026년",
            actionLabel = NEXT_VALUE_ACTION_LABEL
        )
        waitUntilSelectedItem("연도: 2027년")

        performCustomAccessibilityAction(
            contentDescription = "월: 5월",
            actionLabel = NEXT_VALUE_ACTION_LABEL
        )
        waitUntilSelectedItem("월: 6월")

        performCustomAccessibilityAction(
            contentDescription = "월: 6월",
            actionLabel = PREVIOUS_VALUE_ACTION_LABEL
        )
        waitUntilSelectedItem("월: 5월")

        composeRule.runOnIdle {
            assertEquals(
                LocalDate(year = 2027, month = Month.MAY, day = 1),
                state.selectedMonthDate
            )
        }
    }

    @Test
    fun yearMonthPicker_callsOnSelectedYearMonthChangeAfterUserSelection() {
        lateinit var changedYearMonthState: MutableState<YearMonth?>

        composeRule.setContent {
            val state = rememberYearMonthPickerState(
                initialYear = 2026,
                initialMonth = 5
            )
            changedYearMonthState = remember { mutableStateOf(null) }

            YearMonthPicker(
                state = state,
                onSelectedYearMonthChange = { changedYearMonthState.value = it },
                items = PickerDefaults.yearMonthPickerItems(
                    yearItems = listOf(2026),
                    monthItems = listOf(5, 6)
                ),
                style = PickerDefaults.style(visibleItemsCount = 3),
                accessibility = PickerDefaults.yearMonthPickerAccessibility(
                    monthPickerLabel = "월",
                    monthItemContentDescription = { "${it}월" },
                    nextItemActionLabel = NEXT_VALUE_ACTION_LABEL
                )
            )
        }

        performCustomAccessibilityAction(
            contentDescription = "월: 5월",
            actionLabel = NEXT_VALUE_ACTION_LABEL
        )
        waitUntilSelectedItem("월: 6월")

        composeRule.runOnIdle {
            assertEquals(YearMonth(year = 2026, month = 6), changedYearMonthState.value)
        }
    }

    private fun waitUntilSelectedItem(contentDescription: String) {
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule
                .onAllNodes(hasContentDescription(contentDescription) and isSelected())
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
    }

    private fun performCustomAccessibilityAction(
        contentDescription: String,
        actionLabel: String
    ) {
        val action = composeRule
            .onNode(
                hasContentDescription(contentDescription) and
                        hasCustomAccessibilityAction(actionLabel)
            )
            .fetchSemanticsNode()
            .config[SemanticsActions.CustomActions]
            .first { it.label == actionLabel }

        composeRule.runOnIdle {
            assertTrue(action.action())
        }
    }

    private fun assertCustomAccessibilityAction(
        contentDescription: String,
        actionLabel: String
    ) {
        composeRule
            .onNode(hasContentDescription(contentDescription) and hasCustomAccessibilityAction(actionLabel))
            .assertExists()
    }

    private fun assertNoCustomAccessibilityAction(
        contentDescription: String,
        actionLabel: String
    ) {
        val nodes = composeRule
            .onAllNodes(hasContentDescription(contentDescription) and hasCustomAccessibilityAction(actionLabel))
            .fetchSemanticsNodes()

        assertTrue(nodes.isEmpty())
    }

    private fun assertNoCustomAccessibilityActions(contentDescription: String) {
        val actions = composeRule
            .onAllNodes(hasContentDescription(contentDescription))
            .fetchSemanticsNodes()
            .flatMap { node ->
                node.config
                    .getOrNull(SemanticsActions.CustomActions)
                    .orEmpty()
            }

        assertTrue(actions.isNullOrEmpty())
    }
}

private fun hasStateDescription(value: String): SemanticsMatcher =
    SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, value)

private fun hasCustomAccessibilityAction(label: String): SemanticsMatcher =
    SemanticsMatcher("has custom accessibility action '$label'") { node ->
        node.config.getOrNull(SemanticsActions.CustomActions)
            ?.any { action -> action.label == label } == true
    }
