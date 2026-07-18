package com.kez.picker

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.isSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import com.kez.picker.date.DatePicker
import com.kez.picker.date.DatePickerState
import com.kez.picker.date.rememberDatePickerState
import com.kez.picker.time.TimePicker
import com.kez.picker.time.TimePickerState
import com.kez.picker.time.rememberTimePickerState
import com.kez.picker.util.TimeFormat
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class MultiWheelSelectionContractRobolectricTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun datePicker_nonDefaultOrderCommitsOneRepairedLogicalDate() {
        lateinit var state: DatePickerState
        val committedDates = mutableListOf<LocalDate>()
        val items = PickerDefaults.datePickerItems(
            yearItems = listOf(2025),
            monthItems = listOf(1, 2),
            dayItems = listOf(15, 31)
        )

        composeRule.setContent {
            state = rememberDatePickerState(
                items = items,
                initialDate = LocalDate(2025, 1, 31)
            )
            DatePicker(
                state = state,
                items = items,
                onSelectedDateChange = { date ->
                    assertEquals(date, state.selectedDate)
                    committedDates += date
                },
                layout = PickerDefaults.datePickerLayout(
                    columnOrder = listOf(
                        DatePickerColumn.DAY,
                        DatePickerColumn.MONTH,
                        DatePickerColumn.YEAR
                    )
                ),
                style = PickerDefaults.style(visibleItemsCount = 3),
                format = PickerDefaults.datePickerFormat(
                    monthItemContentDescription = { "$it month" },
                    dayItemContentDescription = { "$it day" }
                ),
                semantics = PickerDefaults.datePickerSemantics(
                    monthPickerLabel = "Month",
                    dayPickerLabel = "Day"
                )
            )
        }

        composeRule.onAllNodes(hasContentDescription("Month: 2 month"))[0].performClick()
        waitUntilSelected("Month: 2 month")
        waitUntilSelected("Day: 15 day")

        composeRule.runOnIdle {
            assertEquals(LocalDate(2025, 2, 15), state.selectedDate)
            assertEquals(listOf(LocalDate(2025, 2, 15)), committedDates)
            assertTrue(items.contains(state.selectedDate))
        }
    }

    @Test
    fun timePicker_nonDefaultOrderCommitsOneRepairedLogicalTime() {
        lateinit var state: TimePickerState
        val committedTimes = mutableListOf<LocalTime>()
        val items = PickerDefaults.timePickerItems(
            minuteItems = listOf(0, 30),
            hour24Items = listOf(10, 11),
            minTime = LocalTime(10, 30),
            maxTime = LocalTime(11, 0)
        )

        composeRule.setContent {
            state = rememberTimePickerState(
                items = items,
                initialTime = LocalTime(10, 30),
                timeFormat = TimeFormat.HOUR_24
            )
            TimePicker(
                state = state,
                items = items,
                onSelectedTimeChange = { time ->
                    assertEquals(time, state.selectedTime)
                    committedTimes += time
                },
                layout = PickerDefaults.timePickerLayout(
                    columnOrder = listOf(
                        TimePickerColumn.MINUTE,
                        TimePickerColumn.HOUR,
                        TimePickerColumn.PERIOD
                    )
                ),
                style = PickerDefaults.style(visibleItemsCount = 3),
                format = PickerDefaults.timePickerFormat(
                    hourItemContentDescription = { "$it hour" },
                    minuteItemContentDescription = { "$it minute" }
                ),
                semantics = PickerDefaults.timePickerSemantics(
                    hourPickerLabel = "Hour",
                    minutePickerLabel = "Minute"
                )
            )
        }

        composeRule.onAllNodes(hasContentDescription("Hour: 11 hour"))[0].performClick()
        waitUntilSelected("Hour: 11 hour")
        waitUntilSelected("Minute: 0 minute")

        composeRule.runOnIdle {
            assertEquals(LocalTime(11, 0), state.selectedTime)
            assertEquals(listOf(LocalTime(11, 0)), committedTimes)
            assertTrue(items.contains(state.selectedTime, TimeFormat.HOUR_24))
        }
    }

    @Test
    fun datePicker_programmaticSelectionSynchronizesChildrenWithoutUserCallback() {
        lateinit var state: DatePickerState
        val committedDates = mutableListOf<LocalDate>()
        val items = PickerDefaults.datePickerItems(
            yearItems = listOf(2025, 2026),
            monthItems = listOf(1, 2),
            dayItems = listOf(15, 28, 31)
        )

        composeRule.setContent {
            state = rememberDatePickerState(
                items = items,
                initialDate = LocalDate(2025, 1, 31)
            )
            DatePicker(
                state = state,
                items = items,
                onSelectedDateChange = committedDates::add,
                style = PickerDefaults.style(visibleItemsCount = 3),
                format = PickerDefaults.datePickerFormat(
                    yearItemContentDescription = { "$it year" },
                    monthItemContentDescription = { "$it month" },
                    dayItemContentDescription = { "$it day" }
                ),
                semantics = PickerDefaults.datePickerSemantics(
                    yearPickerLabel = "Year",
                    monthPickerLabel = "Month",
                    dayPickerLabel = "Day"
                )
            )
        }

        composeRule.runOnIdle {
            state.selectDate(LocalDate(2026, 2, 28), items)
        }
        waitUntilSelected("Year: 2026 year")
        waitUntilSelected("Month: 2 month")
        waitUntilSelected("Day: 28 day")

        composeRule.runOnIdle {
            assertEquals(LocalDate(2026, 2, 28), state.selectedDate)
            assertEquals(emptyList<LocalDate>(), committedDates)
        }
    }

    @Test
    fun datePicker_compatibleItemSourceReplacementDispatchesNoUserCallback() {
        lateinit var itemsState: MutableState<DatePickerItems>
        lateinit var state: DatePickerState
        val committedDates = mutableListOf<LocalDate>()

        composeRule.setContent {
            itemsState = remember {
                mutableStateOf(
                    PickerDefaults.datePickerItems(
                        yearItems = listOf(2025, 2026),
                        monthItems = listOf(1, 2),
                        dayItems = listOf(15, 31)
                    )
                )
            }
            state = rememberDatePickerState(
                items = itemsState.value,
                initialDate = LocalDate(2025, 1, 15)
            )
            DatePicker(
                state = state,
                items = itemsState.value,
                onSelectedDateChange = committedDates::add,
                style = PickerDefaults.style(visibleItemsCount = 3),
                format = PickerDefaults.datePickerFormat(
                    yearItemContentDescription = { "$it year" }
                ),
                semantics = PickerDefaults.datePickerSemantics(yearPickerLabel = "Year")
            )
        }

        composeRule.runOnIdle {
            itemsState.value = PickerDefaults.datePickerItems(
                yearItems = listOf(2025, 2027),
                monthItems = listOf(1, 2),
                dayItems = listOf(15, 31)
            )
        }
        waitUntilSelected("Year: 2025 year")

        composeRule.runOnIdle {
            assertEquals(LocalDate(2025, 1, 15), state.selectedDate)
            assertEquals(emptyList<LocalDate>(), committedDates)
        }
    }

    @Test
    fun datePicker_itemSourceReplacementCancelsAnInFlightStillSelectableTarget() {
        lateinit var itemsState: MutableState<DatePickerItems>
        lateinit var state: DatePickerState
        val committedDates = mutableListOf<LocalDate>()
        val originalItems = PickerDefaults.datePickerItems(
            yearItems = listOf(2025),
            monthItems = listOf(1, 2),
            dayItems = (1..31).toList()
        )
        val replacementItems = PickerDefaults.datePickerItems(
            yearItems = listOf(2025, 2026),
            monthItems = listOf(1, 2),
            dayItems = (1..31).toList()
        )

        composeRule.setContent {
            itemsState = remember { mutableStateOf(originalItems) }
            state = rememberDatePickerState(
                items = itemsState.value,
                initialDate = LocalDate(2025, 1, 31)
            )
            DatePicker(
                state = state,
                items = itemsState.value,
                onSelectedDateChange = committedDates::add,
                style = PickerDefaults.style(visibleItemsCount = 3),
                format = PickerDefaults.datePickerFormat(
                    monthItemContentDescription = { "$it month" }
                ),
                semantics = PickerDefaults.datePickerSemantics(monthPickerLabel = "Month")
            )
        }

        composeRule.mainClock.autoAdvance = false
        val monthTargetTopsBefore = nodeTops("Month: 2 month")
        composeRule.onAllNodes(hasContentDescription("Month: 2 month"))[0].performClick()
        composeRule.mainClock.advanceTimeBy(100)
        val monthTargetTopsDuring = nodeTops("Month: 2 month")
        assertTrue(
            "The source must be replaced after the previous child animation has started.",
            monthTargetTopsDuring != monthTargetTopsBefore
        )
        composeRule.runOnIdle {
            itemsState.value = replacementItems
        }
        composeRule.mainClock.advanceTimeBy(5_000)
        composeRule.mainClock.autoAdvance = true
        composeRule.waitForIdle()

        waitUntilSelected("Month: 1 month")
        composeRule.runOnIdle {
            assertEquals(LocalDate(2025, 1, 31), state.selectedDate)
            assertEquals(emptyList<LocalDate>(), committedDates)
        }
    }

    @Test
    fun datePicker_upstreamSettleCancelsInvalidatedDependentAnimationWithoutCallback() {
        lateinit var state: DatePickerState
        val committedDates = mutableListOf<LocalDate>()
        val items = PickerDefaults.datePickerItems(
            yearItems = listOf(2025),
            monthItems = listOf(1, 2),
            dayItems = (1..31).toList()
        )

        composeRule.setContent {
            state = rememberDatePickerState(
                items = items,
                initialDate = LocalDate(2025, 1, 31)
            )
            DatePicker(
                state = state,
                items = items,
                onSelectedDateChange = committedDates::add,
                style = PickerDefaults.style(visibleItemsCount = 3),
                format = PickerDefaults.datePickerFormat(
                    monthItemContentDescription = { "$it month" },
                    dayItemContentDescription = { "$it day" }
                ),
                semantics = PickerDefaults.datePickerSemantics(
                    monthPickerLabel = "Month",
                    dayPickerLabel = "Day"
                )
            )
        }

        composeRule.mainClock.autoAdvance = false
        composeRule.onAllNodes(hasContentDescription("Month: 2 month"))[0].performClick()
        composeRule.mainClock.advanceTimeBy(16)
        val dayTargetTopsBefore = nodeTops("Day: 30 day")
        composeRule.onAllNodes(hasContentDescription("Day: 30 day"))[0].performClick()
        composeRule.mainClock.advanceTimeBy(100)
        val dayTargetTopsDuring = nodeTops("Day: 30 day")
        assertTrue(
            "The dependent child animation must start before the upstream repair invalidates it.",
            dayTargetTopsDuring != dayTargetTopsBefore
        )
        composeRule.mainClock.advanceTimeBy(5_000)
        composeRule.mainClock.autoAdvance = true
        composeRule.waitForIdle()

        waitUntilSelected("Month: 2 month")
        waitUntilSelected("Day: 28 day")

        composeRule.runOnIdle {
            val repairedDate = LocalDate(2025, 2, 28)
            assertEquals(repairedDate, state.selectedDate)
            assertEquals(listOf(repairedDate), committedDates)
            assertTrue(items.contains(state.selectedDate))
        }
    }

    private fun waitUntilSelected(contentDescription: String) {
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule
                .onAllNodes(hasContentDescription(contentDescription) and isSelected())
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
    }

    private fun nodeTops(contentDescription: String): List<Float> =
        composeRule
            .onAllNodes(hasContentDescription(contentDescription))
            .fetchSemanticsNodes()
            .map { it.boundsInRoot.top }
}
