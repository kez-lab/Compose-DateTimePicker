package com.kez.picker.time

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.kez.picker.TimePickerItems
import com.kez.picker.util.TimeFormat
import com.kez.picker.util.TimePeriod
import com.kez.picker.util.currentDateTime
import kotlinx.datetime.LocalTime

/**
 * Creates and remembers a [TimePickerState].
 * Initial time values are read when the state is first created.
 *
 * @param initialHour The initial hour to be selected.
 * If [timeFormat] is [TimeFormat.HOUR_12], this value is automatically adjusted to the 12-hour format (1-12).
 * @param initialMinute The initial minute to be selected.
 * @param initialPeriod The initial period (AM/PM) to be selected. Defaults to the current period based on the current hour.
 * @param timeFormat The time format (12-hour or 24-hour). Defaults to [TimeFormat.HOUR_24].
 * @return A [TimePickerState] initialized with the given time values.
 */
@Composable
fun rememberTimePickerState(
    initialHour: Int,
    initialMinute: Int,
    initialPeriod: TimePeriod = if (initialHour >= 12) TimePeriod.PM else TimePeriod.AM,
    timeFormat: TimeFormat = TimeFormat.HOUR_24
): TimePickerState {
    val rememberedInitialHour = remember { initialHour }
    val rememberedInitialMinute = remember { initialMinute }
    val rememberedInitialPeriod = remember { initialPeriod }
    val adjustedHour = remember(rememberedInitialHour, timeFormat) {
        initialHourForTimeFormat(rememberedInitialHour, timeFormat)
    }
    val saver = remember(timeFormat) { timePickerStateSaver(timeFormat) }
    return rememberSaveable(
        timeFormat,
        saver = saver
    ) {
        TimePickerState(
            initialHour = adjustedHour,
            initialMinute = rememberedInitialMinute,
            initialPeriod = rememberedInitialPeriod,
            timeFormat = timeFormat,
        )
    }
}

/**
 * Creates and remembers a [TimePickerState] from a [LocalTime].
 *
 * When [timeFormat] is [TimeFormat.HOUR_12], the hour is converted to the display-hour range (1-12)
 * and the AM/PM period is derived from [initialTime].
 *
 * @param initialTime The initial time to be selected.
 * @param timeFormat The time format (12-hour or 24-hour). Defaults to [TimeFormat.HOUR_24].
 * @return A [TimePickerState] initialized from [initialTime].
 */
@Composable
fun rememberTimePickerState(
    initialTime: LocalTime = currentDateTime().time,
    timeFormat: TimeFormat = TimeFormat.HOUR_24
): TimePickerState {
    return rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
        initialPeriod = if (initialTime.hour >= 12) TimePeriod.PM else TimePeriod.AM,
        timeFormat = timeFormat
    )
}

/**
 * Creates and remembers a [TimePickerState] whose initial value is coerced by [items].
 *
 * Initial values and [items] are read when the state is first created. This is useful when the
 * picker is rendered with custom item lists and restored app state may fall outside those lists.
 *
 * @param items Selectable values used to coerce [initialTime] before creating the state.
 * @param initialTime The requested initial time.
 * @param timeFormat The time format (12-hour or 24-hour). Defaults to [TimeFormat.HOUR_24].
 * @return A [TimePickerState] initialized to the closest selectable time.
 */
@Composable
fun rememberTimePickerState(
    items: TimePickerItems,
    initialTime: LocalTime = currentDateTime().time,
    timeFormat: TimeFormat = TimeFormat.HOUR_24
): TimePickerState {
    val rememberedInitialTime = remember { initialTime }
    val rememberedItems = remember { items }
    val coercedInitialTime = remember(rememberedInitialTime, rememberedItems, timeFormat) {
        rememberedItems.coerceTime(
            time = rememberedInitialTime,
            timeFormat = timeFormat
        )
    }
    return rememberTimePickerState(
        initialTime = coercedInitialTime,
        timeFormat = timeFormat
    )
}

internal fun initialHourForTimeFormat(initialHour: Int, timeFormat: TimeFormat): Int {
    require(initialHour in 0..23) {
        "initialHour must be in range [0, 23], but was $initialHour"
    }
    return if (timeFormat == TimeFormat.HOUR_12) {
        val hour = initialHour % 12
        if (hour == 0) 12 else hour
    } else {
        initialHour
    }
}

private fun timePickerStateSaver(timeFormat: TimeFormat): Saver<TimePickerState, Any> {
    return listSaver(
        save = { listOf(it.selectedHourOfDay, it.selectedMinute) },
        restore = {
            val restoredHourOfDay = it[0] as Int
            TimePickerState(
                initialHour = initialHourForTimeFormat(restoredHourOfDay, timeFormat),
                initialMinute = it[1] as Int,
                initialPeriod = if (restoredHourOfDay >= 12) TimePeriod.PM else TimePeriod.AM,
                timeFormat = timeFormat
            )
        }
    )
}

