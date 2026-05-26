package com.kez.picker

import androidx.compose.runtime.saveable.SaverScope
import com.kez.picker.date.YearMonth
import com.kez.picker.date.YearMonthPickerState
import com.kez.picker.date.validateYearMonthPickerItems
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/**
 * Unit tests for [YearMonthPickerState] class.
 *
 * Tests cover:
 * - Initial value setting for year and month
 * - Boundary conditions for months (1-12)
 * - Various year values
 * - State independence
 */
class YearMonthPickerStateTest {

    // ==================== Initial Value Tests ====================

    @Test
    fun yearMonthPickerState_initialValues_areCorrect() {
        val state = YearMonthPickerState(
            initialYear = 2024,
            initialMonth = 6
        )

        assertEquals(2024, state.selectedYear)
        assertEquals(6, state.selectedMonth)
        assertEquals(YearMonth(year = 2024, month = 6), state.selectedYearMonth)
        assertEquals(LocalDate(2024, 6, 1), state.selectedMonthDate)
    }

    @Test
    fun yearMonthPickerState_januaryFirstMonth_isCorrect() {
        val state = YearMonthPickerState(
            initialYear = 2024,
            initialMonth = 1
        )

        assertEquals(1, state.selectedMonth)
    }

    @Test
    fun yearMonthPickerState_decemberLastMonth_isCorrect() {
        val state = YearMonthPickerState(
            initialYear = 2024,
            initialMonth = 12
        )

        assertEquals(12, state.selectedMonth)
    }

    @Test
    fun yearMonthPickerState_yearMonthConstructor_initializesSelection() {
        val state = YearMonthPickerState(
            initialYearMonth = YearMonth(year = 2026, month = 5)
        )

        assertEquals(YearMonth(year = 2026, month = 5), state.selectedYearMonth)
    }

    @Test
    fun yearMonthPickerState_localDateConstructor_ignoresDayValue() {
        val state = YearMonthPickerState(
            initialDate = LocalDate(2026, 5, 20)
        )

        assertEquals(YearMonth(year = 2026, month = 5), state.selectedYearMonth)
        assertEquals(LocalDate(2026, 5, 1), state.selectedMonthDate)
    }

    // ==================== Year Boundary Tests ====================

    @Test
    fun yearMonthPickerState_year1900_isCorrect() {
        val state = YearMonthPickerState(
            initialYear = 1900,
            initialMonth = 1
        )

        assertEquals(1900, state.selectedYear)
    }

    @Test
    fun yearMonthPickerState_year2100_isCorrect() {
        val state = YearMonthPickerState(
            initialYear = 2100,
            initialMonth = 12
        )

        assertEquals(2100, state.selectedYear)
    }

    @Test
    fun yearMonthPickerState_minimumYear_isCorrect() {
        val state = YearMonthPickerState(
            initialYear = 1000,
            initialMonth = 1
        )

        assertEquals(1000, state.selectedYear)
    }

    @Test
    fun yearMonthPickerState_maximumYear_isCorrect() {
        val state = YearMonthPickerState(
            initialYear = 9999,
            initialMonth = 12
        )

        assertEquals(9999, state.selectedYear)
    }

    @Test
    fun yearMonthPickerState_yearBelowRange_throws() {
        assertFailsWith<IllegalArgumentException> {
            YearMonthPickerState(
                initialYear = 999,
                initialMonth = 1
            )
        }
    }

    @Test
    fun yearMonthPickerState_yearAboveRange_throws() {
        assertFailsWith<IllegalArgumentException> {
            YearMonthPickerState(
                initialYear = 10000,
                initialMonth = 1
            )
        }
    }

    @Test
    fun yearMonthPickerState_currentYear_isCorrect() {
        val state = YearMonthPickerState(
            initialYear = 2025,
            initialMonth = 1
        )

        assertEquals(2025, state.selectedYear)
    }

    // ==================== Month Boundary Tests ====================

