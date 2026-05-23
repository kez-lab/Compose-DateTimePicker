package com.kez.picker

import androidx.compose.runtime.saveable.SaverScope
import com.kez.picker.time.TimePickerState
import com.kez.picker.time.initialHourForTimeFormat
import com.kez.picker.time.validateTimePickerItems
import com.kez.picker.util.TimeFormat
import com.kez.picker.util.TimePeriod
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

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

    @Test
    fun timePickerState_localTimeConstructor_initializes24HourSelection() {
        val state = TimePickerState(
            initialTime = LocalTime(21, 45),
            timeFormat = TimeFormat.HOUR_24
        )

        assertEquals(21, state.selectedHour)
        assertEquals(45, state.selectedMinute)
        assertEquals(TimePeriod.PM, state.selectedPeriod)
        assertEquals(LocalTime(21, 45), state.selectedTime)
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

    @Test
    fun timePickerState_localTimeConstructor_initializes12HourSelection() {
        val state = TimePickerState(
            initialTime = LocalTime(13, 5),
            timeFormat = TimeFormat.HOUR_12
        )

        assertEquals(1, state.selectedHour)
        assertEquals(5, state.selectedMinute)
        assertEquals(TimePeriod.PM, state.selectedPeriod)
        assertEquals(LocalTime(13, 5), state.selectedTime)
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

    @Test
    fun validateTimePickerItems_allowsCurrentSelection() {
        val state = TimePickerState(
            initialHour = 14,
            initialMinute = 30,
            initialPeriod = TimePeriod.PM,
            timeFormat = TimeFormat.HOUR_24
        )

        validateTimePickerItems(
            state = state,
            minuteItems = listOf(0, 15, 30, 45),
            hourItems = (9..18).toList(),
            periodItems = emptyList()
        )
    }

    @Test
    fun validateTimePickerItems_allows12HourCurrentSelection() {
        val state = TimePickerState(
            initialHour = 11,
            initialMinute = 30,
            initialPeriod = TimePeriod.PM,
            timeFormat = TimeFormat.HOUR_12
        )

        validateTimePickerItems(
            state = state,
            minuteItems = listOf(0, 15, 30, 45),
            hourItems = listOf(9, 10, 11, 12),
            periodItems = TimePeriod.entries
        )
    }

    @Test
    fun validateTimePickerItems_usesHour24ItemsFor24HourFormat() {
        val state = TimePickerState(
            initialHour = 14,
            initialMinute = 30,
            initialPeriod = TimePeriod.PM,
            timeFormat = TimeFormat.HOUR_24
        )

        validateTimePickerItems(
            state = state,
            items = TimePickerItems(
                minuteItems = listOf(0, 30),
                hour24Items = listOf(14, 15),
                hour12Items = emptyList(),
                periodItems = emptyList()
            )
        )
    }

    @Test
    fun validateTimePickerItems_usesHour12ItemsFor12HourFormat() {
        val state = TimePickerState(
            initialHour = 2,
            initialMinute = 30,
            initialPeriod = TimePeriod.PM,
            timeFormat = TimeFormat.HOUR_12
        )

        val error = assertFailsWith<IllegalArgumentException> {
            validateTimePickerItems(
                state = state,
                items = TimePickerItems(
                    minuteItems = listOf(0, 30),
                    hour24Items = listOf(14, 15),
                    hour12Items = listOf(1, 3),
                    periodItems = TimePeriod.entries
                )
            )
        }

        assertTrue(error.message?.contains("hour12Items") == true)
    }

    @Test
    fun validateTimePickerItems_throwsWhenMinuteItemsMissingCurrentSelection() {
        val state = TimePickerState(
            initialHour = 14,
            initialMinute = 30,
            initialPeriod = TimePeriod.PM,
            timeFormat = TimeFormat.HOUR_24
        )

        assertFailsWith<IllegalArgumentException> {
            validateTimePickerItems(
                state = state,
                minuteItems = listOf(0, 15, 45),
                hourItems = (9..18).toList(),
                periodItems = emptyList()
            )
        }
    }

    @Test
    fun validateTimePickerItems_throwsWhenHourItemsMissingCurrentSelection() {
        val state = TimePickerState(
            initialHour = 14,
            initialMinute = 30,
            initialPeriod = TimePeriod.PM,
            timeFormat = TimeFormat.HOUR_24
        )

        assertFailsWith<IllegalArgumentException> {
            validateTimePickerItems(
                state = state,
                minuteItems = listOf(0, 15, 30, 45),
                hourItems = listOf(9, 10, 11, 12, 13),
                periodItems = emptyList()
            )
        }
    }

    @Test
    fun validateTimePickerItems_allowsBoundaryValues() {
        val state24 = TimePickerState(
            initialHour = 23,
            initialMinute = 59,
            initialPeriod = TimePeriod.PM,
            timeFormat = TimeFormat.HOUR_24
        )
        val state12 = TimePickerState(
            initialHour = 12,
            initialMinute = 0,
            initialPeriod = TimePeriod.AM,
            timeFormat = TimeFormat.HOUR_12
        )

        validateTimePickerItems(
            state = state24,
            minuteItems = listOf(0, 59),
            hourItems = listOf(0, 23),
            periodItems = emptyList()
        )
        validateTimePickerItems(
            state = state12,
            minuteItems = listOf(0, 59),
            hourItems = listOf(1, 12),
            periodItems = TimePeriod.entries
        )
    }

    @Test
    fun validateTimePickerItems_throwsWhenMinuteItemsContainInvalidValue() {
        val state = TimePickerState(
            initialHour = 14,
            initialMinute = 30,
            initialPeriod = TimePeriod.PM,
            timeFormat = TimeFormat.HOUR_24
        )

        val exception = assertFailsWith<IllegalArgumentException> {
            validateTimePickerItems(
                state = state,
                minuteItems = listOf(-1, 0, 30, 60),
                hourItems = (9..18).toList(),
                periodItems = emptyList()
            )
        }

        assertTrue(exception.message.orEmpty().contains("Invalid values: [-1, 60]"))
    }

    @Test
    fun validateTimePickerItems_throwsWhenHourItemsContainInvalid12HourValue() {
        val state = TimePickerState(
            initialHour = 11,
            initialMinute = 30,
            initialPeriod = TimePeriod.AM,
            timeFormat = TimeFormat.HOUR_12
        )

        assertFailsWith<IllegalArgumentException> {
            validateTimePickerItems(
                state = state,
                minuteItems = (0..59).toList(),
                hourItems = listOf(0, 1, 2, 11),
                periodItems = TimePeriod.entries
            )
        }
    }

    @Test
    fun validateTimePickerItems_throwsWhenHourItemsContainInvalid24HourValue() {
        val state = TimePickerState(
            initialHour = 14,
            initialMinute = 30,
            initialPeriod = TimePeriod.PM,
            timeFormat = TimeFormat.HOUR_24
        )

        assertFailsWith<IllegalArgumentException> {
            validateTimePickerItems(
                state = state,
                minuteItems = (0..59).toList(),
                hourItems = listOf(-1, 0, 14, 24),
                periodItems = emptyList()
            )
        }
    }

    @Test
    fun validateTimePickerItems_throwsWhenMinuteItemsAreEmpty() {
        val state = TimePickerState(
            initialHour = 14,
            initialMinute = 30,
            initialPeriod = TimePeriod.PM,
            timeFormat = TimeFormat.HOUR_24
        )

        assertFailsWith<IllegalArgumentException> {
            validateTimePickerItems(
                state = state,
                minuteItems = emptyList(),
                hourItems = (9..18).toList(),
                periodItems = emptyList()
            )
        }
    }

    @Test
    fun validateTimePickerItems_throwsWhenHourItemsAreEmpty() {
        val state = TimePickerState(
            initialHour = 14,
            initialMinute = 30,
            initialPeriod = TimePeriod.PM,
            timeFormat = TimeFormat.HOUR_24
        )

        assertFailsWith<IllegalArgumentException> {
            validateTimePickerItems(
                state = state,
                minuteItems = (0..59).toList(),
                hourItems = emptyList(),
                periodItems = emptyList()
            )
        }
    }

    @Test
    fun validateTimePickerItems_throwsWhen12HourPeriodItemsAreEmpty() {
        val state = TimePickerState(
            initialHour = 11,
            initialMinute = 30,
            initialPeriod = TimePeriod.PM,
            timeFormat = TimeFormat.HOUR_12
        )

        assertFailsWith<IllegalArgumentException> {
            validateTimePickerItems(
                state = state,
                minuteItems = (0..59).toList(),
                hourItems = (1..12).toList(),
                periodItems = emptyList()
            )
        }
    }

    @Test
    fun validateTimePickerItems_throwsWhenPeriodItemsMissingCurrentSelection() {
        val state = TimePickerState(
            initialHour = 11,
            initialMinute = 30,
            initialPeriod = TimePeriod.PM,
            timeFormat = TimeFormat.HOUR_12
        )

        assertFailsWith<IllegalArgumentException> {
            validateTimePickerItems(
                state = state,
                minuteItems = (0..59).toList(),
                hourItems = (1..12).toList(),
                periodItems = listOf(TimePeriod.AM)
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
    fun timePickerState_selectTime_updates24HourSelection() {
        val state = TimePickerState(
            initialHour = 8,
            initialMinute = 0,
            initialPeriod = TimePeriod.AM,
            timeFormat = TimeFormat.HOUR_24
        )

        state.selectTime(LocalTime(21, 45))

        assertEquals(21, state.selectedHour)
        assertEquals(45, state.selectedMinute)
        assertEquals(TimePeriod.PM, state.selectedPeriod)
        assertEquals(LocalTime(21, 45), state.selectedTime)
    }

    @Test
    fun timePickerState_selectTime_updates12HourSelection() {
        val state = TimePickerState(
            initialHour = 8,
            initialMinute = 0,
            initialPeriod = TimePeriod.AM,
            timeFormat = TimeFormat.HOUR_12
        )

        state.selectTime(LocalTime(0, 30))

        assertEquals(12, state.selectedHour)
        assertEquals(30, state.selectedMinute)
        assertEquals(TimePeriod.AM, state.selectedPeriod)
        assertEquals(LocalTime(0, 30), state.selectedTime)

        state.selectTime(LocalTime(13, 5))

        assertEquals(1, state.selectedHour)
        assertEquals(5, state.selectedMinute)
        assertEquals(TimePeriod.PM, state.selectedPeriod)
        assertEquals(LocalTime(13, 5), state.selectedTime)
    }

    @Test
    fun timePickerState_selectedTime_updatesWhenSelectionChanges() {
        val state = TimePickerState(
            initialHour = 12,
            initialMinute = 0,
            initialPeriod = TimePeriod.AM,
            timeFormat = TimeFormat.HOUR_12
        )

        state.selectTime(LocalTime(23, 30))

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
        state.selectTime(LocalTime(19, 45))

        val restored = state.saveAndRestore()

        assertEquals(7, restored.selectedHour)
        assertEquals(45, restored.selectedMinute)
        assertEquals(TimePeriod.PM, restored.selectedPeriod)
        assertEquals(TimeFormat.HOUR_12, restored.timeFormat)
        assertEquals(LocalTime(19, 45), restored.selectedTime)
    }

    @Test
    fun timePickerState_saver_roundTrips12HourMidnightAndNoon() {
        val midnight = TimePickerState(
            initialHour = 12,
            initialMinute = 5,
            initialPeriod = TimePeriod.AM,
            timeFormat = TimeFormat.HOUR_12
        ).saveAndRestore()

        assertEquals(12, midnight.selectedHour)
        assertEquals(5, midnight.selectedMinute)
        assertEquals(TimePeriod.AM, midnight.selectedPeriod)
        assertEquals(TimeFormat.HOUR_12, midnight.timeFormat)
        assertEquals(0, midnight.selectedHourOfDay)
        assertEquals(LocalTime(0, 5), midnight.selectedTime)

        val noon = TimePickerState(
            initialHour = 12,
            initialMinute = 15,
            initialPeriod = TimePeriod.PM,
            timeFormat = TimeFormat.HOUR_12
        ).saveAndRestore()

        assertEquals(12, noon.selectedHour)
        assertEquals(15, noon.selectedMinute)
        assertEquals(TimePeriod.PM, noon.selectedPeriod)
        assertEquals(TimeFormat.HOUR_12, noon.timeFormat)
        assertEquals(12, noon.selectedHourOfDay)
        assertEquals(LocalTime(12, 15), noon.selectedTime)
    }

    @Test
    fun timePickerItems_coerceTime_usesClosest24HourItems() {
        val items = TimePickerItems(
            minuteItems = listOf(0, 30),
            hour24Items = listOf(9, 17),
            hour12Items = emptyList(),
            periodItems = emptyList()
        )

        assertEquals(LocalTime(17, 30), items.coerceTime(LocalTime(14, 20), TimeFormat.HOUR_24))
    }

    @Test
    fun timePickerItems_coerceTime_usesClosest12HourDisplayItems() {
        val items = TimePickerItems(
            minuteItems = listOf(0, 30),
            hour24Items = emptyList(),
            hour12Items = listOf(9, 11),
            periodItems = listOf(TimePeriod.AM)
        )

        assertEquals(LocalTime(9, 30), items.coerceTime(LocalTime(22, 45), TimeFormat.HOUR_12))
    }

    @Test
    fun timePickerState_selectTimeWithItems_coercesSelection() {
        val state = TimePickerState(
            initialHour = 8,
            initialMinute = 0,
            initialPeriod = TimePeriod.AM,
            timeFormat = TimeFormat.HOUR_24
        )
        val items = TimePickerItems(
            minuteItems = listOf(0, 30),
            hour24Items = listOf(9, 17),
            hour12Items = emptyList(),
            periodItems = emptyList()
        )

        state.selectTime(LocalTime(14, 20), items)

        assertEquals(LocalTime(17, 30), state.selectedTime)
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
