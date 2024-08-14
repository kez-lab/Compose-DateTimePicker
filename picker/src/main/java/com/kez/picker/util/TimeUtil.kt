package com.kez.picker.util

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

internal val YEAR_RANGE = (1000..9999).map { it.toString() }
internal val MONTH_RANGE = (1..12).map { it.toString() }

internal val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

internal val currentYear = currentTime.year
internal val currentMonth = currentTime.monthNumber