package com.kez.picker

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.isSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.kez.picker.date.DatePicker
import com.kez.picker.date.DateRangePicker
import com.kez.picker.date.DateRangePickerState
import com.kez.picker.date.rememberDatePickerState
import com.kez.picker.date.rememberDateRangePickerState
import com.kez.picker.date.rememberYearMonthPickerState
import com.kez.picker.date.YearMonthPicker
import com.kez.picker.time.TimePicker
import com.kez.picker.time.rememberTimePickerState
import com.kez.picker.util.TimeFormat
import com.kez.picker.util.TimePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class PickerFormatRobolectricTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun picker_usesVisibleTextAsDefaultAccessibilityValueDescription() {
        composeRule.setContent {
            var selectedItem by remember { mutableStateOf(2) }

            Picker(
                items = listOf(1, 2, 3),
                selectedItem = selectedItem,
                onSelectedItemChange = { selectedItem = it },
                style = PickerDefaults.style(visibleItemsCount = 3),
                isInfinity = false,
                format = PickerDefaults.itemFormat(
                    itemText = { "No. $it" }
                ),
                semantics = PickerDefaults.semantics(
                    pickerLabel = "Number"
                )
            )
        }

        composeRule
            .onNode(hasContentDescription("Number: No. 2") and isSelected())
            .assertExists()
    }

    @Test
    fun picker_usesCustomVisibleTextSeparatelyFromAccessibilityValueDescription() {
        composeRule.setContent {
            var selectedItem by remember { mutableStateOf(2) }

            Picker(
                items = listOf(1, 2, 3),
                selectedItem = selectedItem,
                onSelectedItemChange = { selectedItem = it },
                style = PickerDefaults.style(visibleItemsCount = 3),
                isInfinity = false,
                format = PickerDefaults.itemFormat(
                    itemText = { "No. $it" },
                    itemContentDescription = { "value $it" }
                ),
                semantics = PickerDefaults.semantics(
                    pickerLabel = "Number"
                )
            )
        }

        composeRule.onNodeWithText("No. 2").assertExists()
        composeRule
            .onNode(hasContentDescription("Number: value 2") and isSelected())
            .assertExists()
    }

    @Test
    fun timePicker_forwardsCustomDisplayTextToChildPickers() {
        composeRule.setContent {
            val items = PickerDefaults.timePickerItems(
                hour24Items = listOf(9),
                minuteItems = listOf(5)
            )
            val state = rememberTimePickerState(
                items = items,
                initialTime = LocalTime(hour = 9, minute = 5),
                timeFormat = TimeFormat.HOUR_24
            )

            TimePicker(
                state = state,
                items = items,
                style = PickerDefaults.style(visibleItemsCount = 1),
                format = PickerDefaults.timePickerFormat(
                    hourItemText = { it.toString().padStart(length = 2, padChar = '0') },
                    minuteItemText = { it.toString().padStart(length = 2, padChar = '0') }
                )
            )
        }

        composeRule.onNodeWithText("09").assertExists()
        composeRule.onNodeWithText("05").assertExists()
    }

    @Test
    fun timePicker_handlesColumnOrderWhenPeriodIsRenderedLast() {
        composeRule.setContent {
            val items = PickerDefaults.timePickerItems(
                minuteItems = listOf(5),
                hour12Items = listOf(10),
                periodItems = listOf(TimePeriod.AM)
            )
            val state = rememberTimePickerState(
                items = items,
                initialTime = LocalTime(hour = 10, minute = 5),
                timeFormat = TimeFormat.HOUR_12
            )

            TimePicker(
                state = state,
                items = items,
                style = PickerDefaults.style(visibleItemsCount = 1),
                layout = PickerDefaults.timePickerLayout(
                    columnOrder = listOf(
                        TimePickerColumn.HOUR,
                        TimePickerColumn.MINUTE,
                        TimePickerColumn.PERIOD
                    )
                )
            )
        }

        composeRule.onNodeWithText("10").assertExists()
        composeRule.onNodeWithText("5").assertExists()
        composeRule.onNodeWithText("AM").assertExists()
    }

    @Test
    fun datePicker_forwardsCustomDisplayTextToChildPickers() {
        composeRule.setContent {
            val items = PickerDefaults.datePickerItems(
                yearItems = listOf(2026),
                monthItems = listOf(5),
                dayItems = listOf(20)
            )
            val state = rememberDatePickerState(
                items = items,
                initialDate = LocalDate(year = 2026, month = 5, day = 20)
            )

            DatePicker(
                state = state,
                items = items,
                style = PickerDefaults.style(visibleItemsCount = 1),
                format = PickerDefaults.datePickerFormat(
                    yearItemText = { "${it}년" },
                    monthItemText = { "${it}월" },
                    dayItemText = { "${it}일" }
                )
            )
        }

        composeRule.onNodeWithText("2026년").assertExists()
        composeRule.onNodeWithText("5월").assertExists()
        composeRule.onNodeWithText("20일").assertExists()
    }

    @Test
    fun datePicker_handlesColumnOrderWhenYearIsRenderedLast() {
        composeRule.setContent {
            val items = PickerDefaults.datePickerItems(
                yearItems = listOf(2026),
                monthItems = listOf(5),
                dayItems = listOf(20)
            )
            val state = rememberDatePickerState(
                items = items,
                initialDate = LocalDate(year = 2026, month = 5, day = 20)
            )

            DatePicker(
                state = state,
                items = items,
                style = PickerDefaults.style(visibleItemsCount = 1),
                layout = PickerDefaults.datePickerLayout(
                    columnOrder = listOf(
                        DatePickerColumn.MONTH,
                        DatePickerColumn.DAY,
                        DatePickerColumn.YEAR
                    )
                )
            )
        }

        composeRule.onNodeWithText("2026").assertExists()
        composeRule.onNodeWithText("5").assertExists()
        composeRule.onNodeWithText("20").assertExists()
    }

    @Test
    fun dateRangePicker_startSelectionChangeKeepsEndPickerSelectionRendered() {
        lateinit var state: DateRangePickerState
        val items = PickerDefaults.datePickerItems(
            yearItems = listOf(2026),
            monthItems = listOf(5),
            dayItems = listOf(24, 25, 26)
        )

        composeRule.setContent {
            state = rememberDateRangePickerState(
                items = items,
                initialStartDate = LocalDate(year = 2026, month = 5, day = 24),
                initialEndDate = LocalDate(year = 2026, month = 5, day = 26)
            )

            DateRangePicker(
                state = state,
                items = items,
                style = PickerDefaults.style(visibleItemsCount = 3),
                startLabel = null,
                endLabel = null,
                format = PickerDefaults.datePickerFormat(
                    dayItemContentDescription = { "$it day" }
                ),
                semantics = PickerDefaults.dateRangePickerSemantics(
                    start = PickerDefaults.datePickerSemantics(
                        yearPickerLabel = "Start year",
                        monthPickerLabel = "Start month",
                        dayPickerLabel = "Start day"
                    ),
                    end = PickerDefaults.datePickerSemantics(
                        yearPickerLabel = "End year",
                        monthPickerLabel = "End month",
                        dayPickerLabel = "End day"
                    )
                )
            )
        }

        composeRule.runOnIdle {
            state.selectStartDate(LocalDate(year = 2026, month = 5, day = 25), items)
        }
        composeRule.waitForIdle()

        composeRule
            .onNode(hasContentDescription("Start day: 25 day") and isSelected())
            .assertExists()
        composeRule
            .onNode(hasContentDescription("End day: 26 day") and isSelected())
            .assertExists()
    }

    @Test
    fun yearMonthPicker_forwardsCustomDisplayTextToChildPickers() {
        composeRule.setContent {
            val items = PickerDefaults.yearMonthPickerItems(
                yearItems = listOf(2026),
                monthItems = listOf(5)
            )
            val state = rememberYearMonthPickerState(
                items = items,
                initialDate = LocalDate(year = 2026, month = 5, day = 1)
            )

            YearMonthPicker(
                state = state,
                items = items,
                style = PickerDefaults.style(visibleItemsCount = 1),
                format = PickerDefaults.yearMonthPickerFormat(
                    yearItemText = { "${it}년" },
                    monthItemText = { "May" }
                )
            )
        }

        composeRule.onNodeWithText("2026년").assertExists()
        composeRule.onNodeWithText("May").assertExists()
    }

    @Test
    fun yearMonthPicker_handlesColumnOrderWhenYearIsRenderedLast() {
        composeRule.setContent {
            val items = PickerDefaults.yearMonthPickerItems(
                yearItems = listOf(2026),
                monthItems = listOf(12)
            )
            val state = rememberYearMonthPickerState(
                items = items,
                initialYear = 2026,
                initialMonth = 12
            )

            YearMonthPicker(
                state = state,
                items = items,
                style = PickerDefaults.style(visibleItemsCount = 1),
                layout = PickerDefaults.yearMonthPickerLayout(
                    columnOrder = listOf(
                        YearMonthPickerColumn.MONTH,
                        YearMonthPickerColumn.YEAR
                    )
                )
            )
        }

        composeRule.onNodeWithText("2026").assertExists()
        composeRule.onNodeWithText("12").assertExists()
    }
}
