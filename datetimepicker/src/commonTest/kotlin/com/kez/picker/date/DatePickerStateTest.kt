package com.kez.picker.date

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DatePickerStateTest {

    @Test
    fun testMaxDay_January() {
        // 2023-01-01
        val state = DatePickerState(initialYear = 2023, initialMonth = 1, initialDay = 1)
        assertEquals(31, state.maxDay)
    }

    @Test
    fun testMaxDay_February_NonLeapYear() {
        // 2023-02-01
        val state = DatePickerState(initialYear = 2023, initialMonth = 2, initialDay = 1)
        assertEquals(28, state.maxDay)
    }

    @Test
    fun testMaxDay_February_LeapYear() {
        // 2024-02-01 (Leap Year)
        val state = DatePickerState(initialYear = 2024, initialMonth = 2, initialDay = 1)
        assertEquals(29, state.maxDay)
    }

    @Test
    fun testMaxDay_February_LeapYear_Century() {
        // 2000-02-01 (Leap Year)
        val state = DatePickerState(initialYear = 2000, initialMonth = 2, initialDay = 1)
        assertEquals(29, state.maxDay)
    }

    @Test
    fun testMaxDay_February_NonLeapYear_Century() {
        // 1900-02-01 (Not a Leap Year)
        val state = DatePickerState(initialYear = 1900, initialMonth = 2, initialDay = 1)
        assertEquals(28, state.maxDay)
    }

    @Test
    fun testMaxDay_April() {
        // 2023-04-01
        val state = DatePickerState(initialYear = 2023, initialMonth = 4, initialDay = 1)
        assertEquals(30, state.maxDay)
    }

    @Test
    fun testInitialDay_Clamped_WhenExceedsMaxDay() {
        // Trying to set Feb 30 should be clamped to Feb 28
        val state = DatePickerState(initialYear = 2023, initialMonth = 2, initialDay = 30)
        assertEquals(28, state.selectedDay)
    }

    @Test
    fun testInitialDay_Clamped_LeapYear() {
        // Trying to set Feb 30 on leap year should be clamped to Feb 29
        val state = DatePickerState(initialYear = 2024, initialMonth = 2, initialDay = 30)
        assertEquals(29, state.selectedDay)
    }

    @Test
    fun testInitialMonth_Throws_WhenOutOfRange() {
        assertFailsWith<IllegalArgumentException> {
            DatePickerState(initialYear = 2024, initialMonth = 13, initialDay = 1)
        }
    }

    @Test
    fun testInitialDay_Throws_WhenLessThanOne() {
        assertFailsWith<IllegalArgumentException> {
            DatePickerState(initialYear = 2024, initialMonth = 1, initialDay = 0)
        }
    }

    @Test
    fun testSelectedValues_MatchInitialValues() {
        val state = DatePickerState(initialYear = 2025, initialMonth = 6, initialDay = 15)
        assertEquals(2025, state.selectedYear)
        assertEquals(6, state.selectedMonth)
        assertEquals(15, state.selectedDay)
    }

    @Test
    fun testMaxDay_AllMonths() {
        val daysInMonth = mapOf(
            1 to 31, 2 to 28, 3 to 31, 4 to 30,
            5 to 31, 6 to 30, 7 to 31, 8 to 31,
            9 to 30, 10 to 31, 11 to 30, 12 to 31
        )

        daysInMonth.forEach { (month, expectedDays) ->
            val state = DatePickerState(initialYear = 2023, initialMonth = month, initialDay = 1)
            assertEquals(expectedDays, state.maxDay, "Month $month should have $expectedDays days")
        }
    }

    @Test
    fun testMaxDay_February_LeapYearVariations() {
        // Test various leap years
        val leapYears = listOf(2000, 2004, 2020, 2024, 2400)
        val nonLeapYears = listOf(1900, 2100, 2023, 2025)

        leapYears.forEach { year ->
            val state = DatePickerState(initialYear = year, initialMonth = 2, initialDay = 1)
            assertEquals(29, state.maxDay, "Year $year should be a leap year with 29 days in Feb")
        }

        nonLeapYears.forEach { year ->
            val state = DatePickerState(initialYear = year, initialMonth = 2, initialDay = 1)
            assertEquals(28, state.maxDay, "Year $year should not be a leap year with 28 days in Feb")
        }
    }
}
