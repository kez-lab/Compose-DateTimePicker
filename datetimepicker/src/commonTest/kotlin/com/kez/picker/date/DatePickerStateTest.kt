package com.kez.picker.date

import androidx.compose.runtime.saveable.SaverScope
import com.kez.picker.DatePickerConstraints
import com.kez.picker.DatePickerItems
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

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
    fun datePickerState_localDateConstructor_initializesSelection() {
        val state = DatePickerState(LocalDate(2026, 5, 20))

        assertEquals(2026, state.selectedYear)
        assertEquals(5, state.selectedMonth)
        assertEquals(20, state.selectedDay)
        assertEquals(LocalDate(2026, 5, 20), state.selectedDate)
    }

    @Test
    fun testInitialMonth_Throws_WhenOutOfRange() {
        assertFailsWith<IllegalArgumentException> {
            DatePickerState(initialYear = 2024, initialMonth = 13, initialDay = 1)
        }
    }

    @Test
    fun testInitialYear_Throws_WhenOutOfRange() {
        assertFailsWith<IllegalArgumentException> {
            DatePickerState(initialYear = 999, initialMonth = 1, initialDay = 1)
        }
        assertFailsWith<IllegalArgumentException> {
            DatePickerState(initialYear = 10000, initialMonth = 1, initialDay = 1)
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
        assertEquals(LocalDate(2025, 6, 15), state.selectedDate)
    }

    @Test
    fun testSelectedDate_UpdatesWhenDateSelectionChanges() {
        val state = DatePickerState(initialYear = 2025, initialMonth = 1, initialDay = 1)

        state.selectDate(LocalDate(2026, 12, 25))

        assertEquals(LocalDate(2026, 12, 25), state.selectedDate)
    }

    @Test
    fun datePickerState_selectDate_updatesSelection() {
        val state = DatePickerState(initialYear = 2025, initialMonth = 1, initialDay = 1)

        state.selectDate(LocalDate(2026, 5, 20))

        assertEquals(2026, state.selectedYear)
        assertEquals(5, state.selectedMonth)
        assertEquals(20, state.selectedDay)
        assertEquals(LocalDate(2026, 5, 20), state.selectedDate)
    }

    @Test
    fun datePickerState_selectDateParts_updatesSelection() {
        val state = DatePickerState(initialYear = 2025, initialMonth = 1, initialDay = 1)

        state.selectDate(year = 2026, month = 5, day = 20)

        assertEquals(2026, state.selectedYear)
        assertEquals(5, state.selectedMonth)
        assertEquals(20, state.selectedDay)
        assertEquals(LocalDate(2026, 5, 20), state.selectedDate)
    }

    @Test
    fun datePickerState_selectDateParts_clampsInvalidDayForMonth() {
        val state = DatePickerState(initialYear = 2025, initialMonth = 1, initialDay = 1)

        state.selectDate(year = 2026, month = 2, day = 31)

        assertEquals(LocalDate(2026, 2, 28), state.selectedDate)
    }

    @Test
    fun datePickerState_selectDate_throwsWhenYearOutOfRange() {
        val state = DatePickerState(initialYear = 2025, initialMonth = 1, initialDay = 1)

        assertFailsWith<IllegalArgumentException> {
            state.selectDate(LocalDate(999, 1, 1))
        }
        assertFailsWith<IllegalArgumentException> {
            state.selectDate(LocalDate(10000, 1, 1))
        }
    }

    @Test
    fun testSaver_RoundTripsCurrentSelection() {
        val state = DatePickerState(initialYear = 2025, initialMonth = 1, initialDay = 1)
        state.selectDate(LocalDate(2026, 2, 28))

        val restored = state.saveAndRestore()

        assertEquals(2026, restored.selectedYear)
        assertEquals(2, restored.selectedMonth)
        assertEquals(28, restored.selectedDay)
        assertEquals(LocalDate(2026, 2, 28), restored.selectedDate)
    }

    @Test
    fun testValidateDatePickerItems_AllowsCurrentSelection() {
        val state = DatePickerState(initialYear = 2025, initialMonth = 6, initialDay = 15)

        validateDatePickerItems(
            state = state,
            yearItems = (2020..2030).toList(),
            monthItems = listOf(3, 6, 9, 12)
        )
    }

    @Test
    fun testValidateDatePickerItems_ThrowsWhenYearItemsMissingCurrentSelection() {
        val state = DatePickerState(initialYear = 2025, initialMonth = 6, initialDay = 15)

        val error = assertFailsWith<IllegalArgumentException> {
            validateDatePickerItems(
                state = state,
                yearItems = (2026..2030).toList(),
                monthItems = listOf(3, 6, 9, 12)
            )
        }

        val message = error.message.orEmpty()
        assertTrue(message.contains("rememberDatePickerState(items = items"))
        assertTrue(message.contains("items.coerceDate"))
        assertTrue(message.contains("state.selectDate(date, items)"))
    }

    @Test
    fun testValidateDatePickerItems_ThrowsWhenMonthItemsContainInvalidValue() {
        val state = DatePickerState(initialYear = 2025, initialMonth = 6, initialDay = 15)

        assertFailsWith<IllegalArgumentException> {
            validateDatePickerItems(
                state = state,
                yearItems = (2020..2030).toList(),
                monthItems = listOf(6, 13)
            )
        }
    }

    @Test
    fun testValidateDatePickerItems_ThrowsWhenMonthItemsMissingCurrentSelection() {
        val state = DatePickerState(initialYear = 2025, initialMonth = 6, initialDay = 15)

        assertFailsWith<IllegalArgumentException> {
            validateDatePickerItems(
                state = state,
                yearItems = (2020..2030).toList(),
                monthItems = listOf(3, 9, 12)
            )
        }
    }

    @Test
    fun testValidateDatePickerItems_AllowsBoundaryValues() {
        val state = DatePickerState(initialYear = 1000, initialMonth = 1, initialDay = 15)

        validateDatePickerItems(
            state = state,
            yearItems = listOf(1000, 9999),
            monthItems = listOf(1, 12)
        )
    }

    @Test
    fun testValidateDatePickerItems_AllowsCustomDayItemsContainingCurrentSelection() {
        val state = DatePickerState(initialYear = 2025, initialMonth = 6, initialDay = 15)

        validateDatePickerItems(
            state = state,
            items = DatePickerItems(
                yearItems = listOf(2025),
                monthItems = listOf(6),
                dayItems = listOf(1, 15)
            )
        )
    }

    @Test
    fun testValidateDatePickerItems_ThrowsWhenDayItemsMissingCurrentSelection() {
        val state = DatePickerState(initialYear = 2025, initialMonth = 6, initialDay = 15)

        assertFailsWith<IllegalArgumentException> {
            validateDatePickerItems(
                state = state,
                items = DatePickerItems(
                    yearItems = listOf(2025),
                    monthItems = listOf(6),
                    dayItems = listOf(1, 10)
                )
            )
        }
    }

    @Test
    fun testValidateDatePickerItems_ThrowsWhenDayItemsAreEmpty() {
        val state = DatePickerState(initialYear = 2025, initialMonth = 6, initialDay = 15)

        assertFailsWith<IllegalArgumentException> {
            validateDatePickerItems(
                state = state,
                items = DatePickerItems(
                    yearItems = listOf(2025),
                    monthItems = listOf(6),
                    dayItems = emptyList()
                )
            )
        }
    }

    @Test
    fun testValidateDatePickerItems_ThrowsWhenDayItemsContainDuplicateValues() {
        val state = DatePickerState(initialYear = 2025, initialMonth = 6, initialDay = 15)

        assertFailsWith<IllegalArgumentException> {
            validateDatePickerItems(
                state = state,
                items = DatePickerItems(
                    yearItems = listOf(2025),
                    monthItems = listOf(6),
                    dayItems = listOf(1, 15, 15)
                )
            )
        }
    }

    @Test
    fun testValidateDatePickerItems_ThrowsWhenDayItemsContainInvalidValue() {
        val state = DatePickerState(initialYear = 2025, initialMonth = 6, initialDay = 15)

        assertFailsWith<IllegalArgumentException> {
            validateDatePickerItems(
                state = state,
                items = DatePickerItems(
                    yearItems = listOf(2025),
                    monthItems = listOf(6),
                    dayItems = listOf(0, 15, 32)
                )
            )
        }
    }

    @Test
    fun testValidateDatePickerItems_ThrowsWhenDayItemsCannotRepresentEveryYearMonth() {
        val state = DatePickerState(initialYear = 2025, initialMonth = 1, initialDay = 31)

        assertFailsWith<IllegalArgumentException> {
            validateDatePickerItems(
                state = state,
                items = DatePickerItems(
                    yearItems = listOf(2025),
                    monthItems = listOf(1, 2),
                    dayItems = listOf(31)
                )
            )
        }
    }

    @Test
    fun datePickerItems_coerceDate_usesClosestItems() {
        val items = DatePickerItems(
            yearItems = listOf(2024, 2026),
            monthItems = listOf(2, 12),
            dayItems = listOf(1, 15, 31)
        )

        assertEquals(
            LocalDate(year = 2026, month = Month.FEBRUARY, day = 1),
            items.coerceDate(LocalDate(year = 2025, month = Month.NOVEMBER, day = 30))
        )
    }

    @Test
    fun datePickerItems_coerceDate_comparesAdjacentYearsWhenRequestedYearIsSelectable() {
        val items = DatePickerItems(
            yearItems = listOf(2025, 2026),
            monthItems = listOf(1, 12),
            dayItems = listOf(1)
        )

        assertEquals(
            LocalDate(year = 2026, month = Month.JANUARY, day = 1),
            items.coerceDate(LocalDate(year = 2025, month = Month.DECEMBER, day = 31))
        )
    }

    @Test
    fun datePickerItems_contains_checksMembership() {
        val items = DatePickerItems(
            yearItems = listOf(2024, 2026),
            monthItems = listOf(2, 12),
            dayItems = listOf(1, 15, 31)
        )

        assertEquals(true, items.contains(LocalDate(year = 2024, month = Month.DECEMBER, day = 31)))
        assertEquals(false, items.contains(LocalDate(year = 2025, month = Month.DECEMBER, day = 31)))
        assertEquals(false, items.contains(LocalDate(year = 2024, month = Month.FEBRUARY, day = 29)))
    }

    @Test
    fun datePickerItems_containsParts_checksMembership() {
        val items = DatePickerItems(
            yearItems = listOf(2024, 2026),
            monthItems = listOf(2, 12),
            dayItems = listOf(1, 15, 31)
        )

        assertEquals(true, items.contains(year = 2024, month = 12, day = 31))
        assertEquals(false, items.contains(year = 2025, month = 12, day = 31))
        assertEquals(false, items.contains(year = 2024, month = 2, day = 29))
        assertEquals(false, items.contains(year = 2024, month = 2, day = 30))
        assertEquals(false, items.contains(year = 2024, month = 13, day = 1))
    }

    @Test
    fun datePickerConstraints_throwsWhenMinimumIsAfterMaximum() {
        val error = assertFailsWith<IllegalArgumentException> {
            DatePickerConstraints(
                minDate = LocalDate(year = 2026, month = Month.JUNE, day = 20),
                maxDate = LocalDate(year = 2026, month = Month.MAY, day = 10)
            )
        }

        assertTrue(error.message.orEmpty().contains("sort it before creating DatePickerConstraints"))
    }

    @Test
    fun datePickerItems_contains_respectsDateConstraints() {
        val items = DatePickerItems(
            yearItems = listOf(2026),
            monthItems = (1..12).toList(),
            dayItems = (1..31).toList(),
            constraints = DatePickerConstraints(
                minDate = LocalDate(year = 2026, month = Month.MAY, day = 10),
                maxDate = LocalDate(year = 2026, month = Month.JUNE, day = 20)
            )
        )

        assertEquals(true, items.contains(LocalDate(year = 2026, month = Month.MAY, day = 10)))
        assertEquals(true, items.contains(LocalDate(year = 2026, month = Month.JUNE, day = 20)))
        assertEquals(false, items.contains(LocalDate(year = 2026, month = Month.MAY, day = 9)))
        assertEquals(false, items.contains(LocalDate(year = 2026, month = Month.JUNE, day = 21)))
    }

    @Test
    fun datePickerItems_coerceDate_respectsDateConstraints() {
        val items = DatePickerItems(
            yearItems = listOf(2026),
            monthItems = (1..12).toList(),
            dayItems = (1..31).toList(),
            constraints = DatePickerConstraints(
                minDate = LocalDate(year = 2026, month = Month.MAY, day = 10),
                maxDate = LocalDate(year = 2026, month = Month.JUNE, day = 20)
            )
        )

        assertEquals(
            LocalDate(year = 2026, month = Month.MAY, day = 10),
            items.coerceDate(LocalDate(year = 2026, month = Month.JANUARY, day = 1))
        )
        assertEquals(
            LocalDate(year = 2026, month = Month.JUNE, day = 20),
            items.coerceDate(LocalDate(year = 2026, month = Month.DECEMBER, day = 31))
        )
    }

    @Test
    fun datePickerItems_coerceDate_throwsActionableMessageWhenConstraintsFilterEveryDate() {
        val items = DatePickerItems(
            yearItems = listOf(2026),
            monthItems = listOf(1),
            dayItems = listOf(1),
            constraints = DatePickerConstraints(
                minDate = LocalDate(year = 2026, month = Month.MAY, day = 10),
                maxDate = LocalDate(year = 2026, month = Month.JUNE, day = 20)
            )
        )

        val error = assertFailsWith<IllegalArgumentException> {
            items.coerceDate(LocalDate(year = 2026, month = Month.JANUARY, day = 1))
        }

        val message = error.message.orEmpty()
        assertTrue(message.contains("minDate/maxDate"))
        assertTrue(message.contains("year/month/day"))
    }

    @Test
    fun validateDatePickerItems_allowsConstraintsToFilterBoundaryMonths() {
        val state = DatePickerState(initialYear = 2026, initialMonth = 3, initialDay = 31)
        val items = DatePickerItems(
            yearItems = listOf(2026),
            monthItems = (1..12).toList(),
            dayItems = listOf(31),
            constraints = DatePickerConstraints(
                minDate = LocalDate(year = 2026, month = Month.MARCH, day = 31),
                maxDate = LocalDate(year = 2026, month = Month.MARCH, day = 31)
            )
        )

        validateDatePickerItems(
            state = state,
            items = items
        )
    }

    @Test
    fun validateDatePickerItems_throwsWhenCurrentDateIsOutsideConstraints() {
        val state = DatePickerState(initialYear = 2026, initialMonth = 5, initialDay = 9)
        val items = DatePickerItems(
            yearItems = listOf(2026),
            monthItems = (1..12).toList(),
            dayItems = (1..31).toList(),
            constraints = DatePickerConstraints(
                minDate = LocalDate(year = 2026, month = Month.MAY, day = 10),
                maxDate = LocalDate(year = 2026, month = Month.JUNE, day = 20)
            )
        )

        val error = assertFailsWith<IllegalArgumentException> {
            validateDatePickerItems(
                state = state,
                items = items
            )
        }

        val message = error.message.orEmpty()
        assertTrue(message.contains("constraints"))
        assertTrue(message.contains("rememberDatePickerState(items = items"))
        assertTrue(message.contains("items.coerceDate"))
        assertTrue(message.contains("state.selectDate(date, items)"))
    }

    @Test
    fun datePickerItems_coerceDate_filtersDayItemsByCoercedYearMonth() {
        val items = DatePickerItems(
            yearItems = listOf(2025),
            monthItems = listOf(2),
            dayItems = listOf(15, 31)
        )

        assertEquals(
            LocalDate(year = 2025, month = Month.FEBRUARY, day = 15),
            items.coerceDate(LocalDate(year = 2025, month = Month.FEBRUARY, day = 28))
        )
    }

    @Test
    fun datePickerItems_coerceDateParts_clampsInvalidDayBeforeCoercing() {
        val items = DatePickerItems(
            yearItems = listOf(2025),
            monthItems = listOf(2),
            dayItems = listOf(15, 28)
        )

        assertEquals(
            LocalDate(year = 2025, month = Month.FEBRUARY, day = 28),
            items.coerceDate(year = 2025, month = 2, day = 31)
        )
    }

    @Test
    fun datePickerState_selectDateWithItems_coercesSelection() {
        val state = DatePickerState(initialYear = 2024, initialMonth = 2, initialDay = 1)
        val items = DatePickerItems(
            yearItems = listOf(2024, 2026),
            monthItems = listOf(2, 12),
            dayItems = listOf(1, 15, 31)
        )

        state.selectDate(
            date = LocalDate(year = 2025, month = Month.NOVEMBER, day = 30),
            items = items
        )

        assertEquals(LocalDate(year = 2026, month = Month.FEBRUARY, day = 1), state.selectedDate)
    }

    @Test
    fun datePickerState_selectDatePartsWithItems_coercesSelection() {
        val state = DatePickerState(initialYear = 2024, initialMonth = 2, initialDay = 1)
        val items = DatePickerItems(
            yearItems = listOf(2024, 2026),
            monthItems = listOf(2, 12),
            dayItems = listOf(1, 15, 31)
        )

        state.selectDate(
            year = 2025,
            month = 11,
            day = 30,
            items = items
        )

        assertEquals(LocalDate(year = 2026, month = Month.FEBRUARY, day = 1), state.selectedDate)
    }

    @Test
    fun datePickerState_selectDateWithConstrainedItems_coercesSelection() {
        val state = DatePickerState(initialYear = 2026, initialMonth = 5, initialDay = 10)
        val items = DatePickerItems(
            yearItems = listOf(2026),
            monthItems = (1..12).toList(),
            dayItems = (1..31).toList(),
            constraints = DatePickerConstraints(
                minDate = LocalDate(year = 2026, month = Month.MAY, day = 10),
                maxDate = LocalDate(year = 2026, month = Month.JUNE, day = 20)
            )
        )

        state.selectDate(
            date = LocalDate(year = 2026, month = Month.JANUARY, day = 1),
            items = items
        )

        assertEquals(LocalDate(year = 2026, month = Month.MAY, day = 10), state.selectedDate)
    }

    @Test
    fun testValidateDatePickerItems_ThrowsWhenYearItemsContainInvalidValue() {
        val state = DatePickerState(initialYear = 2025, initialMonth = 6, initialDay = 15)

        assertFailsWith<IllegalArgumentException> {
            validateDatePickerItems(
                state = state,
                yearItems = listOf(999, 2025, 10000),
                monthItems = listOf(3, 6, 9, 12)
            )
        }
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

    private fun DatePickerState.saveAndRestore(): DatePickerState {
        val saved = with(DatePickerState.Saver) {
            SaverScope { true }.save(this@saveAndRestore)
        }
        return DatePickerState.Saver.restore(saved!!)!!
    }
}
