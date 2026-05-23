package com.kez.picker.date

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import com.kez.picker.DatePickerItems
import com.kez.picker.util.currentDate
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

/**
 * Inclusive date range selected by [DateRangePicker].
 *
 * @param startDate The first selected date.
 * @param endDate The last selected date.
 * @throws IllegalArgumentException if [startDate] is after [endDate].
 */
data class DateRange(
    val startDate: LocalDate,
    val endDate: LocalDate
) {
    init {
        require(startDate <= endDate) {
            "startDate must be on or before endDate. startDate=$startDate, endDate=$endDate"
        }
    }

    /**
     * Returns whether [date] is inside this inclusive range.
     */
    operator fun contains(date: LocalDate): Boolean =
        date in startDate..endDate
}

/**
 * Creates and remembers a [DateRangePickerState].
 *
 * Initial values are read when the state is first created.
 *
 * @param initialStartDate The initial range start date.
 * @param initialEndDate The initial range end date.
 * @return A [DateRangePickerState] initialized with the requested date range.
 */
@Composable
fun rememberDateRangePickerState(
    initialStartDate: LocalDate = currentDate(),
    initialEndDate: LocalDate = initialStartDate
): DateRangePickerState {
    val rememberedInitialStartDate = remember { initialStartDate }
    val rememberedInitialEndDate = remember { initialEndDate }
    return rememberSaveable(saver = DateRangePickerState.Saver) {
        DateRangePickerState(
            initialStartDate = rememberedInitialStartDate,
            initialEndDate = rememberedInitialEndDate
        )
    }
}

/**
 * Creates and remembers a [DateRangePickerState] whose initial values are coerced by [items].
 *
 * Initial values and [items] are read when the state is first created.
 *
 * @param items Selectable values used to coerce [initialStartDate] and [initialEndDate].
 * @param initialStartDate The requested range start date.
 * @param initialEndDate The requested range end date.
 * @return A [DateRangePickerState] initialized to the closest selectable date range.
 */
@Composable
fun rememberDateRangePickerState(
    items: DatePickerItems,
    initialStartDate: LocalDate = currentDate(),
    initialEndDate: LocalDate = initialStartDate
): DateRangePickerState {
    val rememberedItems = remember { items }
    val rememberedInitialStartDate = remember { initialStartDate }
    val rememberedInitialEndDate = remember { initialEndDate }
    val coercedInitialRange = remember(
        rememberedItems,
        rememberedInitialStartDate,
        rememberedInitialEndDate
    ) {
        require(rememberedInitialStartDate <= rememberedInitialEndDate) {
            "initialStartDate must be on or before initialEndDate. " +
                    "initialStartDate=$rememberedInitialStartDate, " +
                    "initialEndDate=$rememberedInitialEndDate"
        }
        orderedDateRange(
            startDate = rememberedItems.coerceDate(rememberedInitialStartDate),
            endDate = rememberedItems.coerceDate(rememberedInitialEndDate)
        )
    }
    return rememberDateRangePickerState(
        initialStartDate = coercedInitialRange.startDate,
        initialEndDate = coercedInitialRange.endDate
    )
}

/**
 * State holder for [DateRangePicker].
 *
 * The selected range is always inclusive and ordered. If [selectStartDate] moves the start after
 * the current end, the end date is moved to the same date. If [selectEndDate] moves the end before
 * the current start, the start date is moved to the same date.
 *
 * @param initialStartDate The initial range start date.
 * @param initialEndDate The initial range end date.
 * @throws IllegalArgumentException if [initialStartDate] is after [initialEndDate].
 */
@Stable
class DateRangePickerState(
    initialStartDate: LocalDate,
    initialEndDate: LocalDate
) {
    init {
        require(initialStartDate <= initialEndDate) {
            "initialStartDate must be on or before initialEndDate. " +
                    "initialStartDate=$initialStartDate, initialEndDate=$initialEndDate"
        }
    }

    internal val startDatePickerState = DatePickerState(initialStartDate)
    internal val endDatePickerState = DatePickerState(initialEndDate)

    /**
     * The first selected date.
     */
    val selectedStartDate: LocalDate
        get() = startDatePickerState.selectedDate

    /**
     * The last selected date.
     */
    val selectedEndDate: LocalDate
        get() = endDatePickerState.selectedDate

    /**
     * The selected inclusive date range.
     */
    val selectedDateRange: DateRange
        get() = DateRange(selectedStartDate, selectedEndDate)

    /**
     * Programmatically selects [startDate] and [endDate].
     *
     * @throws IllegalArgumentException if [startDate] is after [endDate].
     */
    fun selectDateRange(startDate: LocalDate, endDate: LocalDate) {
        require(startDate <= endDate) {
            "startDate must be on or before endDate. startDate=$startDate, endDate=$endDate"
        }
        startDatePickerState.selectDate(startDate)
        endDatePickerState.selectDate(endDate)
    }

    /**
     * Programmatically selects the closest date range allowed by [items].
     */
    fun selectDateRange(startDate: LocalDate, endDate: LocalDate, items: DatePickerItems) {
        require(startDate <= endDate) {
            "startDate must be on or before endDate. startDate=$startDate, endDate=$endDate"
        }
        val coercedRange = orderedDateRange(
            startDate = items.coerceDate(startDate),
            endDate = items.coerceDate(endDate)
        )
        selectDateRange(
            startDate = coercedRange.startDate,
            endDate = coercedRange.endDate
        )
    }

    /**
     * Programmatically selects the range start date.
     */
    fun selectStartDate(date: LocalDate) {
        startDatePickerState.selectDate(date)
        if (selectedEndDate < selectedStartDate) {
            endDatePickerState.selectDate(selectedStartDate)
        }
    }

    /**
     * Programmatically selects the closest range start date allowed by [items].
     */
    fun selectStartDate(date: LocalDate, items: DatePickerItems) {
        selectStartDate(items.coerceDate(date))
    }

    /**
     * Programmatically selects the range end date.
     */
    fun selectEndDate(date: LocalDate) {
        endDatePickerState.selectDate(date)
        if (selectedStartDate > selectedEndDate) {
            startDatePickerState.selectDate(selectedEndDate)
        }
    }

    /**
     * Programmatically selects the closest range end date allowed by [items].
     */
    fun selectEndDate(date: LocalDate, items: DatePickerItems) {
        selectEndDate(items.coerceDate(date))
    }

    companion object {
        /**
         * Saves and restores [DateRangePickerState] across configuration changes.
         */
        val Saver: Saver<DateRangePickerState, Any> = listSaver(
            save = {
                listOf(
                    it.selectedStartDate.year,
                    it.selectedStartDate.month.number,
                    it.selectedStartDate.day,
                    it.selectedEndDate.year,
                    it.selectedEndDate.month.number,
                    it.selectedEndDate.day
                )
            },
            restore = {
                DateRangePickerState(
                    initialStartDate = LocalDate(
                        year = it[0] as Int,
                        month = it[1] as Int,
                        day = it[2] as Int
                    ),
                    initialEndDate = LocalDate(
                        year = it[3] as Int,
                        month = it[4] as Int,
                        day = it[5] as Int
                    )
                )
            }
        )
    }
}

private fun orderedDateRange(startDate: LocalDate, endDate: LocalDate): DateRange =
    if (startDate <= endDate) {
        DateRange(startDate = startDate, endDate = endDate)
    } else {
        DateRange(startDate = endDate, endDate = startDate)
    }
