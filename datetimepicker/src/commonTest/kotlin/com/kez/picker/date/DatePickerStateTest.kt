package com.kez.picker.date

import com.kez.picker.DatePickerState
import kotlin.test.Test
import kotlin.test.assertEquals

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
    fun testValidate_ClampsDay() {
        // Start at Jan 31
        val state = DatePickerState(initialYear = 2023, initialMonth = 1, initialDay = 31)
        
        // Change to Feb (Manual simulate state change since DatePickerState doesn't observe itself automatically unless in Composable)
        // But here we are unit testing the class logic.
        // We simulate "user changed month to 2". Max day becomes 28.
        // Currently selected day is 31.
        
        // We update the backing state for month directly (mimicking Picker behavior)
        state.monthState.selectedItem = 2
        
        // Assert maxDay is updated
        assertEquals(28, state.maxDay)
        
        // Validate should clamp day to 28
        state.validate()
        
        assertEquals(28, state.selectedDay)
    }

    @Test
    fun testValidate_LeapYearChange() {
        // Start at Feb 29, 2024 (Leap)
        val state = DatePickerState(initialYear = 2024, initialMonth = 2, initialDay = 29)
        assertEquals(29, state.maxDay)
        
        // Change Year to 2023 (Non-Leap)
        state.yearState.selectedItem = 2023
        
        // Max day should be 28
        assertEquals(28, state.maxDay)
        
        // Validate should clamp
        state.validate()
        
        assertEquals(28, state.selectedDay)
    }
}
