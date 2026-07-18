package com.kez.picker.sample.ui.screen.datetime

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.number
import kotlin.math.abs

internal enum class DateTimePickerColumn {
    YEAR,
    MONTH,
    DAY,
    HOUR,
    MINUTE
}

internal data class DateTimePickerLayout(
    val columnOrder: List<DateTimePickerColumn> = DateTimePickerColumn.entries
) {
    init {
        require(
            columnOrder.size == DateTimePickerColumn.entries.size &&
                    columnOrder.toSet() == DateTimePickerColumn.entries.toSet()
        ) {
            "DateTimePickerLayout.columnOrder must contain each column exactly once, " +
                    "but was $columnOrder."
        }
    }
}

internal data class DateTimePickerItems(
    val candidates: List<LocalDateTime>
) {
    init {
        require(candidates.isNotEmpty()) {
            "DateTimePicker candidates must not be empty."
        }
        require(candidates.distinct().size == candidates.size) {
            "DateTimePicker candidates must not contain duplicate values."
        }
        val nonWholeMinuteCandidates = candidates.filter {
            it.second != 0 || it.nanosecond != 0
        }
        require(nonWholeMinuteCandidates.isEmpty()) {
            "DateTimePicker candidates must use whole-minute values. " +
                    "Invalid values: $nonWholeMinuteCandidates."
        }
        require(candidates.zipWithNext().all { (first, second) ->
            first.toCivilMinuteOrdinal() < second.toCivilMinuteOrdinal()
        }) {
            "DateTimePicker candidates must be strictly increasing."
        }
    }

    fun contains(dateTime: LocalDateTime): Boolean = dateTime in candidates

    fun selectableItemsFor(
        column: DateTimePickerColumn,
        currentDateTime: LocalDateTime
    ): List<Int> = candidates
        .asSequence()
        .filter { candidate -> candidate.matchesPrefixBefore(column, currentDateTime) }
        .map { candidate -> candidate.valueFor(column) }
        .distinct()
        .toList()

    fun coerceDateTime(dateTime: LocalDateTime): LocalDateTime {
        requireWholeMinute(dateTime)
        return closestCandidateTo(dateTime, candidates)
    }

    fun repairedDateTimeAfter(
        currentDateTime: LocalDateTime,
        column: DateTimePickerColumn,
        value: Int
    ): LocalDateTime {
        requireWholeMinute(currentDateTime)
        if (value !in selectableItemsFor(column, currentDateTime)) return currentDateTime

        val matchingCandidates = candidates.filter { candidate ->
            candidate.matchesPrefixBefore(column, currentDateTime) &&
                    candidate.valueFor(column) == value
        }
        val downstreamPreservingCandidates = candidatesPreservingDownstreamPrefix(
            source = matchingCandidates,
            currentDateTime = currentDateTime,
            changedColumn = column
        )
        return closestCandidateTo(currentDateTime, downstreamPreservingCandidates)
    }

    private fun closestCandidateTo(
        dateTime: LocalDateTime,
        source: List<LocalDateTime>
    ): LocalDateTime {
        val targetCivilMinute = dateTime.toCivilMinuteOrdinal()
        return source.minWith(
            compareBy<LocalDateTime> { candidate ->
                abs(candidate.toCivilMinuteOrdinal() - targetCivilMinute)
            }.thenBy { candidate -> candidate.toCivilMinuteOrdinal() }
        )
    }
}

internal val DefaultDateTimePickerItems = DateTimePickerItems(
    candidates = listOf(
        LocalDateTime(2026, 2, 28, 23, 0),
        LocalDateTime(2026, 2, 28, 23, 30),
        LocalDateTime(2026, 3, 1, 0, 0),
        LocalDateTime(2026, 3, 1, 0, 30),
        LocalDateTime(2026, 3, 1, 1, 0),
        LocalDateTime(2026, 3, 1, 1, 30)
    )
)

@Composable
internal fun rememberDateTimePickerState(
    items: DateTimePickerItems,
    initialDateTime: LocalDateTime
): DateTimePickerState {
    val rememberedItems = remember { items }
    val rememberedInitialDateTime = remember { initialDateTime }
    val saver = remember(rememberedItems) { dateTimePickerStateSaver(rememberedItems) }
    return rememberSaveable(saver = saver) {
        DateTimePickerState(
            initialDateTime = rememberedItems.coerceDateTime(rememberedInitialDateTime)
        )
    }
}

