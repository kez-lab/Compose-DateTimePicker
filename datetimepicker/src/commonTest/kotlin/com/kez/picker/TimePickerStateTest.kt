package com.kez.picker

import androidx.compose.runtime.saveable.SaverScope
import com.kez.picker.util.TimeFormat
import com.kez.picker.util.TimePeriod
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Unit tests for [TimePickerState] class.
 *
 * Tests cover:
 * - 24-hour format initialization
 * - 12-hour format initialization with AM/PM
 * - Boundary conditions for hours and minutes
 * - Period (AM/PM) handling
 */
class TimePickerStateTest {

    // ==================== 24-hour Format Tests ====================

    @Test
    fun timePickerState_24HourFormat_initialValues_areCorrect() {
        val state = TimePickerState(
            initialHour = 14,
            initialMinute = 30,
            initialPeriod = TimePeriod.PM,
            timeFormat = TimeFormat.HOUR_24
        )

        assertEquals(14, state.selectedHour)
        assertEquals(30, state.selectedMinute)
        assertEquals(TimeFormat.HOUR_24, state.timeFormat)
    }

    @Test
    fun timePickerState_24HourFormat_midnight_isCorrect() {
        val state = TimePickerState(
            initialHour = 0,
            initialMinute = 0,
            initialPeriod = TimePeriod.AM,
            timeFormat = TimeFormat.HOUR_24
        )

        assertEquals(0, state.selectedHour)
        assertEquals(0, state.selectedMinute)
    }

    @Test
    fun timePickerState_24HourFormat_noon_isCorrect() {
        val state = TimePickerState(
            initialHour = 12,
            initialMinute = 0,
            initialPeriod = TimePeriod.PM,
            timeFormat = TimeFormat.HOUR_24
        )

        assertEquals(12, state.selectedHour)
        assertEquals(0, state.selectedMinute)
    }

    @Test
    fun timePickerState_24HourFormat_endOfDay_isCorrect() {
        val state = TimePickerState(
            initialHour = 23,
            initialMinute = 59,
            initialPeriod = TimePeriod.PM,
            timeFormat = TimeFormat.HOUR_24
        )

        assertEquals(23, state.selectedHour)
        assertEquals(59, state.selectedMinute)
    }

    // ==================== 12-hour Format Tests ====================

    @Test
    fun timePickerState_12HourFormat_morning_isCorrect() {
        val state = TimePickerState(
            initialHour = 9,
            initialMinute = 30,
            initialPeriod = TimePeriod.AM,
            timeFormat = TimeFormat.HOUR_12
        )

        assertEquals(9, state.selectedHour)
        assertEquals(30, state.selectedMinute)
        assertEquals(TimePeriod.AM, state.selectedPeriod)
    }

    @Test
    fun timePickerState_12HourFormat_afternoon_isCorrect() {
        val state = TimePickerState(
            initialHour = 3,
            initialMinute = 45,
            initialPeriod = TimePeriod.PM,
            timeFormat = TimeFormat.HOUR_12
        )

        assertEquals(3, state.selectedHour)
        assertEquals(45, state.selectedMinute)
        assertEquals(TimePeriod.PM, state.selectedPeriod)
    }

    @Test
    fun timePickerState_12HourFormat_12AM_midnight_isCorrect() {
        val state = TimePickerState(
            initialHour = 12,
            initialMinute = 0,
            initialPeriod = TimePeriod.AM,
            timeFormat = TimeFormat.HOUR_12
        )

        assertEquals(12, state.selectedHour)
        assertEquals(0, state.selectedMinute)
        assertEquals(TimePeriod.AM, state.selectedPeriod)
        assertEquals(0, state.selectedHourOfDay)
        assertEquals(LocalTime(0, 0), state.selectedTime)
    }

