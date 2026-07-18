package com.kez.picker.duration

import androidx.compose.runtime.saveable.SaverScope
import com.kez.picker.DurationPickerColumn
import com.kez.picker.DurationPickerConstraints
import com.kez.picker.DurationPickerItems
import com.kez.picker.DurationPickerLayout
import com.kez.picker.MINUTES_PER_HOUR
import com.kez.picker.PickerDefaults
import kotlinx.collections.immutable.persistentListOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds

class DurationPickerStateTest {

    @Test
    fun constraints_requireFiniteNonNegativeWholeMinuteOrderedBounds() {
        assertFailsWith<IllegalArgumentException> {
            DurationPickerConstraints(minDuration = (-1).minutes)
        }
        assertFailsWith<IllegalArgumentException> {
            DurationPickerConstraints(maxDuration = Duration.INFINITE)
        }
        assertFailsWith<IllegalArgumentException> {
            DurationPickerConstraints(minDuration = 30.seconds)
        }
        assertFailsWith<IllegalArgumentException> {
            DurationPickerConstraints(minDuration = 500.milliseconds)
        }
        assertFailsWith<IllegalArgumentException> {
            DurationPickerConstraints(minDuration = 60_500.milliseconds)
        }
        assertFailsWith<IllegalArgumentException> {
            DurationPickerConstraints(minDuration = 1.minutes + 1.nanoseconds)
        }
        assertFailsWith<IllegalArgumentException> {
            DurationPickerConstraints(minDuration = 2.hours, maxDuration = 1.hours)
        }

        val unconstrained = DurationPickerConstraints()
        assertFalse(unconstrained.contains((-1).minutes))
        assertFalse(unconstrained.contains(500.milliseconds))
        assertFalse(unconstrained.contains(Duration.INFINITE))
        assertTrue(unconstrained.contains(Duration.ZERO))
    }

    @Test
    fun items_containsOnlyConfiguredCombinationsInsideInclusiveBounds() {
        val items = PickerDefaults.durationPickerItems(
            hourItems = listOf(0, 1),
            minuteItems = (0..55 step 5).toList(),
            minDuration = 15.minutes,
            maxDuration = 90.minutes
        )

        assertTrue(items.contains(15.minutes))
        assertTrue(items.contains(90.minutes))
        assertFalse(items.contains(10.minutes))
        assertFalse(items.contains(95.minutes))
        assertFalse(items.contains(16.minutes))
        assertFalse(items.contains(500.milliseconds))
        assertFalse(items.contains((-5).minutes))
        assertFalse(items.contains(Duration.INFINITE))
    }

    @Test
    fun coerceDuration_usesScalarDistanceAndPrefersSmallerValueOnTie() {
        val items = DurationPickerItems(
            hourItems = listOf(0, 1),
            minuteItems = listOf(10, 20)
        )

        assertEquals(10.minutes, items.coerceDuration(15.minutes))
        assertEquals(70.minutes, items.coerceDuration(75.minutes))
        assertEquals(80.minutes, items.coerceDuration(3.hours))
    }

    @Test
    fun coerceDuration_isIndependentOfSparseUnsortedSourceOrder() {
        val items = DurationPickerItems(
            hourItems = listOf(2, 0, 1),
            minuteItems = listOf(50, 10)
        )

        assertEquals(10.minutes, items.coerceDuration(30.minutes))
        assertEquals(70.minutes, items.coerceDuration(90.minutes))
    }

    @Test
    fun coerceDuration_rejectsInvalidDomainValuesAndUnsatisfiableSources() {
        val validItems = DurationPickerItems(
            hourItems = listOf(0),
            minuteItems = listOf(0)
        )
        val unsatisfiableItems = DurationPickerItems(
            hourItems = listOf(0),
            minuteItems = listOf(0),
            constraints = DurationPickerConstraints(minDuration = 1.hours)
        )

        assertFailsWith<IllegalArgumentException> {
            validItems.coerceDuration((-1).minutes)
        }
        assertFailsWith<IllegalArgumentException> {
            validItems.coerceDuration(30.seconds)
        }
        assertFailsWith<IllegalArgumentException> {
            validItems.coerceDuration(500.milliseconds)
        }
        assertFailsWith<IllegalArgumentException> {
            unsatisfiableItems.coerceDuration(Duration.ZERO)
        }
    }

    @Test
    fun invalidItemSources_failWithExplicitConfigurationErrors() {
        val invalidSources = listOf(
            DurationPickerItems(hourItems = emptyList(), minuteItems = listOf(0)),
            DurationPickerItems(hourItems = listOf(0, 0), minuteItems = listOf(0)),
            DurationPickerItems(hourItems = listOf(-1, 0), minuteItems = listOf(0)),
            DurationPickerItems(hourItems = listOf(0), minuteItems = emptyList()),
            DurationPickerItems(hourItems = listOf(0), minuteItems = listOf(0, 0)),
            DurationPickerItems(hourItems = listOf(0), minuteItems = listOf(60))
        )

        invalidSources.forEach { items ->
            assertFailsWith<IllegalArgumentException> {
                items.coerceDuration(Duration.ZERO)
            }
        }
    }

