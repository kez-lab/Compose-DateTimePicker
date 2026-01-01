package com.kez.picker

import com.kez.picker.util.TimeFormat
import com.kez.picker.util.TimePeriod
import kotlin.test.Test
import kotlin.test.assertEquals

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
}