    @Test
    fun yearMonthPickerState_allMonths_areValid() {
        for (month in 1..12) {
            val state = YearMonthPickerState(
                initialYear = 2024,
                initialMonth = month
            )
            assertEquals(month, state.selectedMonth, "Month $month should be correctly stored")
        }
    }

    @Test
    fun yearMonthPickerState_invalidMonth_throws() {
        assertFailsWith<IllegalArgumentException> {
            YearMonthPickerState(
                initialYear = 2024,
                initialMonth = 13
            )
        }
    }

    @Test
    fun yearMonthPickerState_zeroMonth_throws() {
        assertFailsWith<IllegalArgumentException> {
            YearMonthPickerState(
                initialYear = 2024,
                initialMonth = 0
            )
        }
    }

    @Test
    fun yearMonthPickerState_selectedMonthDate_updatesWhenSelectionChanges() {
        val state = YearMonthPickerState(
            initialYear = 2024,
            initialMonth = 1
        )

        state.selectYearMonth(year = 2026, month = 12)

        assertEquals(LocalDate(2026, 12, 1), state.selectedMonthDate)
    }

    @Test
    fun yearMonthPickerState_selectedYearMonth_updatesWhenSelectionChanges() {
        val state = YearMonthPickerState(
            initialYear = 2024,
            initialMonth = 1
        )

        state.selectYearMonth(YearMonth(year = 2026, month = 12))

        assertEquals(YearMonth(year = 2026, month = 12), state.selectedYearMonth)
        assertEquals(LocalDate(2026, 12, 1), state.selectedYearMonth.atDay())
    }

    @Test
    fun yearMonth_fromLocalDate_ignoresDayValue() {
        assertEquals(
            YearMonth(year = 2026, month = 5),
            YearMonth.from(LocalDate(2026, 5, 20))
        )
    }

    @Test
    fun yearMonthPickerState_selectYearMonth_updatesSelection() {
        val state = YearMonthPickerState(
            initialYear = 2024,
            initialMonth = 1
        )

        state.selectYearMonth(year = 2026, month = 12)

        assertEquals(2026, state.selectedYear)
        assertEquals(12, state.selectedMonth)
        assertEquals(LocalDate(2026, 12, 1), state.selectedMonthDate)
    }

    @Test
    fun yearMonthPickerState_selectDate_updatesYearAndMonth() {
        val state = YearMonthPickerState(
            initialYear = 2024,
            initialMonth = 1
        )

        state.selectDate(LocalDate(2026, 5, 20))

        assertEquals(2026, state.selectedYear)
        assertEquals(5, state.selectedMonth)
        assertEquals(LocalDate(2026, 5, 1), state.selectedMonthDate)
    }

    @Test
    fun yearMonthPickerState_selectYearMonth_rejectsInvalidValues() {
        val state = YearMonthPickerState(
            initialYear = 2024,
            initialMonth = 1
        )

        assertFailsWith<IllegalArgumentException> {
            state.selectYearMonth(year = 999, month = 1)
        }
        assertFailsWith<IllegalArgumentException> {
            state.selectYearMonth(year = 2024, month = 13)
        }
    }

    @Test
    fun yearMonthPickerState_saver_roundTripsCurrentSelection() {
        val state = YearMonthPickerState(
            initialYear = 2024,
            initialMonth = 1
        )
        state.selectYearMonth(year = 2026, month = 12)

        val restored = state.saveAndRestore()

        assertEquals(2026, restored.selectedYear)
        assertEquals(12, restored.selectedMonth)
        assertEquals(LocalDate(2026, 12, 1), restored.selectedMonthDate)
    }

    @Test
    fun yearMonthPickerItems_coerceYearMonth_usesClosestItems() {
        val items = YearMonthPickerItems(
            yearItems = listOf(2024, 2026),
            monthItems = listOf(3, 9)
        )

        assertEquals(
            YearMonth(year = 2024, month = 9),
            items.coerceYearMonth(YearMonth(year = 2025, month = 6))
        )
    }

