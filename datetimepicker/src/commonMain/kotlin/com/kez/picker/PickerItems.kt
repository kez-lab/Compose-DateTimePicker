package com.kez.picker

import com.kez.picker.date.YearMonth
import com.kez.picker.date.daysInMonth
import com.kez.picker.util.TimeFormat
import com.kez.picker.util.TimePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month
import kotlinx.datetime.number
import kotlin.math.abs

/**
 * Selectable item lists for [com.kez.picker.time.TimePicker].
 *
 * Lists must be non-empty, contain distinct values, stay within their documented ranges, and contain
 * the current state selection for the active time format.
 *
 * @param minuteItems Minute values available for selection. Values must be in 0..59.
 * @param hour24Items Hour values available when using 24-hour time. Values must be in 0..23.
 * @param hour12Items Display-hour values available when using 12-hour time. Values must be in 1..12.
 * @param periodItems AM/PM values available when using 12-hour time.
 * @see PickerDefaults.timePickerItems
 */
data class TimePickerItems(
    val minuteItems: List<Int>,
    val hour24Items: List<Int>,
    val hour12Items: List<Int>,
    val periodItems: List<TimePeriod>
) {
    /**
     * Returns whether [time] is directly selectable under [timeFormat].
     *
     * This predicate does not validate the item-list configuration; it only checks membership.
     */
    fun contains(time: LocalTime, timeFormat: TimeFormat = TimeFormat.HOUR_24): Boolean {
        if (time.minute !in minuteItems) return false
        return when (timeFormat) {
            TimeFormat.HOUR_24 -> time.hour in hour24Items
            TimeFormat.HOUR_12 ->
                displayHourFor(time.hour) in hour12Items &&
                        periodFor(time.hour) in periodItems
        }
    }

    /**
     * Returns the closest selectable time for [time] under [timeFormat].
     *
     * This is useful before calling [com.kez.picker.time.TimePickerState.selectTime] when app-owned
     * state can contain values outside custom picker lists.
     *
     * @throws IllegalArgumentException if the item lists needed by [timeFormat] are empty, contain
     * duplicates, or contain values outside their supported ranges.
     */
    fun coerceTime(time: LocalTime, timeFormat: TimeFormat = TimeFormat.HOUR_24): LocalTime {
        requireValid(timeFormat)
        val minute = minuteItems.closestTo(time.minute)
        return when (timeFormat) {
            TimeFormat.HOUR_24 -> LocalTime(
                hour = hour24Items.closestTo(time.hour),
                minute = minute
            )

            TimeFormat.HOUR_12 -> {
                val period = periodFor(time.hour).takeIf { it in periodItems } ?: periodItems.first()
                val displayHour = hour12Items.closestTo(displayHourFor(time.hour))
                LocalTime(
                    hour = hourOfDayFor(displayHour = displayHour, period = period),
                    minute = minute
                )
            }
        }
    }

    internal fun hourItemsFor(timeFormat: TimeFormat): List<Int> =
        when (timeFormat) {
            TimeFormat.HOUR_12 -> hour12Items
            TimeFormat.HOUR_24 -> hour24Items
        }

    private fun requireValid(timeFormat: TimeFormat) {
        minuteItems.requireIntItems(name = "TimePicker minuteItems", range = 0..59)
        when (timeFormat) {
            TimeFormat.HOUR_24 -> hour24Items.requireIntItems(
                name = "TimePicker hour24Items",
                range = 0..23
            )

            TimeFormat.HOUR_12 -> {
                hour12Items.requireIntItems(name = "TimePicker hour12Items", range = 1..12)
                periodItems.requireItems(name = "TimePicker periodItems")
            }
        }
    }
}

/**
 * Date constraints applied by [DatePickerItems].
 *
 * @param minDate The earliest selectable date, inclusive. Pass null to omit the lower bound.
 * @param maxDate The latest selectable date, inclusive. Pass null to omit the upper bound.
 */