    @Test
    fun timePickerState_12HourFormat_12PM_noon_isCorrect() {
        val state = TimePickerState(
            initialHour = 12,
            initialMinute = 0,
            initialPeriod = TimePeriod.PM,
            timeFormat = TimeFormat.HOUR_12
        )

        assertEquals(12, state.selectedHour)
        assertEquals(0, state.selectedMinute)
        assertEquals(TimePeriod.PM, state.selectedPeriod)
        assertEquals(12, state.selectedHourOfDay)
        assertEquals(LocalTime(12, 0), state.selectedTime)
    }

    @Test
    fun timePickerState_12HourFormat_pmTime_convertsToLocalTime() {
        val state = TimePickerState(
            initialHour = 3,
            initialMinute = 45,
            initialPeriod = TimePeriod.PM,
            timeFormat = TimeFormat.HOUR_12
        )

        assertEquals(15, state.selectedHourOfDay)
        assertEquals(LocalTime(15, 45), state.selectedTime)
    }

    // ==================== Minute Boundary Tests ====================

    @Test
    fun timePickerState_minuteZero_isCorrect() {
        val state = TimePickerState(
            initialHour = 10,
            initialMinute = 0,
            initialPeriod = TimePeriod.AM,
            timeFormat = TimeFormat.HOUR_24
        )

        assertEquals(0, state.selectedMinute)
    }

    @Test
    fun timePickerState_minute59_isCorrect() {
        val state = TimePickerState(
            initialHour = 10,
            initialMinute = 59,
            initialPeriod = TimePeriod.AM,
            timeFormat = TimeFormat.HOUR_24
        )

        assertEquals(59, state.selectedMinute)
    }

    @Test
    fun timePickerState_invalidMinute_throws() {
        assertFailsWith<IllegalArgumentException> {
            TimePickerState(
                initialHour = 10,
                initialMinute = 60,
                initialPeriod = TimePeriod.AM,
                timeFormat = TimeFormat.HOUR_24
            )
        }
    }

    @Test
    fun timePickerState_negativeMinute_throws() {
        assertFailsWith<IllegalArgumentException> {
            TimePickerState(
                initialHour = 10,
                initialMinute = -1,
                initialPeriod = TimePeriod.AM,
                timeFormat = TimeFormat.HOUR_24
            )
        }
    }

    @Test
    fun timePickerState_invalid24Hour_throws() {
        assertFailsWith<IllegalArgumentException> {
            TimePickerState(
                initialHour = 24,
                initialMinute = 0,
                initialPeriod = TimePeriod.AM,
                timeFormat = TimeFormat.HOUR_24
            )
        }
    }

    @Test
    fun timePickerState_negative24Hour_throws() {
        assertFailsWith<IllegalArgumentException> {
            TimePickerState(
                initialHour = -1,
                initialMinute = 0,
                initialPeriod = TimePeriod.AM,
                timeFormat = TimeFormat.HOUR_24
            )
        }
    }

    @Test
    fun timePickerState_invalid12Hour_throws() {
        assertFailsWith<IllegalArgumentException> {
            TimePickerState(
                initialHour = 0,
                initialMinute = 0,
                initialPeriod = TimePeriod.AM,
                timeFormat = TimeFormat.HOUR_12
            )
        }
    }

    @Test
    fun timePickerState_12HourAboveRange_throws() {
        assertFailsWith<IllegalArgumentException> {
            TimePickerState(
                initialHour = 13,
                initialMinute = 0,
                initialPeriod = TimePeriod.AM,
                timeFormat = TimeFormat.HOUR_12
            )
        }
    }

    // ==================== State Independence Tests ====================

    @Test
    fun timePickerState_multipleInstances_areIndependent() {
        val state1 = TimePickerState(
            initialHour = 10,
            initialMinute = 30,
            initialPeriod = TimePeriod.AM,
            timeFormat = TimeFormat.HOUR_24
        )

        val state2 = TimePickerState(
            initialHour = 15,
            initialMinute = 45,
            initialPeriod = TimePeriod.PM,
            timeFormat = TimeFormat.HOUR_24
        )

        assertEquals(10, state1.selectedHour)
        assertEquals(15, state2.selectedHour)
        assertEquals(30, state1.selectedMinute)
        assertEquals(45, state2.selectedMinute)
    }

