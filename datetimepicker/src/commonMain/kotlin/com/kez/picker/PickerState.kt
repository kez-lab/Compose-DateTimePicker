package com.kez.picker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.kez.picker.util.TimePeriod
import com.kez.picker.util.currentDate
import com.kez.picker.util.currentHour
import com.kez.picker.util.currentMinute
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
 * @param initialItem The initial selected item.
 */
@Stable
class PickerState<T>(
    initialItem: T
) {
    /**
     * The currently selected item.
     */
    var selectedItem by mutableStateOf(initialItem)
}

/**
 * Creates and remembers a [YearMonthPickerState].
 *
 * @param initialYear The initial year to be selected. Defaults to the current year.
 * @param initialMonth The initial month to be selected. Defaults to the current month.
 * @return A [YearMonthPickerState] initialized with the given year and month.
 */
@Composable
fun rememberYearMonthPickerState(
    initialYear: Int = currentDate.year,
    initialMonth: Int = currentDate.month.number
): YearMonthPickerState {
    return remember(initialYear, initialMonth) {
        YearMonthPickerState(initialYear, initialMonth)
    }
}

/**
 * State holder for the [YearMonthPicker].
 *
 * Manages the state of the year and month pickers.
 *
 * @param initialYear The initial year to be selected.
 * @param initialMonth The initial month to be selected.
 */
@Stable
class YearMonthPickerState(
    initialYear: Int,
    initialMonth: Int
) {
    val yearState = PickerState(initialYear)
    val monthState = PickerState(initialMonth)

    val selectedYear: Int
        get() = yearState.selectedItem

    val selectedMonth: Int
        get() = monthState.selectedItem
}

/**
 * Creates and remembers a [TimePickerState].
 *
 * @param initialHour The initial hour to be selected. Defaults to the current hour.
 * @param initialMinute The initial minute to be selected. Defaults to the current minute.
 * @param initialPeriod The initial period (AM/PM) to be selected. Defaults to the current period based on the current hour.
 * @return A [TimePickerState] initialized with the given time values.
 */
@Composable
fun rememberTimePickerState(
    initialHour: Int = currentHour,
    initialMinute: Int = currentMinute,
    initialPeriod: TimePeriod = if (currentHour >= 12) TimePeriod.PM else TimePeriod.AM
): TimePickerState {
    return remember(initialHour, initialMinute, initialPeriod) {
        TimePickerState(initialHour, initialMinute, initialPeriod)
    }
}

/**
 * State holder for the [TimePicker].
 *
 * Manages the state of the hour, minute, and period (AM/PM) pickers.
 *
 * @param initialHour The initial hour to be selected.
 * @param initialMinute The initial minute to be selected.
 * @param initialPeriod The initial period (AM/PM) to be selected.
 */
@Stable
class TimePickerState(
    initialHour: Int,
    initialMinute: Int,
    initialPeriod: TimePeriod
) {
    val hourState = PickerState(initialHour)
    val minuteState = PickerState(initialMinute)
    val periodState = PickerState(initialPeriod)

    val selectedHour: Int
        get() = hourState.selectedItem

    val selectedMinute: Int
        get() = minuteState.selectedItem

    val selectedPeriod: TimePeriod
        get() = periodState.selectedItem
}