@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.kez.picker.util

import kotlin.time.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime

/**
 * Range of years for year picker (1000-9999).
 */
val YEAR_RANGE = (1000..9999).toList()

/**
 * Range of months for month picker (1-12).
 */
val MONTH_RANGE = (1..12).toList()

/**
 * Range of days for date picker (1-31).
 */
val DAY_RANGE = (1..31).toList()

/**
 * Range of hours for 24-hour format (0-23).
 */
val HOUR24_RANGE = (0..23).toList()

/**
 * Range of hours for 12-hour format (1-12).
 */
val HOUR12_RANGE = (1..12).toList()

/**
 * Range of minutes (0-59).
 */
val MINUTE_RANGE = (0..59).toList()

/**
 * Returns the current date and time.
 * This function is called each time to get the actual current time,
 * preventing stale time values in long-running applications.
 *
 * @return The current [LocalDateTime] in the system's default timezone.
 */
fun currentDateTime(): LocalDateTime =
    Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

/**
 * Returns the current date.
 *
 * @return The current [LocalDate].
 */
fun currentDate(): LocalDate = currentDateTime().date

/**
 * Returns the current year.
 *
 * @return The current year as an [Int].
 */
fun currentYear(): Int = currentDateTime().year

/**
 * Returns the current month number (1-12).
 *
 * @return The current month number as an [Int].
 */
fun currentMonth(): Int = currentDateTime().month.number

/**
 * Returns the current minute (0-59).
 *
 * @return The current minute as an [Int].
 */
fun currentMinute(): Int = currentDateTime().minute

/**
 * Returns the current hour (0-23).
 *
 * @return The current hour as an [Int].
 */
fun currentHour(): Int = currentDateTime().hour

/**
 * Time format for time picker.
 */
enum class TimeFormat {
    /**
     * 12-hour format (AM/PM).
     */
    HOUR_12,

    /**
     * 24-hour format.
     */
    HOUR_24
}

/**
 * Time period for 12-hour format.
 */
enum class TimePeriod {
    /**
     * AM period (Ante Meridiem).
     */
    AM,

    /**
     * PM period (Post Meridiem).
     */
    PM
}
