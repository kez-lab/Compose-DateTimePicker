package com.kez.picker.duration

import com.kez.picker.DurationPickerColumn
import com.kez.picker.DurationPickerItems
import com.kez.picker.MINUTES_PER_HOUR
import com.kez.picker.closestPickerValueTo
import com.kez.picker.durationPickerValue
import kotlin.time.Duration

/** Applies one settled column value and repairs the result as one selectable scalar duration. */
internal fun DurationPickerItems.repairedDurationAfter(
    currentDuration: Duration,
    column: DurationPickerColumn,
    value: Int
): Duration {
    val currentTotalMinutes = currentDuration.inWholeMinutes
    val currentHours = (currentTotalMinutes / MINUTES_PER_HOUR).toInt()
    val currentMinutes = (currentTotalMinutes % MINUTES_PER_HOUR).toInt()

    return when (column) {
        DurationPickerColumn.HOUR -> {
            if (value !in hourItems) return currentDuration
            val activeMinutes = selectableMinuteItemsFor(value)
            if (activeMinutes.isEmpty()) return currentDuration
            val nextMinutes = activeMinutes.closestPickerValueTo(
                value = currentMinutes,
                sourceName = "DurationPicker dependent minute items for hours=$value"
            )
            durationPickerValue(hours = value, minutes = nextMinutes)
        }

        DurationPickerColumn.MINUTE -> {
            if (value !in selectableMinuteItemsFor(currentHours)) return currentDuration
            durationPickerValue(hours = currentHours, minutes = value)
        }
    }
}