    @Test
    fun yearMonth_compareTo_ordersByWholeMonth() {
        assertEquals(
            true,
            YearMonth(year = 2024, month = 12) < YearMonth(year = 2025, month = 1)
        )
    }

    @Test
    fun yearMonthPickerConstraints_throwsWhenMinimumIsAfterMaximum() {
        assertFailsWith<IllegalArgumentException> {
            YearMonthPickerConstraints(
                minYearMonth = YearMonth(year = 2025, month = 1),
                maxYearMonth = YearMonth(year = 2024, month = 12)
            )
        }
    }

    @Test
    fun yearMonthPickerItems_contains_checksMembership() {
        val items = YearMonthPickerItems(
            yearItems = listOf(2024, 2026),
            monthItems = listOf(3, 9)
        )

        assertEquals(true, items.contains(YearMonth(year = 2024, month = 9)))
        assertEquals(false, items.contains(YearMonth(year = 2025, month = 9)))
        assertEquals(false, items.contains(year = 2024, month = 12))
        assertEquals(true, items.contains(LocalDate(2026, 3, 20)))
    }

    @Test
    fun yearMonthPickerItems_contains_respectsConstraints() {
        val items = YearMonthPickerItems(
            yearItems = listOf(2024, 2025, 2026),
            monthItems = listOf(1, 6, 12),
            constraints = YearMonthPickerConstraints(
                minYearMonth = YearMonth(year = 2024, month = 6),
                maxYearMonth = YearMonth(year = 2025, month = 6)
            )
        )

        assertEquals(true, items.contains(YearMonth(year = 2024, month = 6)))
        assertEquals(true, items.contains(YearMonth(year = 2025, month = 6)))
        assertEquals(false, items.contains(YearMonth(year = 2024, month = 1)))
        assertEquals(false, items.contains(YearMonth(year = 2026, month = 1)))
    }

    @Test
    fun yearMonthPickerItems_containsParts_returnsFalseForInvalidRawValues() {
        val items = YearMonthPickerItems(
            yearItems = listOf(2024, 2026),
            monthItems = listOf(3, 12, 13)
        )

        assertEquals(false, items.contains(year = 999, month = 3))
        assertEquals(false, items.contains(year = 2024, month = 13))
    }

    @Test
    fun yearMonthPickerItems_coerceYearMonth_respectsConstraints() {
        val items = YearMonthPickerItems(
            yearItems = listOf(2024, 2025, 2026),
            monthItems = listOf(1, 6, 12),
            constraints = YearMonthPickerConstraints(
                minYearMonth = YearMonth(year = 2024, month = 6),
                maxYearMonth = YearMonth(year = 2025, month = 6)
            )
        )

        assertEquals(
            YearMonth(year = 2024, month = 6),
            items.coerceYearMonth(year = 2024, month = 1)
        )
        assertEquals(
            YearMonth(year = 2025, month = 6),
            items.coerceYearMonth(year = 2026, month = 12)
        )
    }

    @Test
    fun yearMonthPickerItems_coerceYearMonth_allowsRawValuesOutsidePickerRange() {
        val items = YearMonthPickerItems(
            yearItems = listOf(2024, 2026),
            monthItems = listOf(3, 12)
        )

        assertEquals(
            YearMonth(year = 2024, month = 3),
            items.coerceYearMonth(year = 999, month = 13)
        )
    }

    @Test
    fun yearMonthPickerState_selectYearMonthWithItems_coercesSelection() {
        val state = YearMonthPickerState(initialYear = 2024, initialMonth = 3)
        val items = YearMonthPickerItems(
            yearItems = listOf(2024, 2026),
            monthItems = listOf(3, 9)
        )

        state.selectYearMonth(year = 2025, month = 6, items = items)

        assertEquals(YearMonth(year = 2024, month = 9), state.selectedYearMonth)
    }