    @Test
    fun hourChange_repairsMinuteWithinTheRequestedHourAndScalarBound() {
        val items = PickerDefaults.durationPickerItems(
            hourItems = listOf(0, 1),
            minuteItems = (0..55 step 5).toList(),
            maxDuration = 90.minutes
        )

        val repaired = items.repairedDurationAfter(
            currentDuration = 45.minutes,
            column = DurationPickerColumn.HOUR,
            value = 1
        )

        assertEquals(90.minutes, repaired)
        assertTrue(items.contains(repaired))
    }

    @Test
    fun minuteChange_commitsOneSelectableScalarValue() {
        val items = PickerDefaults.durationPickerItems(
            hourItems = listOf(0, 1),
            minuteItems = (0..55 step 5).toList(),
            maxDuration = 90.minutes
        )

        val repaired = items.repairedDurationAfter(
            currentDuration = 1.hours,
            column = DurationPickerColumn.MINUTE,
            value = 25
        )

        assertEquals(85.minutes, repaired)
        assertTrue(items.contains(repaired))
    }

    @Test
    fun lateUnavailableColumnValues_keepCurrentLogicalDuration() {
        val replacementItems = PickerDefaults.durationPickerItems(
            hourItems = listOf(0, 1),
            minuteItems = listOf(0, 30),
            maxDuration = 60.minutes
        )
        val current = 60.minutes

        val lateHour = replacementItems.repairedDurationAfter(
            currentDuration = current,
            column = DurationPickerColumn.HOUR,
            value = 2
        )
        val lateMinute = replacementItems.repairedDurationAfter(
            currentDuration = current,
            column = DurationPickerColumn.MINUTE,
            value = 30
        )

        assertEquals(current, lateHour)
        assertEquals(current, lateMinute)
    }

    @Test
    fun state_ownsOneScalarValueAndProgrammaticSelectionCanUseItems() {
        val state = DurationPickerState(initialHours = 1, initialMinutes = 5)
        val items = PickerDefaults.durationPickerItems(
            hourItems = listOf(0, 1),
            minuteItems = listOf(0, 30),
            maxDuration = 90.minutes
        )

        assertEquals(65.minutes, state.selectedDuration)
        assertEquals(1, state.selectedHours)
        assertEquals(5, state.selectedMinutes)

        state.selectDuration(100.minutes, items)

        assertEquals(90.minutes, state.selectedDuration)
        assertEquals(1, state.selectedHours)
        assertEquals(30, state.selectedMinutes)
    }

    @Test
    fun state_rejectsValuesOutsideTheHourMinuteDomain() {
        assertFailsWith<IllegalArgumentException> {
            DurationPickerState(initialDuration = (-1).minutes)
        }
        assertFailsWith<IllegalArgumentException> {
            DurationPickerState(initialDuration = 30.seconds)
        }
        assertFailsWith<IllegalArgumentException> {
            DurationPickerState(initialDuration = 500.milliseconds)
        }
        assertFailsWith<IllegalArgumentException> {
            DurationPickerState(initialHours = -1, initialMinutes = 0)
        }
        assertFailsWith<IllegalArgumentException> {
            DurationPickerState(initialHours = 0, initialMinutes = 60)
        }
    }

    @Test
    fun state_andSaver_preserveLargestSupportedElapsedHour() {
        val largestSupported =
            (Int.MAX_VALUE.toLong() * MINUTES_PER_HOUR + 59L).minutes
        val state = DurationPickerState(initialDuration = largestSupported)

        assertEquals(Int.MAX_VALUE, state.selectedHours)
        assertEquals(59, state.selectedMinutes)
        assertEquals(largestSupported, state.saveAndRestore().selectedDuration)

        val firstUnsupported =
            ((Int.MAX_VALUE.toLong() + 1L) * MINUTES_PER_HOUR).minutes
        assertFailsWith<IllegalArgumentException> {
            DurationPickerState(initialDuration = firstUnsupported)
        }
    }

    @Test
    fun layout_requiresEveryColumnExactlyOnce() {
        assertFailsWith<IllegalArgumentException> {
            DurationPickerLayout(
                hourWeight = 1f,
                minuteWeight = 1f,
                columnOrder = persistentListOf(
                    DurationPickerColumn.HOUR,
                    DurationPickerColumn.HOUR
                )
            )
        }
    }

    private fun DurationPickerState.saveAndRestore(): DurationPickerState {
        val saved = with(DurationPickerState.Saver) {
            SaverScope { true }.save(this@saveAndRestore)
        }
        return DurationPickerState.Saver.restore(saved!!)!!
    }
}
