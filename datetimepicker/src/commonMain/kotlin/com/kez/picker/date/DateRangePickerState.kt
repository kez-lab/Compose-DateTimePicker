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
 * Use [ordered] when app-owned start/end inputs may arrive in either order.
 *
 * @param startDate The first selected date.
 * @param endDate The last selected date.
 * @throws IllegalArgumentException if [startDate] is after [endDate].
 */
data class DateRange(
    val startDate: LocalDate,
    val endDate: LocalDate
) {
    /**
     * Creates a [DateRange] with explicit start and end date parts.
     *
     * If a day is greater than the maximum day for its year/month, it is clamped to that maximum.
     *
     * @param startYear The range start year. Must be in 1000..9999.
     * @param startMonth The range start month. Must be in 1..12.
     * @param startDay The range start day. Must be at least 1.
     * @param endYear The range end year. Must be in 1000..9999.
     * @param endMonth The range end month. Must be in 1..12.
     * @param endDay The range end day. Must be at least 1.
     * @throws IllegalArgumentException if any date part is outside its supported range, or if the
     * resulting start date is after the resulting end date.
     */
    constructor(
        startYear: Int,
        startMonth: Int,
        startDay: Int,
        endYear: Int = startYear,
        endMonth: Int = startMonth,
        endDay: Int = startDay
    ) : this(
        startDate = dateFromParts(year = startYear, month = startMonth, day = startDay),
        endDate = dateFromParts(year = endYear, month = endMonth, day = endDay)
    )

    init {
        require(startDate <= endDate) {
            "startDate must be on or before endDate. startDate=$startDate, endDate=$endDate. " +
                    dateRangeOrderedAdvice()
        }
    }

    /**
     * Number of calendar days in this inclusive range.
     *
     * A range whose [startDate] and [endDate] are the same has a [dayCount] of 1.
     */
    val dayCount: Int
        get() = (endDate.toEpochDays() - startDate.toEpochDays() + 1).toInt()

    /**
     * Returns whether this range selects exactly one calendar day.
     */
    val isSingleDay: Boolean
        get() = startDate == endDate

    /**
     * Returns whether [date] is inside this inclusive range.
     */
    operator fun contains(date: LocalDate): Boolean =
        date in startDate..endDate

    /**
     * Returns whether [range] is fully inside this inclusive range.
     */
    operator fun contains(range: DateRange): Boolean =
        range.startDate >= startDate && range.endDate <= endDate

    /**
     * Returns whether [year], [month], and [day] are inside this inclusive range.
     *
     * Values outside the supported year, month, or day ranges return false. A [day] greater than the
     * maximum valid day for [year] and [month] also returns false.
     */
    fun contains(year: Int, month: Int, day: Int): Boolean {
        if (year !in 1000..9999 || month !in 1..12 || day < 1) return false
        if (day > daysInMonth(year, month)) return false
        return LocalDate(year = year, month = month, day = day) in this
    }

    /**
     * Returns whether this inclusive range and [range] share at least one calendar day.
     */
    fun overlaps(range: DateRange): Boolean =
        startDate <= range.endDate && range.startDate <= endDate

    /**
     * Returns the inclusive overlap between this range and [range], or `null` when they do not
     * share any calendar day.
     */
    fun intersection(range: DateRange): DateRange? {
        val intersectionStart = maxOf(startDate, range.startDate)
        val intersectionEnd = minOf(endDate, range.endDate)
        return if (intersectionStart <= intersectionEnd) {
            DateRange(startDate = intersectionStart, endDate = intersectionEnd)
        } else {
            null
        }
    }

    companion object {
        /**
         * Creates a [DateRange] from two dates, ordering them if [startDate] is after [endDate].
         *
         * This is useful when a form, preset, or restored value supplies two dates without a
         * guaranteed start/end order.
         */
        fun ordered(startDate: LocalDate, endDate: LocalDate): DateRange =
            if (startDate <= endDate) {
                DateRange(startDate = startDate, endDate = endDate)
            } else {
                DateRange(startDate = endDate, endDate = startDate)
            }

        /**
         * Creates a [DateRange] from explicit date parts, ordering the resulting dates if needed.
         *
         * If a day is greater than the maximum day for its year/month, it is clamped before the
         * dates are ordered.
         */
        fun ordered(
            startYear: Int,
            startMonth: Int,
            startDay: Int,
            endYear: Int = startYear,
            endMonth: Int = startMonth,
            endDay: Int = startDay
        ): DateRange =
            ordered(
                startDate = dateFromParts(year = startYear, month = startMonth, day = startDay),
                endDate = dateFromParts(year = endYear, month = endMonth, day = endDay)
            )
    }
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
 * Creates and remembers a [DateRangePickerState] from an initial [DateRange].
 *
 * Initial values are read when the state is first created.
 *
 * @param initialDateRange The initial range to be selected.
 * @return A [DateRangePickerState] initialized with [initialDateRange].
 */
@Composable
fun rememberDateRangePickerState(
    initialDateRange: DateRange
): DateRangePickerState {
    return rememberDateRangePickerState(
        initialStartDate = initialDateRange.startDate,
        initialEndDate = initialDateRange.endDate
    )
}

/**
 * Creates and remembers a [DateRangePickerState] with explicit start and end date parts.
 *
 * Initial values are read when the state is first created. If a day is greater than the maximum day
 * for its year/month, it is clamped to that maximum.
 *
 * @param initialStartYear The initial range start year. Must be in 1000..9999.
 * @param initialStartMonth The initial range start month. Must be in 1..12.
 * @param initialStartDay The initial range start day. Must be at least 1.
 * @param initialEndYear The initial range end year. Must be in 1000..9999.
 * @param initialEndMonth The initial range end month. Must be in 1..12.
 * @param initialEndDay The initial range end day. Must be at least 1.
 * @return A [DateRangePickerState] initialized with the requested date range.
 */
@Composable
fun rememberDateRangePickerState(
    initialStartYear: Int,
    initialStartMonth: Int,
    initialStartDay: Int,
    initialEndYear: Int = initialStartYear,
    initialEndMonth: Int = initialStartMonth,
    initialEndDay: Int = initialStartDay
): DateRangePickerState {
    val rememberedInitialStartYear = remember { initialStartYear }
    val rememberedInitialStartMonth = remember { initialStartMonth }
    val rememberedInitialStartDay = remember { initialStartDay }
    val rememberedInitialEndYear = remember { initialEndYear }
    val rememberedInitialEndMonth = remember { initialEndMonth }
    val rememberedInitialEndDay = remember { initialEndDay }
    return rememberSaveable(saver = DateRangePickerState.Saver) {
        DateRangePickerState(
            initialStartYear = rememberedInitialStartYear,
            initialStartMonth = rememberedInitialStartMonth,
            initialStartDay = rememberedInitialStartDay,
            initialEndYear = rememberedInitialEndYear,
            initialEndMonth = rememberedInitialEndMonth,
            initialEndDay = rememberedInitialEndDay
        )
    }
}

/**
 * Creates and remembers a [DateRangePickerState] whose initial values are coerced by [items].
 *
 * Initial values and [items] are read when the state is first created.
 * This overload accepts unordered [initialStartDate] and [initialEndDate] values. Both dates are
 * coerced by [items] and then ordered before the state is created.
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
        rememberedItems.coerceDateRange(
            startDate = rememberedInitialStartDate,
            endDate = rememberedInitialEndDate
        )
    }
    return rememberDateRangePickerState(
        initialStartDate = coercedInitialRange.startDate,
        initialEndDate = coercedInitialRange.endDate
    )
}

/**
 * Creates and remembers a [DateRangePickerState] from an initial [DateRange] coerced by [items].
 *
 * Initial values and [items] are read when the state is first created.
 *
 * @param items Selectable values used to coerce [initialDateRange].
 * @param initialDateRange The requested initial range.
 * @return A [DateRangePickerState] initialized to the closest selectable date range.
 */
@Composable
fun rememberDateRangePickerState(
    items: DatePickerItems,
    initialDateRange: DateRange
): DateRangePickerState {
    return rememberDateRangePickerState(
        items = items,
        initialStartDate = initialDateRange.startDate,
        initialEndDate = initialDateRange.endDate
    )
}

/**
 * Creates and remembers a [DateRangePickerState] with explicit start and end date parts coerced by
 * [items].
 *
 * Initial values and [items] are read when the state is first created. If a day is greater than the
 * maximum day for its year/month, it is clamped before [items] coercion.
 * This overload accepts unordered resulting dates and orders them before the state is created.
 *
 * @param items Selectable values used to coerce the requested start and end dates.
 * @param initialStartYear The requested range start year.
 * @param initialStartMonth The requested range start month.
 * @param initialStartDay The requested range start day.
 * @param initialEndYear The requested range end year.
 * @param initialEndMonth The requested range end month.
 * @param initialEndDay The requested range end day.
 * @return A [DateRangePickerState] initialized to the closest selectable date range.
 */
@Composable
fun rememberDateRangePickerState(
    items: DatePickerItems,
    initialStartYear: Int,
    initialStartMonth: Int,
    initialStartDay: Int,
    initialEndYear: Int = initialStartYear,
    initialEndMonth: Int = initialStartMonth,
    initialEndDay: Int = initialStartDay
): DateRangePickerState {
    val rememberedInitialStartYear = remember { initialStartYear }
    val rememberedInitialStartMonth = remember { initialStartMonth }
    val rememberedInitialStartDay = remember { initialStartDay }
    val rememberedInitialEndYear = remember { initialEndYear }
    val rememberedInitialEndMonth = remember { initialEndMonth }
    val rememberedInitialEndDay = remember { initialEndDay }
    val initialStartDate = remember(
        rememberedInitialStartYear,
        rememberedInitialStartMonth,
        rememberedInitialStartDay
    ) {
        dateFromParts(
            year = rememberedInitialStartYear,
            month = rememberedInitialStartMonth,
            day = rememberedInitialStartDay
        )
    }
    val initialEndDate = remember(
        rememberedInitialEndYear,
        rememberedInitialEndMonth,
        rememberedInitialEndDay
    ) {
        dateFromParts(
            year = rememberedInitialEndYear,
            month = rememberedInitialEndMonth,
            day = rememberedInitialEndDay
        )
    }
    return rememberDateRangePickerState(
        items = items,
        initialStartDate = initialStartDate,
        initialEndDate = initialEndDate
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
    /**
     * Creates a [DateRangePickerState] from [initialDateRange].
     */
    constructor(initialDateRange: DateRange) : this(
        initialStartDate = initialDateRange.startDate,
        initialEndDate = initialDateRange.endDate
    )

    /**
     * Creates a [DateRangePickerState] with explicit start and end date parts.
     *
     * If a day is greater than the maximum day for its year/month, it is clamped to that maximum.
     */
    constructor(
        initialStartYear: Int,
        initialStartMonth: Int,
        initialStartDay: Int,
        initialEndYear: Int = initialStartYear,
        initialEndMonth: Int = initialStartMonth,
        initialEndDay: Int = initialStartDay
    ) : this(
        initialStartDate = dateFromParts(
            year = initialStartYear,
            month = initialStartMonth,
            day = initialStartDay
        ),
        initialEndDate = dateFromParts(
            year = initialEndYear,
            month = initialEndMonth,
            day = initialEndDay
        )
    )

    init {
        require(initialStartDate <= initialEndDate) {
            "initialStartDate must be on or before initialEndDate. " +
                    "initialStartDate=$initialStartDate, initialEndDate=$initialEndDate. " +
                    dateRangeOrderedAdvice()
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
            "startDate must be on or before endDate. startDate=$startDate, endDate=$endDate. " +
                    dateRangeOrderedAdvice()
        }
        startDatePickerState.selectDate(startDate)
        endDatePickerState.selectDate(endDate)
    }

    /**
     * Programmatically selects [dateRange].
     */
    fun selectDateRange(dateRange: DateRange) {
        selectDateRange(
            startDate = dateRange.startDate,
            endDate = dateRange.endDate
        )
    }

    /**
     * Programmatically selects the range from explicit start and end date parts.
     *
     * If a day is greater than the maximum day for its year/month, it is clamped to that maximum.
     *
     * @throws IllegalArgumentException if the resulting start date is after the resulting end date.
     */
    fun selectDateRange(
        startYear: Int,
        startMonth: Int,
        startDay: Int,
        endYear: Int,
        endMonth: Int,
        endDay: Int
    ) {
        selectDateRange(
            startDate = dateFromParts(year = startYear, month = startMonth, day = startDay),
            endDate = dateFromParts(year = endYear, month = endMonth, day = endDay)
        )
    }

    /**
     * Programmatically selects the closest date range allowed by [items].
     */
    fun selectDateRange(startDate: LocalDate, endDate: LocalDate, items: DatePickerItems) {
        require(startDate <= endDate) {
            "startDate must be on or before endDate. startDate=$startDate, endDate=$endDate. " +
                    dateRangeOrderedAdvice()
        }
        val coercedRange = items.coerceDateRange(startDate = startDate, endDate = endDate)
        selectDateRange(
            startDate = coercedRange.startDate,
            endDate = coercedRange.endDate
        )
    }

    /**
     * Programmatically selects the closest date range to [dateRange] allowed by [items].
     */
    fun selectDateRange(dateRange: DateRange, items: DatePickerItems) {
        selectDateRange(
            startDate = dateRange.startDate,
            endDate = dateRange.endDate,
            items = items
        )
    }

    /**
     * Programmatically selects the closest date range to the requested date parts allowed by [items].
     *
     * If a day is greater than the maximum day for its year/month, it is clamped before [items]
     * coercion.
     */
    fun selectDateRange(
        startYear: Int,
        startMonth: Int,
        startDay: Int,
        endYear: Int,
        endMonth: Int,
        endDay: Int,
        items: DatePickerItems
    ) {
        selectDateRange(
            startDate = dateFromParts(year = startYear, month = startMonth, day = startDay),
            endDate = dateFromParts(year = endYear, month = endMonth, day = endDay),
            items = items
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
     * Programmatically selects the range start date from explicit date parts.
     *
     * If [day] is greater than the maximum day for [year] and [month], it is clamped to that
     * maximum.
     */
    fun selectStartDate(year: Int, month: Int, day: Int) {
        selectStartDate(dateFromParts(year = year, month = month, day = day))
    }

    /**
     * Programmatically selects the closest range start date allowed by [items].
     */
    fun selectStartDate(date: LocalDate, items: DatePickerItems) {
        selectStartDate(items.coerceDate(date))
    }

    /**
     * Programmatically selects the closest range start date to [year], [month], and [day] allowed by
     * [items].
     */
    fun selectStartDate(year: Int, month: Int, day: Int, items: DatePickerItems) {
        selectStartDate(dateFromParts(year = year, month = month, day = day), items)
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
     * Programmatically selects the range end date from explicit date parts.
     *
     * If [day] is greater than the maximum day for [year] and [month], it is clamped to that
     * maximum.
     */
    fun selectEndDate(year: Int, month: Int, day: Int) {
        selectEndDate(dateFromParts(year = year, month = month, day = day))
    }

    /**
     * Programmatically selects the closest range end date allowed by [items].
     */
    fun selectEndDate(date: LocalDate, items: DatePickerItems) {
        selectEndDate(items.coerceDate(date))
    }

    /**
     * Programmatically selects the closest range end date to [year], [month], and [day] allowed by
     * [items].
     */
    fun selectEndDate(year: Int, month: Int, day: Int, items: DatePickerItems) {
        selectEndDate(dateFromParts(year = year, month = month, day = day), items)
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

private fun dateFromParts(year: Int, month: Int, day: Int): LocalDate {
    require(year in 1000..9999) {
        "year must be in range [1000, 9999], but was $year"
    }
    require(month in 1..12) {
        "month must be in range [1, 12], but was $month"
    }
    require(day >= 1) {
        "day must be greater than or equal to 1, but was $day"
    }
    return LocalDate(
        year = year,
        month = month,
        day = day.coerceAtMost(daysInMonth(year, month))
    )
}

private fun dateRangeOrderedAdvice(): String =
    "If app-owned start/end inputs may arrive in either order, use DateRange.ordered(...) before " +
            "creating or selecting a DateRange."
