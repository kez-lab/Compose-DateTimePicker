package com.kez.picker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.kez.picker.util.TimeFormat
import com.kez.picker.util.TimePeriod
import com.kez.picker.util.currentDate
import com.kez.picker.util.currentHour
import com.kez.picker.util.currentMinute
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.number

/**
 * Remember a [PickerState] with the given initial item.
 *
 * @param initialItem The initial selected item.
 * @return A [PickerState] with the given initial item.
 */
@Composable
fun <T> rememberPickerState(initialItem: T) = remember { PickerState(initialItem) }

/**
 * State holder for the picker component.
 *
 * The [selectedItem] property is read-only from external code.
 * Item selection is managed internally by the [Picker] component through scrolling.
 *
 * @param initialItem The initial selected item.
 */
@Stable
class PickerState<T>(
    initialItem: T
) {
    /**
     * The currently selected item.
     * This value is updated internally when the user scrolls the picker.
     */
    var selectedItem: T by mutableStateOf(initialItem)
        internal set
}

/**
 * Creates and remembers a [YearMonthPickerState].
 * Initial year and month are read when the state is first created.
 *
 * @param initialYear The initial year to be selected. Defaults to the current year.
 * @param initialMonth The initial month to be selected. Defaults to the current month.
 * @return A [YearMonthPickerState] initialized with the given year and month.
 */
@Composable
fun rememberYearMonthPickerState(
    initialYear: Int = currentDate().year,
    initialMonth: Int = currentDate().month.number
): YearMonthPickerState {
    val rememberedInitialYear = remember { initialYear }
    val rememberedInitialMonth = remember { initialMonth }
    return rememberSaveable(saver = YearMonthPickerState.Saver) {
        YearMonthPickerState(rememberedInitialYear, rememberedInitialMonth)
    }
}

/**
 * Creates and remembers a [YearMonthPickerState] from a [LocalDate].
 *
 * The day value is ignored because [com.kez.picker.date.YearMonthPicker] only selects year and month.
 *
 * @param initialDate The initial date whose year and month should be selected.
 * @return A [YearMonthPickerState] initialized with the year and month from [initialDate].
 * @throws IllegalArgumentException if [initialDate]'s year is outside the supported 1000..9999 range.
 */
@Composable
fun rememberYearMonthPickerState(initialDate: LocalDate): YearMonthPickerState {
    return rememberYearMonthPickerState(
        initialYear = initialDate.year,
        initialMonth = initialDate.month.number
    )
}

/**
 * State holder for the [com.kez.picker.date.YearMonthPicker].
 *
 * Manages the state of the year and month pickers.
 * Internal picker states are not directly accessible to prevent inconsistent state modifications.
 *
 * @param initialYear The initial year to be selected. Must be in 1000..9999.
 * @param initialMonth The initial month to be selected.
 * @throws IllegalArgumentException if [initialYear] or [initialMonth] is outside the supported range.
 */
@Stable
class YearMonthPickerState(
    initialYear: Int,
    initialMonth: Int
) {
    internal val yearState = PickerState(initialYear)
    internal val monthState = PickerState(initialMonth)

    init {
        require(initialYear in 1000..9999) {
            "initialYear must be in range [1000, 9999], but was $initialYear"
        }
        require(initialMonth in 1..12) {
            "initialMonth must be in range [1, 12], but was $initialMonth"
        }
    }

    /**
     * The currently selected year.
     */
    val selectedYear: Int
        get() = yearState.selectedItem

    /**
     * The currently selected month (1-12).
     */
    val selectedMonth: Int
        get() = monthState.selectedItem

    /**
     * The selected year and month represented as the first day of that month.
     */
    val selectedMonthDate: LocalDate
        get() = LocalDate(selectedYear, selectedMonth, 1)

    companion object {
        /**
         * Saves and restores [YearMonthPickerState] across configuration changes.
         */
        val Saver: Saver<YearMonthPickerState, Any> = listSaver(
            save = { listOf(it.selectedYear, it.selectedMonth) },
            restore = {
                YearMonthPickerState(
                    initialYear = it[0] as Int,
                    initialMonth = it[1] as Int
                )
            }
        )
    }
}

/**
 * Creates and remembers a [TimePickerState].
 * Initial time values are read when the state is first created.
 *
 * @param initialHour The initial hour to be selected. Defaults to the current hour.
 * If [timeFormat] is [TimeFormat.HOUR_12], this value is automatically adjusted to the 12-hour format (1-12).
 * @param initialMinute The initial minute to be selected. Defaults to the current minute.
 * @param initialPeriod The initial period (AM/PM) to be selected. Defaults to the current period based on the current hour.
 * @param timeFormat The time format (12-hour or 24-hour). Defaults to [TimeFormat.HOUR_24].
 * @return A [TimePickerState] initialized with the given time values.
 */
@Composable
fun rememberTimePickerState(
    initialHour: Int = currentHour(),
    initialMinute: Int = currentMinute(),
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
    initialTime: LocalTime,
    timeFormat: TimeFormat = TimeFormat.HOUR_24
): TimePickerState {
    return rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
        initialPeriod = if (initialTime.hour >= 12) TimePeriod.PM else TimePeriod.AM,
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
    internal val hourState = PickerState(initialHour)
    internal val minuteState = PickerState(initialMinute)
    internal val periodState = PickerState(initialPeriod)

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

    /**
     * The currently selected hour.
     * For 12-hour format: 1-12, for 24-hour format: 0-23.
     */
    val selectedHour: Int
        get() = hourState.selectedItem

    /**
     * The currently selected minute (0-59).
     */
    val selectedMinute: Int
        get() = minuteState.selectedItem

    /**
     * The currently selected period (AM/PM).
     * Only relevant when using 12-hour format.
     */
    val selectedPeriod: TimePeriod
        get() = periodState.selectedItem

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