/**
 * State holder for the [TimePicker].
 *
 * Manages the state of the hour, minute, and period (AM/PM) pickers.
 * Internal picker states are not directly accessible to prevent inconsistent state modifications.
 *
 * @param initialHour The initial hour to be selected.
 * @param initialMinute The initial minute to be selected.
 * @param initialPeriod The initial period (AM/PM) to be selected.
 * @param timeFormat The time format (12-hour or 24-hour).
 */
@Stable
class TimePickerState(
    initialHour: Int,
    initialMinute: Int,
    initialPeriod: TimePeriod,
    val timeFormat: TimeFormat
) {
    /**
     * Creates a [TimePickerState] from a [LocalTime].
     *
     * When [timeFormat] is [TimeFormat.HOUR_12], the hour is converted to the display-hour range
     * and the AM/PM period is derived from [initialTime].
     */
    constructor(
        initialTime: LocalTime,
        timeFormat: TimeFormat = TimeFormat.HOUR_24
    ) : this(
        initialHour = initialHourForTimeFormat(initialTime.hour, timeFormat),
        initialMinute = initialTime.minute,
        initialPeriod = if (initialTime.hour >= 12) TimePeriod.PM else TimePeriod.AM,
        timeFormat = timeFormat
    )

    init {
        require(initialMinute in 0..59) {
            "initialMinute must be in range [0, 59], but was $initialMinute"
        }
        val hourRange = if (timeFormat == TimeFormat.HOUR_12) 1..12 else 0..23
        val hourRangeLabel = if (timeFormat == TimeFormat.HOUR_12) "1, 12" else "0, 23"
        require(initialHour in hourRange) {
            "initialHour must be in range [$hourRangeLabel], but was $initialHour"
        }
    }

    private var mutableSelectedHour: Int by mutableStateOf(initialHour)
    private var mutableSelectedMinute: Int by mutableStateOf(initialMinute)
    private var mutableSelectedPeriod: TimePeriod by mutableStateOf(initialPeriod)

    /**
     * The currently selected hour.
     * For 12-hour format: 1-12, for 24-hour format: 0-23.
     */
    val selectedHour: Int
        get() = mutableSelectedHour

    /**
     * The currently selected minute (0-59).
     */
    val selectedMinute: Int
        get() = mutableSelectedMinute

    /**
     * The currently selected period (AM/PM).
     * Only relevant when using 12-hour format.
     */
    val selectedPeriod: TimePeriod
        get() = mutableSelectedPeriod

    /**
     * The selected hour converted to 24-hour clock time (0-23).
     */
    val selectedHourOfDay: Int
        get() = when (timeFormat) {
            TimeFormat.HOUR_24 -> selectedHour
            TimeFormat.HOUR_12 -> when {
                selectedPeriod == TimePeriod.AM && selectedHour == 12 -> 0
                selectedPeriod == TimePeriod.PM && selectedHour != 12 -> selectedHour + 12
                else -> selectedHour
            }
        }

    /**
     * The selected time represented as [LocalTime].
     */
    val selectedTime: LocalTime
        get() = LocalTime(selectedHourOfDay, selectedMinute)

    /**
     * Programmatically selects [time].
     *
     * The hour is converted to the current [timeFormat]. In 12-hour mode, the AM/PM period is derived from
     * [time]. In 24-hour mode, [selectedPeriod] is still updated for consistency but is not displayed by
     * [TimePicker].
     */
    fun selectTime(time: LocalTime) {
        mutableSelectedHour = initialHourForTimeFormat(time.hour, timeFormat)
        mutableSelectedMinute = time.minute
        mutableSelectedPeriod = if (time.hour >= 12) TimePeriod.PM else TimePeriod.AM
    }

    /**
     * Programmatically selects the closest time to [time] that is allowed by [items].
     *
     * Use this overload when app-owned state can contain values outside custom picker lists.
     */
    fun selectTime(time: LocalTime, items: TimePickerItems) {
        selectTime(items.coerceTime(time = time, timeFormat = timeFormat))
    }

    internal fun selectHour(hour: Int) {
        val hourRange = if (timeFormat == TimeFormat.HOUR_12) 1..12 else 0..23
        val hourRangeLabel = if (timeFormat == TimeFormat.HOUR_12) "1..12" else "0..23"
        require(hour in hourRange) {
            "hour must be in range $hourRangeLabel for timeFormat=$timeFormat, but was $hour"
        }
        mutableSelectedHour = hour
    }

    internal fun selectMinute(minute: Int) {
        require(minute in 0..59) {
            "minute must be in range 0..59, but was $minute"
        }
        mutableSelectedMinute = minute
    }

    internal fun selectPeriod(period: TimePeriod) {
        mutableSelectedPeriod = period
    }

    companion object {
        /**
         * Saves and restores [TimePickerState] across configuration changes.
         */
        val Saver: Saver<TimePickerState, Any> = listSaver(
            save = {
                listOf(
                    it.selectedHour,
                    it.selectedMinute,
                    it.selectedPeriod.name,
                    it.timeFormat.name
                )
            },
            restore = {
                TimePickerState(
                    initialHour = it[0] as Int,
                    initialMinute = it[1] as Int,
                    initialPeriod = TimePeriod.valueOf(it[2] as String),
                    timeFormat = TimeFormat.valueOf(it[3] as String)
                )
            }
        )
    }
}
