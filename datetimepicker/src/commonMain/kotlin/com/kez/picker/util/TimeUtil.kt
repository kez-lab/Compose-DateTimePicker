@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.kez.picker.util

import kotlin.time.Clock
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
 * Current date and time.
 */
val currentDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

/**
 * Current date.
 */
val currentDate = currentDateTime.date

/**
 * Current year.
 */
val currentYear = currentDateTime.year

/**
 * Current month number (1-12).
 */
val currentMonth = currentDateTime.month.number

/**
 * Current minute (0-59).
 */
val currentMinute = currentDateTime.minute

/**
 * Current hour (0-23).
 */
val currentHour = currentDateTime.hour

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