@Stable
internal class DateTimePickerState(
    initialDateTime: LocalDateTime
) {
    init {
        requireWholeMinute(initialDateTime)
    }

    private var mutableSelectedDateTime: LocalDateTime by mutableStateOf(initialDateTime)

    val selectedDateTime: LocalDateTime
        get() = mutableSelectedDateTime

    fun selectDateTime(dateTime: LocalDateTime) {
        requireWholeMinute(dateTime)
        mutableSelectedDateTime = dateTime
    }

    fun selectDateTime(dateTime: LocalDateTime, items: DateTimePickerItems) {
        selectDateTime(items.coerceDateTime(dateTime))
    }

    companion object {
        val Saver: Saver<DateTimePickerState, Any> = dateTimePickerStateSaver()
    }
}

internal fun dateTimePickerStateSaver(
    items: DateTimePickerItems? = null
): Saver<DateTimePickerState, Any> = listSaver(
    save = { state -> state.selectedDateTime.toSaveableList() },
    restore = { restoredValues ->
        val restoredDateTime = restoredValues.toLocalDateTime()
        DateTimePickerState(
            initialDateTime = items?.coerceDateTime(restoredDateTime) ?: restoredDateTime
        )
    }
)

internal fun commitDateTimeSelection(
    state: DateTimePickerState,
    nextDateTime: LocalDateTime,
    onSelectionCommitted: (LocalDateTime) -> Unit
): Boolean {
    if (nextDateTime == state.selectedDateTime) return false
    state.selectDateTime(nextDateTime)
    onSelectionCommitted(nextDateTime)
    return true
}

internal fun LocalDateTime.dateTimeDisplayText(): String =
    "$year-${month.number.twoDigits()}-${day.twoDigits()} " +
            "${hour.twoDigits()}:${minute.twoDigits()}"

private fun LocalDateTime.matchesPrefixBefore(
    column: DateTimePickerColumn,
    currentDateTime: LocalDateTime
): Boolean = when (column) {
    DateTimePickerColumn.YEAR -> true
    DateTimePickerColumn.MONTH -> year == currentDateTime.year
    DateTimePickerColumn.DAY ->
        year == currentDateTime.year && month == currentDateTime.month

    DateTimePickerColumn.HOUR -> date == currentDateTime.date
    DateTimePickerColumn.MINUTE ->
        date == currentDateTime.date && hour == currentDateTime.hour
}

private fun LocalDateTime.valueFor(column: DateTimePickerColumn): Int = when (column) {
    DateTimePickerColumn.YEAR -> year
    DateTimePickerColumn.MONTH -> month.number
    DateTimePickerColumn.DAY -> day
    DateTimePickerColumn.HOUR -> hour
    DateTimePickerColumn.MINUTE -> minute
}

private fun candidatesPreservingDownstreamPrefix(
    source: List<LocalDateTime>,
    currentDateTime: LocalDateTime,
    changedColumn: DateTimePickerColumn
): List<LocalDateTime> {
    var remainingCandidates = source
    for (downstreamColumn in DateTimePickerColumn.entries.drop(changedColumn.ordinal + 1)) {
        val currentValue = currentDateTime.valueFor(downstreamColumn)
        val preservingCandidates = remainingCandidates.filter { candidate ->
            candidate.valueFor(downstreamColumn) == currentValue
        }
        if (preservingCandidates.isEmpty()) break
        remainingCandidates = preservingCandidates
    }

    return remainingCandidates
}

private fun requireWholeMinute(dateTime: LocalDateTime) {
    require(dateTime.second == 0 && dateTime.nanosecond == 0) {
        "DateTimePicker logical values must use whole minutes, but was $dateTime."
    }
}

private fun LocalDateTime.toCivilMinuteOrdinal(): Long =
    date.toEpochDays().toLong() * MINUTES_PER_DAY + hour * MINUTES_PER_HOUR + minute

private fun LocalDateTime.toSaveableList(): List<Any> = listOf(
    year,
    month.number,
    day,
    hour,
    minute
)

private fun List<Any>.toLocalDateTime(): LocalDateTime = LocalDateTime(
    year = this[0] as Int,
    month = this[1] as Int,
    day = this[2] as Int,
    hour = this[3] as Int,
    minute = this[4] as Int
)

private fun Int.twoDigits(): String = toString().padStart(length = 2, padChar = '0')

private const val MINUTES_PER_HOUR = 60L
private const val MINUTES_PER_DAY = 24L * MINUTES_PER_HOUR
