package com.kez.picker

import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.isSelected
import com.kez.picker.date.DatePicker
import com.kez.picker.date.DatePickerState
import com.kez.picker.date.YearMonthPicker
import com.kez.picker.date.YearMonthPickerState
import com.kez.picker.date.rememberDatePickerState
import com.kez.picker.date.rememberYearMonthPickerState
import com.kez.picker.time.TimePicker
import com.kez.picker.time.TimePickerState
import com.kez.picker.time.rememberTimePickerState
import com.kez.picker.util.TimeFormat
import com.kez.picker.util.TimePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class PickerStateRestorationAndroidTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun rememberTimePickerState_restoresProgrammaticSelectionAfterSaveRestore() {
        lateinit var state: TimePickerState
        val restorationTester = StateRestorationTester(composeRule)

        restorationTester.setContent {
            state = rememberTimePickerState(
                initialTime = LocalTime(8, 15),
                timeFormat = TimeFormat.HOUR_12
            )
        }

        composeRule.runOnIdle {
            state.selectTime(LocalTime(23, 45))
        }

        restorationTester.emulateSavedInstanceStateRestore()

        composeRule.runOnIdle {
            assertEquals(LocalTime(23, 45), state.selectedTime)
            assertEquals(TimeFormat.HOUR_12, state.timeFormat)
            assertEquals(11, state.selectedHour)
            assertEquals(45, state.selectedMinute)
            assertEquals(TimePeriod.PM, state.selectedPeriod)
        }
    }

    @Test
    fun rememberTimePickerState_restoresNoonAfterSaveRestore() {
        lateinit var state: TimePickerState
        val restorationTester = StateRestorationTester(composeRule)

        restorationTester.setContent {
            state = rememberTimePickerState(
                initialTime = LocalTime(1, 0),
                timeFormat = TimeFormat.HOUR_12
            )
        }

        composeRule.runOnIdle {
            state.selectTime(LocalTime(12, 15))
            assertEquals(LocalTime(12, 15), state.selectedTime)
        }

        restorationTester.emulateSavedInstanceStateRestore()

        composeRule.runOnIdle {
            assertEquals(LocalTime(12, 15), state.selectedTime)
            assertEquals(TimeFormat.HOUR_12, state.timeFormat)
            assertEquals(12, state.selectedHour)
            assertEquals(12, state.selectedHourOfDay)
            assertEquals(15, state.selectedMinute)
            assertEquals(TimePeriod.PM, state.selectedPeriod)
        }
    }

    @Test
    fun timePicker_restoresSelectionIntoRenderedSemanticsAfterSaveRestore() {
        lateinit var state: TimePickerState
        val restorationTester = StateRestorationTester(composeRule)

        restorationTester.setContent {
            state = rememberTimePickerState(
                initialTime = LocalTime(1, 5),
                timeFormat = TimeFormat.HOUR_12
            )

            TimePicker(
                state = state,
                style = PickerDefaults.style(visibleItemsCount = 3),
                hourPickerLabel = "Hour",
                minutePickerLabel = "Minute",
                periodPickerLabel = "Period",
                hourItemContentDescription = { "$it hour" },
                minuteItemContentDescription = { "$it minute" },
                periodItemContentDescription = { "$it period" }
            )
        }

        composeRule.runOnIdle {
            state.selectTime(LocalTime(0, 30))
            assertEquals(LocalTime(0, 30), state.selectedTime)
        }

        restorationTester.emulateSavedInstanceStateRestore()
        composeRule.waitForIdle()

        composeRule
            .onNode(hasContentDescription("Hour: 12 hour") and isSelected())
            .assertExists()
        composeRule
            .onNode(hasContentDescription("Minute: 30 minute") and isSelected())
            .assertExists()
        composeRule
            .onNode(hasContentDescription("Period: AM period") and isSelected())
            .assertExists()

        composeRule.runOnIdle {
            assertEquals(LocalTime(0, 30), state.selectedTime)
            assertEquals(12, state.selectedHour)
            assertEquals(30, state.selectedMinute)
            assertEquals(TimePeriod.AM, state.selectedPeriod)
        }
    }

    @Test
    fun rememberDatePickerState_restoresProgrammaticSelectionAfterSaveRestore() {
        lateinit var state: DatePickerState
        val restorationTester = StateRestorationTester(composeRule)

        restorationTester.setContent {
            state = rememberDatePickerState(initialDate = LocalDate(2024, 1, 31))
        }

        composeRule.runOnIdle {
            state.selectDate(LocalDate(2026, 2, 28))
        }

        restorationTester.emulateSavedInstanceStateRestore()

        composeRule.runOnIdle {
            assertEquals(2026, state.selectedYear)
            assertEquals(2, state.selectedMonth)
            assertEquals(28, state.selectedDay)
            assertEquals(LocalDate(2026, 2, 28), state.selectedDate)
        }
    }

    @Test
    fun datePicker_restoresSelectionIntoRenderedSemanticsAfterSaveRestore() {
        lateinit var state: DatePickerState
        val restorationTester = StateRestorationTester(composeRule)

        restorationTester.setContent {
            state = rememberDatePickerState(initialDate = LocalDate(2024, 1, 31))

            DatePicker(
                state = state,
                yearItems = listOf(2024, 2026),
                monthItems = listOf(1, 2),
                style = PickerDefaults.style(visibleItemsCount = 3),
                yearPickerLabel = "Year",
                monthPickerLabel = "Month",
                dayPickerLabel = "Day",
                yearItemContentDescription = { "$it year" },
                monthItemContentDescription = { "$it month" },
                dayItemContentDescription = { "$it day" }
            )
        }

        composeRule.runOnIdle {
            state.selectDate(LocalDate(2026, 2, 28))
            assertEquals(LocalDate(2026, 2, 28), state.selectedDate)
        }

        restorationTester.emulateSavedInstanceStateRestore()
        composeRule.waitForIdle()

        composeRule
            .onNode(hasContentDescription("Year: 2026 year") and isSelected())
            .assertExists()
        composeRule
            .onNode(hasContentDescription("Month: 2 month") and isSelected())
            .assertExists()
        composeRule
            .onNode(hasContentDescription("Day: 28 day") and isSelected())
            .assertExists()

        composeRule.runOnIdle {
            assertEquals(LocalDate(2026, 2, 28), state.selectedDate)
        }
    }

    @Test
    fun rememberYearMonthPickerState_restoresProgrammaticSelectionAfterSaveRestore() {
        lateinit var state: YearMonthPickerState
        val restorationTester = StateRestorationTester(composeRule)

        restorationTester.setContent {
            state = rememberYearMonthPickerState(initialDate = LocalDate(2024, 1, 31))
        }

        composeRule.runOnIdle {
            state.selectYearMonth(year = 2027, month = 12)
        }

        restorationTester.emulateSavedInstanceStateRestore()

        composeRule.runOnIdle {
            assertEquals(2027, state.selectedYear)
            assertEquals(12, state.selectedMonth)
            assertEquals(LocalDate(2027, 12, 1), state.selectedMonthDate)
        }
    }

    @Test
    fun yearMonthPicker_restoresSelectionIntoRenderedSemanticsAfterSaveRestore() {
        lateinit var state: YearMonthPickerState
        val restorationTester = StateRestorationTester(composeRule)

        restorationTester.setContent {
            state = rememberYearMonthPickerState(initialDate = LocalDate(2024, 5, 20))

            YearMonthPicker(
                state = state,
                yearItems = listOf(2024, 2027),
                monthItems = listOf(5, 12),
                style = PickerDefaults.style(visibleItemsCount = 3),
                yearPickerLabel = "Year",
                monthPickerLabel = "Month",
                yearItemContentDescription = { "$it year" },
                monthItemContentDescription = { "$it month" }
            )
        }

        composeRule.runOnIdle {
            state.selectYearMonth(year = 2027, month = 12)
            assertEquals(LocalDate(2027, 12, 1), state.selectedMonthDate)
        }

        restorationTester.emulateSavedInstanceStateRestore()
        composeRule.waitForIdle()

        composeRule
            .onNode(hasContentDescription("Year: 2027 year") and isSelected())
            .assertExists()
        composeRule
            .onNode(hasContentDescription("Month: 12 month") and isSelected())
            .assertExists()

        composeRule.runOnIdle {
            assertEquals(LocalDate(2027, 12, 1), state.selectedMonthDate)
        }
    }
}