    @Test
    fun yearMonthPickerState_selectYearMonthWithItems_allowsRawValuesOutsidePickerRange() {
        val state = YearMonthPickerState(initialYear = 2024, initialMonth = 3)
        val items = YearMonthPickerItems(
            yearItems = listOf(2024, 2026),
            monthItems = listOf(3, 12)
        )

        state.selectYearMonth(year = 999, month = 13, items = items)

        assertEquals(YearMonth(year = 2024, month = 3), state.selectedYearMonth)
    }

    @Test
    fun yearMonthPickerState_selectYearMonthWithConstrainedItems_coercesSelection() {
        val state = YearMonthPickerState(initialYear = 2024, initialMonth = 6)
        val items = YearMonthPickerItems(
            yearItems = listOf(2024, 2025, 2026),
            monthItems = listOf(1, 6, 12),
            constraints = YearMonthPickerConstraints(
                minYearMonth = YearMonth(year = 2024, month = 6),
                maxYearMonth = YearMonth(year = 2025, month = 6)
            )
        )

        state.selectYearMonth(year = 2026, month = 12, items = items)

        assertEquals(YearMonth(year = 2025, month = 6), state.selectedYearMonth)
    }

    @Test
    fun validateYearMonthPickerItems_allowsCurrentSelection() {
        val state = YearMonthPickerState(
            initialYear = 2024,
            initialMonth = 6
        )

        validateYearMonthPickerItems(
            state = state,
            yearItems = (2020..2030).toList(),
            monthItems = listOf(3, 6, 9, 12)
        )
    }

    @Test
    fun validateYearMonthPickerItems_throwsWhenYearItemsMissingCurrentSelection() {
        val state = YearMonthPickerState(
            initialYear = 2024,
            initialMonth = 6
        )

        val error = assertFailsWith<IllegalArgumentException> {
            validateYearMonthPickerItems(
                state = state,
                yearItems = (2025..2030).toList(),
                monthItems = listOf(3, 6, 9, 12)
            )
        }

        val message = error.message.orEmpty()
        assertTrue(message.contains("rememberYearMonthPickerState(items = items"))
        assertTrue(message.contains("items.coerceYearMonth"))
        assertTrue(message.contains("state.selectYearMonth(yearMonth, items)"))
    }

    @Test
    fun validateYearMonthPickerItems_throwsWhenMonthItemsContainInvalidValue() {
        val state = YearMonthPickerState(
            initialYear = 2024,
            initialMonth = 6
        )

        assertFailsWith<IllegalArgumentException> {
            validateYearMonthPickerItems(
                state = state,
                yearItems = (2020..2030).toList(),
                monthItems = listOf(0, 6, 13)
            )
        }
    }

    @Test
    fun validateYearMonthPickerItems_throwsWhenMonthItemsMissingCurrentSelection() {
        val state = YearMonthPickerState(
            initialYear = 2024,
            initialMonth = 6
        )

        assertFailsWith<IllegalArgumentException> {
            validateYearMonthPickerItems(
                state = state,
                yearItems = (2020..2030).toList(),
                monthItems = listOf(3, 9, 12)
            )
        }
    }

    @Test
    fun validateYearMonthPickerItems_throwsWhenCurrentSelectionOutsideConstraints() {
        val state = YearMonthPickerState(
            initialYear = 2024,
            initialMonth = 1
        )
        val items = YearMonthPickerItems(
            yearItems = listOf(2024, 2025),
            monthItems = listOf(1, 6, 12),
            constraints = YearMonthPickerConstraints(
                minYearMonth = YearMonth(year = 2024, month = 6),
                maxYearMonth = YearMonth(year = 2025, month = 6)
            )
        )

        val error = assertFailsWith<IllegalArgumentException> {
            validateYearMonthPickerItems(state = state, items = items)
        }

        assertEquals(true, error.message.orEmpty().contains("constraints"))
        assertTrue(error.message.orEmpty().contains("rememberYearMonthPickerState(items = items"))
        assertTrue(error.message.orEmpty().contains("items.coerceYearMonth"))
        assertTrue(error.message.orEmpty().contains("state.selectYearMonth(yearMonth, items)"))
    }

