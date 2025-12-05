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

@Composable
fun rememberYearMonthPickerState(
    initialYear: Int = currentDate.year,
    initialMonth: Int = currentDate.month.number
): YearMonthPickerState {
    return remember(initialYear, initialMonth) {
        YearMonthPickerState(initialYear, initialMonth)
    }
}

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