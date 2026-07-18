package com.kez.picker

import androidx.compose.runtime.snapshots.Snapshot
import com.kez.picker.date.DatePickerState
import com.kez.picker.date.repairedDateAfter
import com.kez.picker.time.TimePickerState
import com.kez.picker.time.repairedTimeAfterHour
import com.kez.picker.time.repairedTimeAfterMinute
import com.kez.picker.time.repairedTimeAfterPeriod
import com.kez.picker.util.TimeFormat
import com.kez.picker.util.TimePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class MultiWheelSelectionContractTest {

    @Test
    fun commitMultiWheelSelection_commitsBeforeDispatchingExactlyOneCallback() {
        var committedState = "before"
        val callbackStates = mutableListOf<String>()

        val changed = commitMultiWheelSelection(
            currentState = "before",
            nextState = "after",
            commitState = { committedState = it },
            onSelectionCommitted = { callbackValue ->
                assertEquals(callbackValue, committedState)
                callbackStates += callbackValue
            }
        )

        assertTrue(changed)
        assertEquals("after", committedState)
        assertEquals(listOf("after"), callbackStates)
    }

    @Test
    fun commitMultiWheelSelection_ignoresUnchangedLogicalValue() {
        var commitCount = 0
        var callbackCount = 0

        val changed = commitMultiWheelSelection(
            currentState = 7,
            nextState = 7,
            commitState = { commitCount++ },
            onSelectionCommitted = { callbackCount++ }
        )

        assertEquals(false, changed)
        assertEquals(0, commitCount)
        assertEquals(0, callbackCount)
    }

    @Test
    fun dateColumnChange_repairsDependentDayAsOneSelectableDate() {
        val items = DatePickerItems(
            yearItems = listOf(2025),
            monthItems = listOf(1, 2),
            dayItems = listOf(15, 31)
        )

        val nextDate = items.repairedDateAfter(
            currentDate = LocalDate(2025, 1, 31),
            column = DatePickerColumn.MONTH,
            value = 2
        )

        assertEquals(LocalDate(2025, 2, 15), nextDate)
        assertTrue(items.contains(nextDate))
    }

    @Test
    fun dateMonthRepair_doesNotTraverseTheLargeYearSource() {
        val items = DatePickerItems(
            yearItems = FailOnReadIntList(size = 9_000),
            monthItems = listOf(1, 2),
            dayItems = listOf(15, 31)
        )

        val nextDate = items.repairedDateAfter(
            currentDate = LocalDate(2025, 1, 31),
            column = DatePickerColumn.MONTH,
            value = 2
        )

        assertEquals(LocalDate(2025, 2, 15), nextDate)
    }

    @Test
    fun dateColumnChanges_fromDifferentColumnsAlwaysReturnSelectableValues() {
        val items = DatePickerItems(
            yearItems = listOf(2024, 2025),
            monthItems = listOf(1, 2),
            dayItems = listOf(15, 29, 31),
            constraints = DatePickerConstraints(
                minDate = LocalDate(2024, 1, 15),
                maxDate = LocalDate(2025, 2, 15)
            )
        )
        val changes = listOf(
            DatePickerColumn.MONTH to 2,
            DatePickerColumn.YEAR to 2025,
            DatePickerColumn.DAY to 31
        )

        val committedDates = changes.runningFold(LocalDate(2024, 1, 31)) { date, (column, value) ->
            items.repairedDateAfter(currentDate = date, column = column, value = value)
        }.drop(1)

        assertEquals(
            listOf(
                LocalDate(2024, 2, 29),
                LocalDate(2025, 2, 15),
                LocalDate(2025, 2, 15)
            ),
            committedDates
        )
        assertTrue(committedDates.all(items::contains))
    }

    @Test
    fun lateUnavailableDateColumnValuesKeepTheCurrentSelectableDate() {
        val replacementItems = DatePickerItems(
            yearItems = listOf(2025, 2026),
            monthItems = listOf(1, 3),
            dayItems = listOf(15, 31),
            constraints = DatePickerConstraints(
                minDate = LocalDate(2025, 1, 1),
                maxDate = LocalDate(2025, 12, 31)
            )
        )
        val currentDate = LocalDate(2025, 3, 31)

        val lateResults = listOf(
            DatePickerColumn.YEAR to 2026,
            DatePickerColumn.MONTH to 2,
            DatePickerColumn.DAY to 30
        ).map { (column, value) ->
            replacementItems.repairedDateAfter(
                currentDate = currentDate,
                column = column,
                value = value
            )
        }

        assertEquals(listOf(currentDate, currentDate, currentDate), lateResults)
        assertTrue(lateResults.all(replacementItems::contains))
    }

    @Test
    fun lateUnavailableTimeColumnValuesKeepTheCurrentSelectableTime() {
        val replacementItems = TimePickerItems(
            minuteItems = listOf(0, 30),
            hour24Items = listOf(0, 10, 12),
            hour12Items = listOf(10, 12),
            periodItems = listOf(TimePeriod.AM)
        )
        val currentTime = LocalTime(0, 0)

        val lateResults = listOf(
            replacementItems.repairedTimeAfterHour(
                currentTime = currentTime,
                timeFormat = TimeFormat.HOUR_24,
                hour = 11
            ),
            replacementItems.repairedTimeAfterMinute(
                currentTime = currentTime,
                timeFormat = TimeFormat.HOUR_24,
                minute = 15
            ),
            replacementItems.repairedTimeAfterPeriod(
                currentTime = currentTime,
                period = TimePeriod.PM
            )
        )

        assertEquals(listOf(currentTime, currentTime, currentTime), lateResults)
        assertTrue(lateResults.all { replacementItems.contains(it, TimeFormat.HOUR_24) })
    }

    @Test
    fun timeColumnChange_repairsDependentMinuteAsOneSelectableTime() {
        val items = TimePickerItems(
            minuteItems = listOf(0, 30),
            hour24Items = listOf(10, 11),
            hour12Items = (1..12).toList(),
            periodItems = com.kez.picker.util.TimePeriod.entries,
            constraints = TimePickerConstraints(
                minTime = LocalTime(10, 30),
                maxTime = LocalTime(11, 0)
            )
        )

        val nextTime = items.repairedTimeAfterHour(
            currentTime = LocalTime(10, 30),
            timeFormat = TimeFormat.HOUR_24,
            hour = 11
        )

        assertEquals(LocalTime(11, 0), nextTime)
        assertTrue(items.contains(nextTime, TimeFormat.HOUR_24))
    }

    @Test
    fun timePeriodChange_repairsHourAndMinuteWithinTheRequestedPeriod() {
        val items = TimePickerItems(
            minuteItems = listOf(0, 30),
            hour24Items = (0..23).toList(),
            hour12Items = listOf(11),
            periodItems = listOf(TimePeriod.AM, TimePeriod.PM)
        )

        val nextTime = items.repairedTimeAfterPeriod(
            currentTime = LocalTime(11, 30),
            period = TimePeriod.PM
        )

        assertEquals(LocalTime(23, 30), nextTime)
        assertTrue(items.contains(nextTime, TimeFormat.HOUR_12))
    }

    @Test
    fun invalidDependentSources_failDuringConfigurationValidation() {
        val invalidDateItems = DatePickerItems(
            yearItems = listOf(2025),
            monthItems = listOf(2),
            dayItems = emptyList()
        )
        val invalidTimeItems = TimePickerItems(
            minuteItems = listOf(0),
            hour24Items = listOf(10),
            hour12Items = listOf(10),
            periodItems = com.kez.picker.util.TimePeriod.entries,
            constraints = TimePickerConstraints(
                minTime = LocalTime(10, 15),
                maxTime = LocalTime(10, 15)
            )
        )

        assertFailsWith<IllegalArgumentException> {
            com.kez.picker.date.validateDatePickerItems(
                state = DatePickerState(initialDate = LocalDate(2025, 2, 1)),
                items = invalidDateItems
            )
        }
        assertFailsWith<IllegalArgumentException> {
            com.kez.picker.time.validateTimePickerItems(
                state = TimePickerState(
                    initialTime = LocalTime(10, 0),
                    timeFormat = TimeFormat.HOUR_24
                ),
                items = invalidTimeItems
            )
        }
    }

    @Test
    fun programmaticMultiFieldUpdates_applyOneSnapshotPerLogicalValue() {
        val dateState = DatePickerState(LocalDate(2024, 1, 31))
        val timeState = TimePickerState(LocalTime(10, 5))
        Snapshot.sendApplyNotifications()
        val observedLogicalValues = mutableListOf<Pair<LocalDate, LocalTime>>()
        val observer = Snapshot.registerApplyObserver { changed, _ ->
            if (changed.isNotEmpty()) {
                observedLogicalValues += dateState.selectedDate to timeState.selectedTime
            }
        }

        try {
            dateState.selectDate(LocalDate(2025, 2, 28))
            timeState.selectTime(LocalTime(23, 45))
            Snapshot.sendApplyNotifications()
        } finally {
            observer.dispose()
        }

        assertEquals(
            listOf(
                LocalDate(2025, 2, 28) to LocalTime(10, 5),
                LocalDate(2025, 2, 28) to LocalTime(23, 45)
            ),
            observedLogicalValues
        )
        assertEquals(LocalDate(2025, 2, 28), dateState.selectedDate)
        assertEquals(LocalTime(23, 45), timeState.selectedTime)
    }

    @Test
    fun programmaticMultiFieldUpdates_canRunInsideAnExistingMutableSnapshot() {
        val dateState = DatePickerState(LocalDate(2024, 1, 31))
        val timeState = TimePickerState(LocalTime(10, 5))

        Snapshot.withMutableSnapshot {
            dateState.selectDate(LocalDate(2025, 2, 28))
            timeState.selectTime(LocalTime(23, 45))
        }

        assertEquals(LocalDate(2025, 2, 28), dateState.selectedDate)
        assertEquals(LocalTime(23, 45), timeState.selectedTime)
    }

    private class FailOnReadIntList(
        override val size: Int
    ) : AbstractList<Int>() {
        override fun get(index: Int): Int =
            error("The year source must not be traversed during a month/day repair.")
    }
}
