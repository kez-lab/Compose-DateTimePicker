package com.kez.picker.util

import kotlinx.datetime.LocalDateTime

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