package com.kez.picker.duration

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.kez.picker.DurationPickerItems
import com.kez.picker.MINUTES_PER_HOUR
import com.kez.picker.durationPickerValue
import com.kez.picker.requireDurationPickerValue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

/**
 * Creates and remembers a [DurationPickerState].
 *
 * [initialDuration] is read only when the state is first created.
 *
 * @param initialDuration Initial finite, non-negative, whole-minute duration.
 * @return A saveable duration picker state.
 * @throws IllegalArgumentException if [initialDuration] is outside the supported duration domain.
 */
@Composable
fun rememberDurationPickerState(
    initialDuration: Duration = Duration.ZERO
): DurationPickerState {
    val rememberedInitialDuration = remember { initialDuration }
    return rememberSaveable(saver = DurationPickerState.Saver) {
        DurationPickerState(initialDuration = rememberedInitialDuration)
    }
}

/**
 * Creates and remembers a [DurationPickerState] normalized by [items].
 *
 * The initial value and item source are read when the state is first created. On recreation, the
 * saved duration is coerced by the same item source so restored state remains selectable.
 *
 * @param items Selectable hour/minute sources and scalar bounds.
 * @param initialDuration Requested initial finite, non-negative, whole-minute duration.
 * @return A state initialized to the closest selectable duration.
 * @throws IllegalArgumentException if the requested value or item configuration is invalid or the
 * item configuration has no selectable scalar combination.
 */
@Composable
fun rememberDurationPickerState(
    items: DurationPickerItems,
    initialDuration: Duration = Duration.ZERO
): DurationPickerState {
    val rememberedItems = remember { items }
    val rememberedInitialDuration = remember { initialDuration }
    val coercedInitialDuration = remember(rememberedItems, rememberedInitialDuration) {
        rememberedItems.coerceDuration(rememberedInitialDuration)
    }
    val saver = remember(rememberedItems) { durationPickerStateSaver(rememberedItems) }
    return rememberSaveable(saver = saver) {
        DurationPickerState(initialDuration = coercedInitialDuration)
    }
}

/**
 * Saveable logical state for [DurationPicker].
 *
 * The state owns one scalar [selectedDuration]. [selectedHours] and [selectedMinutes] are derived
 * views of that value, so programmatic multi-field changes cannot expose an intermediate state.
 *
 * @param initialDuration Initial finite, non-negative, whole-minute duration.
 * @throws IllegalArgumentException if [initialDuration] is outside the supported duration domain
 * or its elapsed whole-hour part does not fit in [Int].
 */
@Stable
class DurationPickerState(
    initialDuration: Duration = Duration.ZERO
) {
    /**
     * Creates state from elapsed [initialHours] and minute-within-hour [initialMinutes].
     *
     * @param initialHours Initial non-negative elapsed whole hours.
     * @param initialMinutes Initial minute-within-hour value in `0..59`.
     * @throws IllegalArgumentException if either part is outside its supported range.
     */
    constructor(initialHours: Int, initialMinutes: Int) : this(
        initialDuration = checkedDurationPickerValue(
            hours = initialHours,
            minutes = initialMinutes,
            hourName = "initialHours",
            minuteName = "initialMinutes"
        )
    )

    init {
        initialDuration.requireDurationPickerValue("initialDuration")
        require(initialDuration.inWholeMinutes / MINUTES_PER_HOUR <= Int.MAX_VALUE) {
            "initialDuration whole hours must fit in Int, but was $initialDuration."
        }
    }

    private var mutableSelectedDuration: Duration by mutableStateOf(initialDuration)

    /** The currently selected logical duration. */
    val selectedDuration: Duration
        get() = mutableSelectedDuration

    /** The elapsed whole-hour part of [selectedDuration]. */
    val selectedHours: Int
        get() = (selectedDuration.inWholeMinutes / MINUTES_PER_HOUR).toInt()

    /** The minute-within-hour part of [selectedDuration], in `0..59`. */
    val selectedMinutes: Int
        get() = (selectedDuration.inWholeMinutes % MINUTES_PER_HOUR).toInt()

    /**
     * Programmatically selects [duration] without dispatching `DurationPicker` user callbacks.
     *
     * @param duration The finite, non-negative, whole-minute duration to select.
     * @throws IllegalArgumentException if [duration] is not finite, non-negative, whole-minute, or
     * its whole-hour part does not fit in [Int].
     */
    fun selectDuration(duration: Duration) {
        duration.requireDurationPickerValue("duration")
        require(duration.inWholeMinutes / MINUTES_PER_HOUR <= Int.MAX_VALUE) {
            "duration whole hours must fit in Int, but was $duration."
        }
        mutableSelectedDuration = duration
    }

    /**
     * Programmatically selects elapsed [hours] and minute-within-hour [minutes].
     *
     * @param hours The non-negative elapsed whole-hour component.
     * @param minutes The minute-within-hour component in `0..59`.
     * @throws IllegalArgumentException if either part is outside its supported range.
     */
    fun selectDuration(hours: Int, minutes: Int) {
        selectDuration(
            checkedDurationPickerValue(
                hours = hours,
                minutes = minutes,
                hourName = "hours",
                minuteName = "minutes"
            )
        )
    }

    /**
     * Programmatically selects the closest duration to [duration] allowed by [items].
     *
     * @param duration The requested finite, non-negative, whole-minute duration.
     * @param items The item sources and scalar bounds used for coercion.
     * @throws IllegalArgumentException if the requested value or item configuration is invalid or
     * the item configuration has no selectable scalar combination.
     */
    fun selectDuration(duration: Duration, items: DurationPickerItems) {
        selectDuration(items.coerceDuration(duration))
    }

    /**
     * Programmatically selects the closest duration to [hours]/[minutes] allowed by [items].
     *
     * @param hours The requested non-negative elapsed whole-hour component.
     * @param minutes The requested minute-within-hour component in `0..59`.
     * @param items The item sources and scalar bounds used for coercion.
     * @throws IllegalArgumentException if the requested parts or item configuration are invalid or
     * the item configuration has no selectable scalar combination.
     */
    fun selectDuration(hours: Int, minutes: Int, items: DurationPickerItems) {
        selectDuration(items.coerceDuration(hours = hours, minutes = minutes))
    }

    companion object {
        /** Saves and restores [DurationPickerState] as total whole minutes. */
        val Saver: Saver<DurationPickerState, Any> = listSaver(
            save = { listOf(it.selectedDuration.inWholeMinutes) },
            restore = { DurationPickerState(initialDuration = (it[0] as Long).minutes) }
        )
    }
}

private fun durationPickerStateSaver(
    items: DurationPickerItems
): Saver<DurationPickerState, Any> = listSaver(
    save = { listOf(it.selectedDuration.inWholeMinutes) },
    restore = {
        DurationPickerState(
            initialDuration = items.coerceDuration((it[0] as Long).minutes)
        )
    }
)

private fun checkedDurationPickerValue(
    hours: Int,
    minutes: Int,
    hourName: String,
    minuteName: String
): Duration {
    require(hours >= 0) { "$hourName must be non-negative, but was $hours." }
    require(minutes in 0..59) {
        "$minuteName must be in range [0, 59], but was $minutes."
    }
    return durationPickerValue(hours = hours, minutes = minutes)
}