data class DatePickerConstraints(
    val minDate: LocalDate? = null,
    val maxDate: LocalDate? = null
) {
    init {
        if (minDate != null && maxDate != null) {
            require(minDate <= maxDate) {
                "DatePicker minDate must be on or before maxDate. minDate=$minDate, maxDate=$maxDate"
            }
        }
    }

    /**
     * Returns whether [date] is inside the configured inclusive bounds.
     */
    fun contains(date: LocalDate): Boolean =
        (minDate == null || date >= minDate) &&
                (maxDate == null || date <= maxDate)

    internal val isUnbounded: Boolean
        get() = minDate == null && maxDate == null
}

/**
 * Selectable item lists for [com.kez.picker.date.DatePicker].
 *
 * Lists must be non-empty, contain distinct values, stay within their documented ranges, and contain
 * the current state selection. [dayItems] is filtered by the selected year/month maximum day and
 * [constraints] before rendering.
 *
 * @param yearItems Year values available for selection. Values must be in 1000..9999.
 * @param monthItems Month values available for selection. Values must be in 1..12.
 * @param dayItems Day values available for selection. Values must be in 1..31.
 * @param constraints Inclusive date bounds applied after the year, month, and day item lists.
 * @see PickerDefaults.datePickerItems
 */
data class DatePickerItems(
    val yearItems: List<Int>,
    val monthItems: List<Int>,
    val dayItems: List<Int>,
    val constraints: DatePickerConstraints = DatePickerConstraints()
) {
    /**
     * Returns whether [date] is directly selectable.
     *
     * [dayItems] is checked after the selected year/month maximum day and [constraints] are applied.
     */
    fun contains(date: LocalDate): Boolean {
        val month = date.month.number
        return date.year in yearItems &&
                month in monthItems &&
                date.day <= daysInMonth(date.year, month) &&
                date.day in dayItems &&
                constraints.contains(date)
    }

    /**
     * Returns the closest selectable date for [date].
     *
     * The year and month are coerced to the closest configured values, then [dayItems] is filtered by
     * that coerced year/month maximum day before the day is coerced.
     *
     * @throws IllegalArgumentException if the configured item lists are empty, contain duplicates,
     * contain values outside their supported ranges, or cannot provide at least one valid day for every
     * selectable year/month combination.
     */
    fun coerceDate(date: LocalDate): LocalDate {
        requireValid()
        val year = selectableYearItems().closestTo(date.year)
        val month = selectableMonthItemsFor(year).closestTo(date.month.number)
        val day = selectableDayItemsFor(year = year, month = month).closestTo(date.day)
        return LocalDate(year = year, month = monthFor(month), day = day)
    }

    internal fun selectableYearItems(): List<Int> =
        yearItems.filter { year ->
            monthItems.any { month -> selectableDayItemsFor(year = year, month = month).isNotEmpty() }
        }

    internal fun selectableMonthItemsFor(year: Int): List<Int> =
        monthItems.filter { month ->
            selectableDayItemsFor(year = year, month = month).isNotEmpty()
        }

    internal fun selectableDayItemsFor(year: Int, month: Int): List<Int> =
        dayItems.filter { day ->
            day <= daysInMonth(year, month) &&
                    constraints.contains(LocalDate(year = year, month = monthFor(month), day = day))
        }

    private fun requireValid() {
        yearItems.requireIntItems(name = "DatePicker yearItems", range = 1000..9999)
        monthItems.requireIntItems(name = "DatePicker monthItems", range = 1..12)
        dayItems.requireIntItems(name = "DatePicker dayItems", range = 1..31)
        if (constraints.isUnbounded) {
            val minimumMaxDay = monthItems.minOf { month ->
                when (month) {
                    2 -> if (yearItems.any { daysInMonth(it, month) == 28 }) 28 else 29
                    4, 6, 9, 11 -> 30
                    else -> 31
                }
            }
            require(dayItems.any { it <= minimumMaxDay }) {
                "DatePicker dayItems must contain at least one day valid for every selectable " +
                        "year/month combination. Smallest maximum day is $minimumMaxDay."
            }
        }
        require(selectableYearItems().isNotEmpty()) {
            "DatePicker items must contain at least one date allowed by constraints."
        }
    }
}

