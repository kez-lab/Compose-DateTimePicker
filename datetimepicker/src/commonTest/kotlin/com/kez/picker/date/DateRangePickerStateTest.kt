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

class DateRangePickerStateTest {

    @Test
    fun dateRange_requiresOrderedDates() {
        assertFailsWith<IllegalArgumentException> {
            DateRange(
                startDate = LocalDate(2026, 5, 2),
                endDate = LocalDate(2026, 5, 1)
            )
        }
    }

    @Test
    fun dateRange_containsInclusiveBoundaryDates() {
        val range = DateRange(
            startDate = LocalDate(2026, 5, 1),
            endDate = LocalDate(2026, 5, 3)
        )

        assertTrue(LocalDate(2026, 5, 1) in range)
        assertTrue(LocalDate(2026, 5, 2) in range)
        assertTrue(LocalDate(2026, 5, 3) in range)
    }

    @Test
    fun dateRangePickerState_initializesSelectedRange() {
        val state = DateRangePickerState(
            initialStartDate = LocalDate(2026, 5, 1),
            initialEndDate = LocalDate(2026, 5, 3)
        )

        assertEquals(LocalDate(2026, 5, 1), state.selectedStartDate)
        assertEquals(LocalDate(2026, 5, 3), state.selectedEndDate)
        assertEquals(
            DateRange(
                startDate = LocalDate(2026, 5, 1),
                endDate = LocalDate(2026, 5, 3)
            ),
            state.selectedDateRange
        )
    }

    @Test
    fun dateRangePickerState_initializesFromDateRange() {
        val state = DateRangePickerState(
            initialDateRange = DateRange(
                startDate = LocalDate(2026, 5, 1),
                endDate = LocalDate(2026, 5, 3)
            )
        )

        assertEquals(LocalDate(2026, 5, 1), state.selectedStartDate)
        assertEquals(LocalDate(2026, 5, 3), state.selectedEndDate)
    }

    @Test
    fun dateRangePickerState_initializesFromDateParts() {
        val state = DateRangePickerState(
            initialStartYear = 2026,
            initialStartMonth = 5,
            initialStartDay = 1,
            initialEndYear = 2026,
            initialEndMonth = 5,
            initialEndDay = 3
        )

        assertEquals(LocalDate(2026, 5, 1), state.selectedStartDate)
        assertEquals(LocalDate(2026, 5, 3), state.selectedEndDate)
    }

    @Test
    fun dateRangePickerState_initialDatePartsClampInvalidDayForMonth() {
        val state = DateRangePickerState(
            initialStartYear = 2026,
            initialStartMonth = 2,
            initialStartDay = 31,
            initialEndYear = 2026,
            initialEndMonth = 3,
            initialEndDay = 31
        )

        assertEquals(LocalDate(2026, 2, 28), state.selectedStartDate)
        assertEquals(LocalDate(2026, 3, 31), state.selectedEndDate)
    }

    @Test
    fun dateRangePickerState_rejectsInitialStartAfterEnd() {
        assertFailsWith<IllegalArgumentException> {
            DateRangePickerState(
                initialStartDate = LocalDate(2026, 5, 3),
                initialEndDate = LocalDate(2026, 5, 1)
            )
        }
    }

    @Test
    fun dateRangePickerState_selectStartDate_movesEndWhenStartPassesEnd() {
        val state = DateRangePickerState(
            initialStartDate = LocalDate(2026, 5, 1),
            initialEndDate = LocalDate(2026, 5, 3)
        )

        state.selectStartDate(LocalDate(2026, 5, 10))

        assertEquals(LocalDate(2026, 5, 10), state.selectedStartDate)
        assertEquals(LocalDate(2026, 5, 10), state.selectedEndDate)
    }

    @Test
    fun dateRangePickerState_selectStartDateParts_movesEndWhenStartPassesEnd() {
        val state = DateRangePickerState(
            initialStartDate = LocalDate(2026, 5, 1),
            initialEndDate = LocalDate(2026, 5, 3)
        )

        state.selectStartDate(year = 2026, month = 5, day = 10)

        assertEquals(LocalDate(2026, 5, 10), state.selectedStartDate)
        assertEquals(LocalDate(2026, 5, 10), state.selectedEndDate)
    }

    @Test
    fun dateRangePickerState_selectEndDate_movesStartWhenEndPrecedesStart() {
        val state = DateRangePickerState(
            initialStartDate = LocalDate(2026, 5, 5),
            initialEndDate = LocalDate(2026, 5, 10)
        )

        state.selectEndDate(LocalDate(2026, 5, 1))

        assertEquals(LocalDate(2026, 5, 1), state.selectedStartDate)
        assertEquals(LocalDate(2026, 5, 1), state.selectedEndDate)
    }

    @Test
    fun dateRangePickerState_selectEndDateParts_movesStartWhenEndPrecedesStart() {
        val state = DateRangePickerState(
            initialStartDate = LocalDate(2026, 5, 5),
            initialEndDate = LocalDate(2026, 5, 10)
        )

        state.selectEndDate(year = 2026, month = 5, day = 1)

        assertEquals(LocalDate(2026, 5, 1), state.selectedStartDate)
        assertEquals(LocalDate(2026, 5, 1), state.selectedEndDate)
    }

