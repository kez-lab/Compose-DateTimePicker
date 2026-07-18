package com.kez.picker.time

import com.kez.picker.TimePickerItems
import com.kez.picker.closestPickerValueTo
import com.kez.picker.util.TimeFormat
import com.kez.picker.util.TimePeriod
import kotlinx.datetime.LocalTime

/**
 * Applies one TimePicker hour-column change and repairs every dependent part as one logical value.
 */
internal fun TimePickerItems.repairedTimeAfterHour(
    currentTime: LocalTime,
    timeFormat: TimeFormat,
    hour: Int
): LocalTime {
    val currentPeriod = periodFor(currentTime)
    if (hour !in selectableHourItemsFor(timeFormat = timeFormat, period = currentPeriod)) {
        return currentTime
    }
    return repairedTimeAfter(
        currentTime = currentTime,
        timeFormat = timeFormat,
        requestedHour = hour
    )
}

/**
 * Applies one TimePicker minute-column change and repairs every dependent part as one logical value.
 */
internal fun TimePickerItems.repairedTimeAfterMinute(
    currentTime: LocalTime,
    timeFormat: TimeFormat,
    minute: Int
): LocalTime {
    if (minute !in selectableMinuteItemsFor(hourOfDay = currentTime.hour)) return currentTime
    return repairedTimeAfter(
        currentTime = currentTime,
        timeFormat = timeFormat,
        requestedMinute = minute
    )
}

/**
 * Applies one TimePicker period-column change and repairs every dependent part as one logical value.
 */
internal fun TimePickerItems.repairedTimeAfterPeriod(
    currentTime: LocalTime,
    period: TimePeriod
): LocalTime {
    if (period !in selectablePeriodItems()) return currentTime
    return repairedTimeAfter(
        currentTime = currentTime,
        timeFormat = TimeFormat.HOUR_12,
        requestedPeriod = period
    )
}

private fun TimePickerItems.repairedTimeAfter(
    currentTime: LocalTime,
    timeFormat: TimeFormat,
    requestedHour: Int? = null,
    requestedMinute: Int? = null,
    requestedPeriod: TimePeriod? = null
): LocalTime {
    val nextPeriod = when (timeFormat) {
        TimeFormat.HOUR_24 -> TimePeriod.AM
        TimeFormat.HOUR_12 -> {
            val availablePeriods = selectablePeriodItems()
            require(availablePeriods.isNotEmpty()) {
                "TimePicker dependent period items must contain at least one selectable value."
            }
            val period = requestedPeriod ?: periodFor(currentTime)
            period.takeIf(availablePeriods::contains) ?: availablePeriods.first()
        }
    }
    val availableHours = selectableHourItemsFor(
        timeFormat = timeFormat,
        period = nextPeriod
    )
    val hour = requestedHour ?: when (timeFormat) {
            TimeFormat.HOUR_24 -> currentTime.hour
            TimeFormat.HOUR_12 -> displayHourFor(currentTime)
    }
    val nextHour = availableHours.closestPickerValueTo(
        value = hour,
        sourceName = "TimePicker dependent hour items for timeFormat=$timeFormat, period=$nextPeriod"
    )
    val nextHourOfDay = when (timeFormat) {
        TimeFormat.HOUR_24 -> nextHour
        TimeFormat.HOUR_12 -> hourOfDay(displayHour = nextHour, period = nextPeriod)
    }
    val availableMinutes = selectableMinuteItemsFor(hourOfDay = nextHourOfDay)
    val minute = requestedMinute ?: currentTime.minute
    val nextMinute = availableMinutes.closestPickerValueTo(
        value = minute,
        sourceName = "TimePicker dependent minute items for hourOfDay=$nextHourOfDay"
    )
    return LocalTime(hour = nextHourOfDay, minute = nextMinute)
}

private fun displayHourFor(time: LocalTime): Int = (time.hour % 12).let { hour ->
    if (hour == 0) 12 else hour
}

private fun periodFor(time: LocalTime): TimePeriod =
    if (time.hour >= 12) TimePeriod.PM else TimePeriod.AM

private fun hourOfDay(displayHour: Int, period: TimePeriod): Int {
    require(displayHour in 1..12) {
        "displayHour must be in range [1, 12], but was $displayHour"
    }
    return when {
        period == TimePeriod.AM && displayHour == 12 -> 0
        period == TimePeriod.PM && displayHour != 12 -> displayHour + 12
        else -> displayHour
    }
}