/**
 * Selectable item lists for [com.kez.picker.date.YearMonthPicker].
 *
 * Lists must be non-empty, contain distinct values, stay within their documented ranges, and contain
 * the current state selection.
 *
 * @param yearItems Year values available for selection. Values must be in 1000..9999.
 * @param monthItems Month values available for selection. Values must be in 1..12.
 * @see PickerDefaults.yearMonthPickerItems
 */
data class YearMonthPickerItems(
    val yearItems: List<Int>,
    val monthItems: List<Int>
) {
    /**
     * Returns whether [yearMonth] is directly selectable.
     */
    fun contains(yearMonth: YearMonth): Boolean =
        contains(year = yearMonth.year, month = yearMonth.month)

    /**
     * Returns whether [year] and [month] are directly selectable.
     */
    fun contains(year: Int, month: Int): Boolean =
        year in yearItems && month in monthItems

    /**
     * Returns whether the year/month portion of [date] is directly selectable.
     */
    fun contains(date: LocalDate): Boolean =
        contains(YearMonth.from(date))

    /**
     * Returns the closest selectable [YearMonth] for [yearMonth].
     *
     * @throws IllegalArgumentException if [yearItems] or [monthItems] are empty, contain duplicates,
     * or contain values outside their supported ranges.
     */
    fun coerceYearMonth(yearMonth: YearMonth): YearMonth =
        coerceYearMonth(year = yearMonth.year, month = yearMonth.month)

    /**
     * Returns the closest selectable [YearMonth] for [year] and [month].
     */
    fun coerceYearMonth(year: Int, month: Int): YearMonth {
        requireValid()
        return YearMonth(
            year = yearItems.closestTo(year),
            month = monthItems.closestTo(month)
        )
    }

    /**
     * Returns the closest selectable [YearMonth] for the year/month portion of [date].
     */
    fun coerceDate(date: LocalDate): LocalDate =
        coerceYearMonth(YearMonth.from(date)).atDay()

    private fun requireValid() {
        yearItems.requireIntItems(name = "YearMonthPicker yearItems", range = 1000..9999)
        monthItems.requireIntItems(name = "YearMonthPicker monthItems", range = 1..12)
    }
}

private fun <T> List<T>.requireItems(name: String) {
    require(isNotEmpty()) { "$name must not be empty." }
    require(distinct().size == size) { "$name must not contain duplicate values." }
}

private fun List<Int>.requireIntItems(name: String, range: IntRange) {
    requireItems(name)
    val invalidValues = filterNot { it in range }.distinct()
    require(invalidValues.isEmpty()) {
        "$name must contain only values in range [${range.first}, ${range.last}]. " +
                "Invalid values: $invalidValues"
    }
}

private fun List<Int>.closestTo(value: Int): Int =
    minWith(compareBy<Int> { abs(it - value) }.thenBy { it })

private fun displayHourFor(hourOfDay: Int): Int {
    val hour = hourOfDay % 12
    return if (hour == 0) 12 else hour
}

private fun monthFor(monthNumber: Int): Month =
    Month.entries.first { it.number == monthNumber }

private fun periodFor(hourOfDay: Int): TimePeriod =
    if (hourOfDay >= 12) TimePeriod.PM else TimePeriod.AM

private fun hourOfDayFor(displayHour: Int, period: TimePeriod): Int =
    when {
        period == TimePeriod.AM && displayHour == 12 -> 0
        period == TimePeriod.PM && displayHour != 12 -> displayHour + 12
        else -> displayHour
    }
