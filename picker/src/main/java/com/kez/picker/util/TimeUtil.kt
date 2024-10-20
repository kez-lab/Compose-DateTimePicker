package com.kez.picker.util

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

internal val YEAR_RANGE = (1000..9999).toList()
internal val MONTH_RANGE = (1..12).toList()

internal val HOUR24_RANGE = (0..23).toList()
internal val HOUR12_RANGE = (1..12).toList()
internal val MINUTE_RANGE = (0..59).toList()

internal val currentDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

internal val currentDate = currentDateTime.date
internal val currentYear = currentDateTime.year
internal val currentMonth = currentDateTime.monthNumber
internal val currentMinute = currentDateTime.minute
internal val currentHour = currentDateTime.hour

enum class TimeFormat {
    HOUR_12, HOUR_24
}

enum class TimePeriod {
    AM, PM
}