    // ==================== TimeFormat Tests ====================

    @Test
    fun timePickerState_timeFormatProperty_isCorrect() {
        val state24 = TimePickerState(
            initialHour = 10,
            initialMinute = 0,
            initialPeriod = TimePeriod.AM,
            timeFormat = TimeFormat.HOUR_24
        )

        val state12 = TimePickerState(
            initialHour = 10,
            initialMinute = 0,
            initialPeriod = TimePeriod.AM,
            timeFormat = TimeFormat.HOUR_12
        )

        assertEquals(TimeFormat.HOUR_24, state24.timeFormat)
        assertEquals(TimeFormat.HOUR_12, state12.timeFormat)
    }

    @Test
    fun timePickerState_24HourFormat_selectedTime_isCorrect() {
        val state = TimePickerState(
            initialHour = 23,
            initialMinute = 15,
            initialPeriod = TimePeriod.PM,
            timeFormat = TimeFormat.HOUR_24
        )

        assertEquals(23, state.selectedHourOfDay)
        assertEquals(LocalTime(23, 15), state.selectedTime)
    }

    @Test
    fun timePickerState_selectedTime_updatesWhenInternalStateChanges() {
        val state = TimePickerState(
            initialHour = 12,
            initialMinute = 0,
            initialPeriod = TimePeriod.AM,
            timeFormat = TimeFormat.HOUR_12
        )

        state.hourState.selectedItem = 11
        state.minuteState.selectedItem = 30
        state.periodState.selectedItem = TimePeriod.PM

        assertEquals(23, state.selectedHourOfDay)
        assertEquals(LocalTime(23, 30), state.selectedTime)
    }

    @Test
    fun timePickerState_saver_roundTripsCurrentSelection() {
        val state = TimePickerState(
            initialHour = 12,
            initialMinute = 0,
            initialPeriod = TimePeriod.AM,
            timeFormat = TimeFormat.HOUR_12
        )
        state.hourState.selectedItem = 7
        state.minuteState.selectedItem = 45
        state.periodState.selectedItem = TimePeriod.PM

        val restored = state.saveAndRestore()

        assertEquals(7, restored.selectedHour)
        assertEquals(45, restored.selectedMinute)
        assertEquals(TimePeriod.PM, restored.selectedPeriod)
        assertEquals(TimeFormat.HOUR_12, restored.timeFormat)
        assertEquals(LocalTime(19, 45), restored.selectedTime)
    }

    @Test
    fun initialHourForTimeFormat_converts24HourInputFor12HourMode() {
        assertEquals(12, initialHourForTimeFormat(0, TimeFormat.HOUR_12))
        assertEquals(12, initialHourForTimeFormat(12, TimeFormat.HOUR_12))
        assertEquals(1, initialHourForTimeFormat(13, TimeFormat.HOUR_12))
        assertEquals(11, initialHourForTimeFormat(23, TimeFormat.HOUR_12))
    }

    @Test
    fun initialHourForTimeFormat_rejectsOutOfRangeHourOfDay() {
        assertFailsWith<IllegalArgumentException> {
            initialHourForTimeFormat(24, TimeFormat.HOUR_12)
        }
        assertFailsWith<IllegalArgumentException> {
            initialHourForTimeFormat(-1, TimeFormat.HOUR_12)
        }
    }

    private fun TimePickerState.saveAndRestore(): TimePickerState {
        val saved = with(TimePickerState.Saver) {
            SaverScope { true }.save(this@saveAndRestore)
        }
        return TimePickerState.Saver.restore(saved!!)!!
    }
}
