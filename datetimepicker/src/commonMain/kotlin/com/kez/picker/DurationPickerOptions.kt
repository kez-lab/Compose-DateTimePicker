package com.kez.picker

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

/**
 * Inclusive scalar bounds applied by [DurationPickerItems].
 *
 * Bounds use whole-minute precision because [com.kez.picker.duration.DurationPicker] exposes hour
 * and minute columns. Each provided bound must be finite and non-negative.
 *
 * @param minDuration The smallest selectable duration, inclusive. Pass null to omit the lower bound.
 * @param maxDuration The largest selectable duration, inclusive. Pass null to omit the upper bound.
 * @throws IllegalArgumentException if a bound is not finite, non-negative, whole-minute, or the
 * lower bound is greater than the upper bound.
 */
data class DurationPickerConstraints(
    val minDuration: Duration? = null,
    val maxDuration: Duration? = null
) {
    init {
        minDuration?.requireDurationPickerValue("DurationPicker minDuration")
        maxDuration?.requireDurationPickerValue("DurationPicker maxDuration")
        if (minDuration != null && maxDuration != null) {
            require(minDuration <= maxDuration) {
                "DurationPicker minDuration must be less than or equal to maxDuration. " +
                        "minDuration=$minDuration, maxDuration=$maxDuration."
            }
        }
    }

    /**
     * Returns whether [duration] is inside the configured inclusive bounds.
     *
     * @param duration The scalar duration to test.
     */
    fun contains(duration: Duration): Boolean =
        duration.isDurationPickerValue() &&
                (minDuration == null || duration >= minDuration) &&
                (maxDuration == null || duration <= maxDuration)
}

/**
 * Selectable item sources for [com.kez.picker.duration.DurationPicker].
 *
 * [hourItems] represent elapsed whole hours and must contain distinct non-negative values.
 * [minuteItems] must contain distinct values in `0..59`. [constraints] are applied to the combined
 * scalar duration after both item sources. Treat both lists as immutable while composed.
 *
 * @param hourItems Elapsed whole-hour values available for selection.
 * @param minuteItems Minute-within-hour values available for selection.
 * @param constraints Inclusive scalar duration bounds.
 * @see PickerDefaults.durationPickerItems
 */
data class DurationPickerItems(
    val hourItems: List<Int>,
    val minuteItems: List<Int>,
    val constraints: DurationPickerConstraints = DurationPickerConstraints()
) {
    /**
     * Returns whether [duration] is directly selectable.
     *
     * This membership check does not validate the complete item configuration. Invalid, duplicate,
     * empty, or unsatisfiable sources are rejected by [coerceDuration] and by `DurationPicker`
     * composition.
     *
     * @param duration The scalar duration to test.
     */
    fun contains(duration: Duration): Boolean {
        if (!duration.isDurationPickerValue()) return false
        val totalMinutes = duration.inWholeMinutes
        if (totalMinutes / MINUTES_PER_HOUR > Int.MAX_VALUE) return false
        val hours = (totalMinutes / MINUTES_PER_HOUR).toInt()
        val minutes = (totalMinutes % MINUTES_PER_HOUR).toInt()
        return hours in hourItems &&
                minutes in minuteItems &&
                constraints.contains(duration)
    }

    /**
     * Returns whether [hours] and [minutes] form a directly selectable duration.
     *
     * Invalid negative hours or minute values outside `0..59` return false.
     *
     * @param hours The non-negative elapsed whole-hour component.
     * @param minutes The minute-within-hour component.
     */
    fun contains(hours: Int, minutes: Int): Boolean {
        if (hours < 0 || minutes !in 0..59) return false
        return contains(durationPickerValue(hours = hours, minutes = minutes))
    }

    /**
     * Returns the selectable duration closest to [duration]. Equal distances prefer the smaller
     * duration.
     *
     * @param duration The finite, non-negative, whole-minute duration to coerce.
     * @throws IllegalArgumentException if [duration] is not finite, non-negative, and aligned to a
     * whole minute, or if these item sources are invalid or contain no allowed combination.
     */
    fun coerceDuration(duration: Duration): Duration {
        duration.requireDurationPickerValue("duration")
        requireValid()

        var closest: Duration? = null
        var closestDistance: Duration? = null
        hourItems.forEach { hour ->
            selectableMinuteItemsFor(hour).forEach { minute ->
                val candidate = durationPickerValue(hours = hour, minutes = minute)
                val distance = if (candidate >= duration) {
                    candidate - duration
                } else {
                    duration - candidate
                }
                if (
                    closest == null ||
                    distance < closestDistance!! ||
                    (distance == closestDistance && candidate < closest!!)
                ) {
                    closest = candidate
                    closestDistance = distance
                }
            }
        }
        return checkNotNull(closest)
    }

    /**
     * Returns the selectable duration closest to the requested [hours] and [minutes].
     *
     * @param hours The requested non-negative elapsed whole-hour component.
     * @param minutes The requested minute-within-hour component in `0..59`.
     * @throws IllegalArgumentException if the requested parts or item sources are invalid.
     */
    fun coerceDuration(hours: Int, minutes: Int): Duration {
        require(hours >= 0) { "hours must be non-negative, but was $hours." }
        require(minutes in 0..59) { "minutes must be in range [0, 59], but was $minutes." }
        return coerceDuration(durationPickerValue(hours = hours, minutes = minutes))
    }

    internal fun selectableHourItems(): List<Int> =
        hourItems.filter { hour -> selectableMinuteItemsFor(hour).isNotEmpty() }

    internal fun selectableMinuteItemsFor(hour: Int): List<Int> =
        minuteItems.filter { minute ->
            constraints.contains(durationPickerValue(hours = hour, minutes = minute))
        }

    internal fun requireValid() {
        require(hourItems.isNotEmpty()) { "DurationPicker hourItems must not be empty." }
        require(hourItems.distinct().size == hourItems.size) {
            "DurationPicker hourItems must not contain duplicate values."
        }
        val invalidHours = hourItems.filter { it < 0 }.distinct()
        require(invalidHours.isEmpty()) {
            "DurationPicker hourItems must contain only non-negative values. " +
                    "Invalid values: $invalidHours"
        }

        require(minuteItems.isNotEmpty()) { "DurationPicker minuteItems must not be empty." }
        require(minuteItems.distinct().size == minuteItems.size) {
            "DurationPicker minuteItems must not contain duplicate values."
        }
        val invalidMinutes = minuteItems.filterNot { it in 0..59 }.distinct()
        require(invalidMinutes.isEmpty()) {
            "DurationPicker minuteItems must contain only values in range [0, 59]. " +
                    "Invalid values: $invalidMinutes"
        }

        require(selectableHourItems().isNotEmpty()) {
            "DurationPicker items must contain at least one hour/minute combination allowed by " +
                    "constraints. Adjust minDuration/maxDuration or include an item combination " +
                    "inside the allowed range."
        }
    }
}

