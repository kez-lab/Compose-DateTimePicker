package com.kez.picker

import kotlin.math.abs

/**
 * Commits one already-repaired logical value produced by a multi-column user interaction.
 *
 * The state commit happens before [onSelectionCommitted], and unchanged values dispatch neither.
 * App-driven state changes bypass this function so they cannot be confused with user commits.
 */
internal fun <State : Any> commitMultiWheelSelection(
    currentState: State,
    nextState: State,
    commitState: (State) -> Unit,
    onSelectionCommitted: (State) -> Unit
): Boolean {
    if (nextState == currentState) return false

    commitState(nextState)
    onSelectionCommitted(nextState)
    return true
}

/** Returns the numerically closest value, preferring the smaller value on equal distance. */
internal fun List<Int>.closestPickerValueTo(value: Int, sourceName: String): Int {
    require(isNotEmpty()) { "$sourceName must contain at least one selectable value." }
    return minWith(
        compareBy<Int> { abs(it - value) }
            .thenBy { it }
    )
}