    @Test
    fun dateRangePickerState_selectDateRangeParts_updatesSelection() {
        val state = DateRangePickerState(
            initialStartDate = LocalDate(2026, 5, 1),
            initialEndDate = LocalDate(2026, 5, 3)
        )

        state.selectDateRange(
            startYear = 2027,
            startMonth = 1,
            startDay = 2,
            endYear = 2027,
            endMonth = 1,
            endDay = 5
        )

        assertEquals(LocalDate(2027, 1, 2), state.selectedStartDate)
        assertEquals(LocalDate(2027, 1, 5), state.selectedEndDate)
    }

    @Test
    fun dateRangePickerState_selectDateRangeValue_updatesSelection() {
        val state = DateRangePickerState(
            initialStartDate = LocalDate(2026, 5, 1),
            initialEndDate = LocalDate(2026, 5, 3)
        )

        state.selectDateRange(
            DateRange(
                startDate = LocalDate(2027, 1, 2),
                endDate = LocalDate(2027, 1, 5)
            )
        )

        assertEquals(LocalDate(2027, 1, 2), state.selectedStartDate)
        assertEquals(LocalDate(2027, 1, 5), state.selectedEndDate)
    }

    @Test
    fun dateRangePickerState_selectDateRangeWithItems_coercesSelection() {
        val state = DateRangePickerState(
            initialStartDate = LocalDate(2026, 5, 10),
            initialEndDate = LocalDate(2026, 6, 10)
        )
        val items = DatePickerItems(
            yearItems = listOf(2026),
            monthItems = (1..12).toList(),
            dayItems = (1..31).toList(),
            constraints = DatePickerConstraints(
                minDate = LocalDate(2026, Month.MAY, 10),
                maxDate = LocalDate(2026, Month.JUNE, 20)
            )
        )

        state.selectDateRange(
            startDate = LocalDate(2026, Month.JANUARY, 1),
            endDate = LocalDate(2026, Month.DECEMBER, 31),
            items = items
        )

        assertEquals(LocalDate(2026, Month.MAY, 10), state.selectedStartDate)
        assertEquals(LocalDate(2026, Month.JUNE, 20), state.selectedEndDate)
    }

    @Test
    fun dateRangePickerState_selectDateRangePartsWithItems_coercesSelection() {
        val state = DateRangePickerState(
            initialStartDate = LocalDate(2026, 5, 10),
            initialEndDate = LocalDate(2026, 6, 10)
        )
        val items = DatePickerItems(
            yearItems = listOf(2026),
            monthItems = (1..12).toList(),
            dayItems = (1..31).toList(),
            constraints = DatePickerConstraints(
                minDate = LocalDate(2026, Month.MAY, 10),
                maxDate = LocalDate(2026, Month.JUNE, 20)
            )
        )

        state.selectDateRange(
            startYear = 2026,
            startMonth = 1,
            startDay = 1,
            endYear = 2026,
            endMonth = 12,
            endDay = 31,
            items = items
        )

        assertEquals(LocalDate(2026, Month.MAY, 10), state.selectedStartDate)
        assertEquals(LocalDate(2026, Month.JUNE, 20), state.selectedEndDate)
    }

    @Test
    fun dateRangePickerState_selectDateRangeValueWithItems_coercesSelection() {
        val state = DateRangePickerState(
            initialStartDate = LocalDate(2026, 5, 10),
            initialEndDate = LocalDate(2026, 6, 10)
        )
        val items = DatePickerItems(
            yearItems = listOf(2026),
            monthItems = (1..12).toList(),
            dayItems = (1..31).toList(),
            constraints = DatePickerConstraints(
                minDate = LocalDate(2026, Month.MAY, 10),
                maxDate = LocalDate(2026, Month.JUNE, 20)
            )
        )

        state.selectDateRange(
            dateRange = DateRange(
                startDate = LocalDate(2026, Month.JANUARY, 1),
                endDate = LocalDate(2026, Month.DECEMBER, 31)
            ),
            items = items
        )

        assertEquals(LocalDate(2026, Month.MAY, 10), state.selectedStartDate)
        assertEquals(LocalDate(2026, Month.JUNE, 20), state.selectedEndDate)
    }

    @Test
    fun dateRangePickerState_saver_roundTripsCurrentSelection() {
        val state = DateRangePickerState(
            initialStartDate = LocalDate(2026, 5, 1),
            initialEndDate = LocalDate(2026, 5, 3)
        )
        state.selectDateRange(
            startDate = LocalDate(2027, 1, 2),
            endDate = LocalDate(2027, 1, 5)
        )

        val restored = state.saveAndRestore()

        assertEquals(LocalDate(2027, 1, 2), restored.selectedStartDate)
        assertEquals(LocalDate(2027, 1, 5), restored.selectedEndDate)
    }

    private fun DateRangePickerState.saveAndRestore(): DateRangePickerState {
        val saved = with(DateRangePickerState.Saver) {
            SaverScope { true }.save(this@saveAndRestore)
        }
        return DateRangePickerState.Saver.restore(saved!!)!!
    }
}