/**
 * Value formatting for [com.kez.picker.duration.DurationPicker].
 *
 * @param hour Formatting for elapsed whole-hour values.
 * @param minute Formatting for minute-within-hour values.
 */
@Immutable
data class DurationPickerFormat(
    val hour: PickerItemFormat<Int>,
    val minute: PickerItemFormat<Int>
)

/**
 * Accessibility configuration for [com.kez.picker.duration.DurationPicker].
 *
 * @param hour Structural semantics for the elapsed-hour column.
 * @param minute Structural semantics for the minute-within-hour column.
 */
@Immutable
data class DurationPickerSemantics(
    val hour: PickerSemantics,
    val minute: PickerSemantics
)

/** Identifies a [com.kez.picker.duration.DurationPicker] column for visual ordering. */
enum class DurationPickerColumn {
    /** Elapsed whole-hour column. */
    HOUR,

    /** Minute-within-hour column. */
    MINUTE
}

/**
 * Layout options for [com.kez.picker.duration.DurationPicker].
 *
 * A null weight leaves that column unweighted so `pickerModifier` can provide its width.
 *
 * @param hourWeight The elapsed-hour column weight, or null for an unweighted column.
 * @param minuteWeight The minute-within-hour column weight, or null for an unweighted column.
 * @param columnOrder The visual order containing each [DurationPickerColumn] exactly once.
 * @throws IllegalArgumentException if a provided weight is not positive or [columnOrder] does not
 * contain each column exactly once.
 * @see PickerDefaults.durationPickerLayout
 */
@Immutable
data class DurationPickerLayout(
    val hourWeight: Float?,
    val minuteWeight: Float?,
    val columnOrder: ImmutableList<DurationPickerColumn> = persistentListOf(
        DurationPickerColumn.HOUR,
        DurationPickerColumn.MINUTE
    )
) {
    init {
        require(hourWeight == null || hourWeight > 0f) {
            "hourWeight must be positive when provided, but was $hourWeight."
        }
        require(minuteWeight == null || minuteWeight > 0f) {
            "minuteWeight must be positive when provided, but was $minuteWeight."
        }
        require(
            columnOrder.size == DurationPickerColumn.entries.size &&
                    columnOrder.toSet() == DurationPickerColumn.entries.toSet()
        ) {
            "DurationPickerLayout.columnOrder must contain each of " +
                    "${DurationPickerColumn.entries} exactly once, but was $columnOrder."
        }
    }
}

internal const val MINUTES_PER_HOUR: Long = 60L

internal fun durationPickerValue(hours: Int, minutes: Int): Duration =
    (hours.toLong() * MINUTES_PER_HOUR + minutes).minutes

internal fun Duration.isDurationPickerValue(): Boolean =
    isFinite() && this >= Duration.ZERO && this == inWholeMinutes.minutes

internal fun Duration.requireDurationPickerValue(name: String) {
    require(isFinite()) { "$name must be finite, but was $this." }
    require(this >= Duration.ZERO) { "$name must be non-negative, but was $this." }
    require(this == inWholeMinutes.minutes) {
        "$name must use whole-minute precision, but was $this."
    }
}
