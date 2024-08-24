package com.kez.picker.util

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

internal val YEAR_RANGE = (1000..9999).map { it.toString() }
internal val MONTH_RANGE = (1..12).map { it.toString() }

internal val HOUR_RANGE = (1..12).map { it.toString() }
internal val MINUTE_RANGE = (0..59).map { it.toString() }

internal val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

internal val currentYear = currentTime.year
internal val currentMonth = currentTime.monthNumber
internal val currentMinute = currentTime.minute
internal val currentHour = currentTime.hour