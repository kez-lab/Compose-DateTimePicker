package com.kez.picker.util

import kotlinx.datetime.LocalDateTime

/**
 * Calculate time based on hour, minute, format and period.
 *
 * @param hour The hour value (0-23 for 24-hour format, 1-12 for 12-hour format).
 * @param minute The minute value (0-59).
 * @param timeFormat The time format (12-hour or 24-hour).
 * @param period The time period (AM/PM) for 12-hour format.
 * @return A [LocalDateTime] instance with the calculated time.
 */
fun calculateTime(
    hour: Int,
    minute: Int,
    timeFormat: TimeFormat,
    period: TimePeriod? = null,
): LocalDateTime {
    val adjustHour = when (timeFormat) {
        TimeFormat.HOUR_12 -> {
            when (period) {
                TimePeriod.AM -> if (hour == 12) 0 else hour
                TimePeriod.PM -> if (hour == 12) 12 else hour + 12
                null -> hour
            }
        }

        TimeFormat.HOUR_24 -> hour
    }

    return LocalDateTime(
        year = currentYear,
        monthNumber = currentMonth,
        dayOfMonth = currentDate.dayOfMonth,
        hour = adjustHour.coerceIn(0, 23),
        minute = minute.coerceIn(0, 59)
    )
} 