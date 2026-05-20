package com.kez.picker

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.isSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import com.kez.picker.date.DatePicker
import com.kez.picker.date.DatePickerState
import com.kez.picker.date.YearMonthPicker
import com.kez.picker.date.rememberDatePickerState
import com.kez.picker.time.TimePicker
import com.kez.picker.util.TimeFormat
import com.kez.picker.util.TimePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class PickerAccessibilitySemanticsAndroidTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun picker_exposesPickerLabelSelectedValueAndSelectedItemSemantics() {
        composeRule.setContent {
            val state = rememberPickerState(10)

            Picker(
                items = listOf(9, 10, 11),
                state = state,
                startIndex = 1,
                isInfinity = false,
                pickerLabel = "Hour",
                itemContentDescription = { "$it" }
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
            val state = rememberPickerState(1)

            Picker(
                items = listOf(1, 2, 3),
                state = state,
                visibleItemsCount = 3,
                isInfinity = false,
                pickerLabel = "Day",
                itemContentDescription = { "$it day" }
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
    fun picker_updatesAccessibilitySemanticsWhenSelectionChangesProgrammatically() {
        lateinit var state: PickerState<Int>

        composeRule.setContent {
            state = rememberPickerState(1)

            Picker(
                items = (1..30).toList(),
                state = state,
                visibleItemsCount = 3,
                isInfinity = false,
                pickerLabel = "Value",
                itemContentDescription = { "$it" }
            )
        }

        composeRule.runOnIdle {
            state.selectItem(30)
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
    fun picker_normalizesMissingProgrammaticSelectionToCenteredItem() {
        lateinit var state: PickerState<Int>

        composeRule.setContent {
            state = rememberPickerState(2)

            Picker(
                items = listOf(1, 2, 3),
                state = state,
                startIndex = 1,
                visibleItemsCount = 3,
                isInfinity = false,
                pickerLabel = "Value",
                itemContentDescription = { "$it" }
            )
        }

        composeRule.runOnIdle {
            state.selectItem(3)
        }

        waitUntilSelectedItem("Value: 3")

        composeRule.runOnIdle {
            state.selectItem(99)
        }

        composeRule.waitForIdle()

        composeRule
            .onNode(hasContentDescription("Value: 3") and hasStateDescription("3"))
            .assertExists()

        composeRule
            .onNode(hasContentDescription("Value: 3") and isSelected())
            .assertIsSelected()

        composeRule.runOnIdle {
            assertEquals(3, state.selectedItem)
        }
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
                hourItems = listOf(1, 2, 3),
                minuteItems = listOf(0, 30),
                periodItems = listOf(TimePeriod.AM, TimePeriod.PM),
                visibleItemsCount = 3,
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
        }

        composeRule.runOnIdle {
            state.selectTime(LocalTime(hour = 14, minute = 30))
        }

        waitUntilSelectedItem("시간: 2시")
        waitUntilSelectedItem("분: 30분")
        waitUntilSelectedItem("오전/오후: 오후")
    }

    @Test
    fun timePicker_normalizesMissingProgrammaticChildValuesIndependently() {
        lateinit var state: TimePickerState

        composeRule.setContent {
            state = rememberTimePickerState(
                initialTime = LocalTime(hour = 3, minute = 15),
                timeFormat = TimeFormat.HOUR_12
            )

            TimePicker(
                state = state,
                hourItems = listOf(1, 2, 3, 4, 5),
                minuteItems = listOf(0, 15, 45),
                periodItems = listOf(TimePeriod.AM),
                visibleItemsCount = 3,
                hourPickerLabel = "Hour",
                minutePickerLabel = "Minute",
                periodPickerLabel = "Period",
                hourItemContentDescription = { "$it" },
                minuteItemContentDescription = { "$it" },
                periodItemContentDescription = { it.name }
            )
        }

        composeRule.runOnIdle {
            state.selectTime(LocalTime(hour = 20, minute = 30))
        }

        composeRule.waitForIdle()

        composeRule
            .onNode(hasContentDescription("Hour: 3") and hasStateDescription("3"))
            .assertExists()
        composeRule
            .onNode(hasContentDescription("Minute: 15") and hasStateDescription("15"))
            .assertExists()
        composeRule
            .onNode(hasContentDescription("Period: AM") and hasStateDescription("AM"))
            .assertExists()

        waitUntilSelectedItem("Hour: 3")
        waitUntilSelectedItem("Minute: 15")
        waitUntilSelectedItem("Period: AM")

        composeRule.runOnIdle {
            assertEquals(3, state.selectedHour)
            assertEquals(15, state.selectedMinute)
            assertEquals(TimePeriod.AM, state.selectedPeriod)
        }
    }

    @Test
    fun datePicker_normalizesMissingProgrammaticChildValueIndependently() {
        lateinit var state: DatePickerState

        composeRule.setContent {
            state = rememberDatePickerState(
                initialYear = 2026,
                initialMonth = 5,
                initialDay = 20
            )

            DatePicker(
                state = state,
                yearItems = listOf(2026, 2027),
                monthItems = listOf(1, 5, 12),
                visibleItemsCount = 3,
                yearPickerLabel = "Year",
                monthPickerLabel = "Month",
                dayPickerLabel = "Day",
                yearItemContentDescription = { "$it" },
                monthItemContentDescription = { "$it" },
                dayItemContentDescription = { "$it" }
            )
        }

        composeRule.runOnIdle {
            state.selectDate(LocalDate(2027, 6, 20))
        }

        waitUntilSelectedItem("Year: 2027")
        composeRule.waitForIdle()

        composeRule
            .onNode(hasContentDescription("Year: 2027") and hasStateDescription("2027"))
            .assertExists()
        composeRule
            .onNode(hasContentDescription("Month: 5") and hasStateDescription("5"))
            .assertExists()
        composeRule
            .onNode(hasContentDescription("Day: 20") and hasStateDescription("20"))
            .assertExists()

        waitUntilSelectedItem("Month: 5")
        waitUntilSelectedItem("Day: 20")

        composeRule.runOnIdle {
            assertEquals(2027, state.selectedYear)
            assertEquals(5, state.selectedMonth)
            assertEquals(20, state.selectedDay)
        }
    }

    @Test
    fun yearMonthPicker_normalizesMissingProgrammaticChildValueIndependently() {
        lateinit var state: YearMonthPickerState

        composeRule.setContent {
            state = rememberYearMonthPickerState(
                initialYear = 2026,
                initialMonth = 5
            )

            YearMonthPicker(
                state = state,
                yearItems = listOf(2026, 2027),
                monthItems = listOf(1, 5, 12),
                visibleItemsCount = 3,
                yearPickerLabel = "Year",
                monthPickerLabel = "Month",
                yearItemContentDescription = { "$it" },
                monthItemContentDescription = { "$it" }
            )
        }

        composeRule.runOnIdle {
            state.selectYearMonth(year = 2027, month = 6)
        }

        waitUntilSelectedItem("Year: 2027")
        composeRule.waitForIdle()

        composeRule
            .onNode(hasContentDescription("Year: 2027") and hasStateDescription("2027"))
            .assertExists()
        composeRule
            .onNode(hasContentDescription("Month: 5") and hasStateDescription("5"))
            .assertExists()

        waitUntilSelectedItem("Month: 5")

        composeRule.runOnIdle {
            assertEquals(2027, state.selectedYear)
            assertEquals(5, state.selectedMonth)
        }
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
                yearItems = listOf(2026, 2027),
                monthItems = listOf(1, 5),
                visibleItemsCount = 3,
                yearPickerLabel = "연도",
                monthPickerLabel = "월",
                dayPickerLabel = "일",
                yearItemContentDescription = { "${it}년" },
                monthItemContentDescription = { "${it}월" },
                dayItemContentDescription = { "${it}일" }
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
            val state = rememberPickerState(2)

            Picker(
                items = listOf(1, 2, 3),
                state = state,
                startIndex = 1,
                isInfinity = false,
                pickerLabel = "   ",
                itemContentDescription = { "$it item" }
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
                hourItems = listOf(10, 11),
                minuteItems = listOf(5, 10),
                visibleItemsCount = 3,
                hourPickerLabel = "시간",
                minutePickerLabel = "분",
                hourItemContentDescription = { "${it}시" },
                minuteItemContentDescription = { "${it}분" }
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
    fun timePicker_forwardsCustomPeriodAccessibilityDescriptionIn12HourMode() {
        composeRule.setContent {
            val state = rememberTimePickerState(
                initialTime = LocalTime(hour = 22, minute = 5),
                timeFormat = TimeFormat.HOUR_12
            )

            TimePicker(
                state = state,
                hourItems = listOf(10, 11),
                minuteItems = listOf(5, 10),
                periodItems = listOf(TimePeriod.AM, TimePeriod.PM),
                visibleItemsCount = 3,
                periodPickerLabel = "오전/오후",
                periodItemContentDescription = {
                    when (it) {
                        TimePeriod.AM -> "오전"
                        TimePeriod.PM -> "오후"
                    }
                }
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
                yearItems = listOf(2026),
                monthItems = listOf(5),
                visibleItemsCount = 3,
                yearPickerLabel = "연도",
                monthPickerLabel = "월",
                dayPickerLabel = "일",
                yearItemContentDescription = { "${it}년" },
                monthItemContentDescription = { "${it}월" },
                dayItemContentDescription = { "${it}일" }
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
    fun yearMonthPicker_forwardsCustomAccessibilityDescriptionsToChildPickers() {
        composeRule.setContent {
            val state = rememberYearMonthPickerState(
                initialYear = 2026,
                initialMonth = 5
            )

            YearMonthPicker(
                state = state,
                yearItems = listOf(2026),
                monthItems = listOf(5),
                visibleItemsCount = 3,
                yearPickerLabel = "연도",
                monthPickerLabel = "월",
                yearItemContentDescription = { "${it}년" },
                monthItemContentDescription = { "${it}월" }
            )
        }

        composeRule
            .onNode(hasContentDescription("연도: 2026년") and hasStateDescription("2026년"))
            .assertExists()

        composeRule
            .onNode(hasContentDescription("월: 5월") and hasStateDescription("5월"))
            .assertExists()
    }

    private fun waitUntilSelectedItem(contentDescription: String) {
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule
                .onAllNodes(hasContentDescription(contentDescription) and isSelected())
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
    }
}

private fun hasStateDescription(value: String): SemanticsMatcher =
    SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, value)