    @Test
    fun validateYearMonthPickerItems_allowsBoundaryValues() {
        val state = YearMonthPickerState(
            initialYear = 1000,
            initialMonth = 1
        )

        validateYearMonthPickerItems(
            state = state,
            yearItems = listOf(1000, 9999),
            monthItems = listOf(1, 12)
        )
    }

    @Test
    fun validateYearMonthPickerItems_throwsWhenYearItemsContainInvalidValue() {
        val state = YearMonthPickerState(
            initialYear = 2024,
            initialMonth = 6
        )

        assertFailsWith<IllegalArgumentException> {
            validateYearMonthPickerItems(
                state = state,
                yearItems = listOf(999, 2024, 10000),
                monthItems = listOf(3, 6, 9, 12)
            )
        }
    }

    // ==================== State Independence Tests ====================

    @Test
    fun yearMonthPickerState_multipleInstances_areIndependent() {
        val state1 = YearMonthPickerState(
            initialYear = 2020,
            initialMonth = 3
        )

        val state2 = YearMonthPickerState(
            initialYear = 2024,
            initialMonth = 9
        )

        assertEquals(2020, state1.selectedYear)
        assertEquals(2024, state2.selectedYear)
        assertEquals(3, state1.selectedMonth)
        assertEquals(9, state2.selectedMonth)
    }

    @Test
    fun yearMonthPickerState_sameValues_areEqual() {
        val state1 = YearMonthPickerState(
            initialYear = 2024,
            initialMonth = 6
        )

        val state2 = YearMonthPickerState(
            initialYear = 2024,
            initialMonth = 6
        )

        assertEquals(state1.selectedYear, state2.selectedYear)
        assertEquals(state1.selectedMonth, state2.selectedMonth)
    }

    @Test
    fun yearMonthPickerState_differentValues_areNotEqual() {
        val state1 = YearMonthPickerState(
            initialYear = 2024,
            initialMonth = 6
        )

        val state2 = YearMonthPickerState(
            initialYear = 2025,
            initialMonth = 7
        )

        assertNotEquals(state1.selectedYear, state2.selectedYear)
        assertNotEquals(state1.selectedMonth, state2.selectedMonth)
    }

    // ==================== Special Date Tests ====================

    @Test
    fun yearMonthPickerState_leapYear_isCorrect() {
        val state = YearMonthPickerState(
            initialYear = 2024,
            initialMonth = 2
        )

        assertEquals(2024, state.selectedYear)
        assertEquals(2, state.selectedMonth)
    }

    @Test
    fun yearMonthPickerState_centuryYear_isCorrect() {
        val state = YearMonthPickerState(
            initialYear = 2000,
            initialMonth = 2
        )

        assertEquals(2000, state.selectedYear)
        assertEquals(2, state.selectedMonth)
    }

    @Test
    fun yearMonthPickerState_endOfYear_isCorrect() {
        val state = YearMonthPickerState(
            initialYear = 2024,
            initialMonth = 12
        )

        assertEquals(2024, state.selectedYear)
        assertEquals(12, state.selectedMonth)
    }

    @Test
    fun yearMonthPickerState_startOfYear_isCorrect() {
        val state = YearMonthPickerState(
            initialYear = 2024,
            initialMonth = 1
        )

        assertEquals(2024, state.selectedYear)
        assertEquals(1, state.selectedMonth)
    }

    private fun YearMonthPickerState.saveAndRestore(): YearMonthPickerState {
        val saved = with(YearMonthPickerState.Saver) {
            SaverScope { true }.save(this@saveAndRestore)
        }
        return YearMonthPickerState.Saver.restore(saved!!)!!
    }
}
