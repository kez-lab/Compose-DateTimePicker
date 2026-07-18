package com.kez.picker.sample.ui.screen.datetime

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DateTimePickerContractTest {

    @Test
    fun items_rejectInvalidCandidateSources() {
        val valid = LocalDateTime(2026, 2, 28, 23, 0)
        val invalidItems = listOf(
            { DateTimePickerItems(candidates = emptyList()) },
            { DateTimePickerItems(candidates = listOf(valid, valid)) },
            {
                DateTimePickerItems(
                    candidates = listOf(LocalDateTime(2026, 2, 28, 23, 0, 1))
                )
            },
            {
                DateTimePickerItems(
                    candidates = listOf(
                        LocalDateTime(2026, 3, 1, 0, 0),
                        LocalDateTime(2026, 2, 28, 23, 30)
                    )
                )
            }
        )

        invalidItems.forEach { createItems ->
            assertFailsWith<IllegalArgumentException> { createItems() }
        }
    }

    @Test
    fun selectableItems_followTheCurrentLogicalPrefix() {
        val februarySelection = LocalDateTime(2026, 2, 28, 23, 30)
        assertEquals(
            listOf(2026),
            DefaultDateTimePickerItems.selectableItemsFor(
                DateTimePickerColumn.YEAR,
                februarySelection
            )
        )
        assertEquals(
            listOf(2, 3),
            DefaultDateTimePickerItems.selectableItemsFor(
                DateTimePickerColumn.MONTH,
                februarySelection
            )
        )
        assertEquals(
            listOf(28),
            DefaultDateTimePickerItems.selectableItemsFor(
                DateTimePickerColumn.DAY,
                februarySelection
            )
        )
        assertEquals(
            listOf(23),
            DefaultDateTimePickerItems.selectableItemsFor(
                DateTimePickerColumn.HOUR,
                februarySelection
            )
        )
        assertEquals(
            listOf(0, 30),
            DefaultDateTimePickerItems.selectableItemsFor(
                DateTimePickerColumn.MINUTE,
                februarySelection
            )
        )

        val marchSelection = LocalDateTime(2026, 3, 1, 0, 0)
        assertEquals(
            listOf(1),
            DefaultDateTimePickerItems.selectableItemsFor(
                DateTimePickerColumn.DAY,
                marchSelection
            )
        )
        assertEquals(
            listOf(0, 1),
            DefaultDateTimePickerItems.selectableItemsFor(
                DateTimePickerColumn.HOUR,
                marchSelection
            )
        )
    }

    @Test
    fun coerceDateTime_prefersTheEarlierCandidateOnEqualDistance() {
        assertEquals(
            LocalDateTime(2026, 2, 28, 23, 30),
            DefaultDateTimePickerItems.coerceDateTime(
                LocalDateTime(2026, 2, 28, 23, 45)
            )
        )
    }

    @Test
    fun monthChange_repairsDayHourAndMinuteInOneLogicalValue() {
        val current = LocalDateTime(2026, 2, 28, 23, 30)
        val repaired = DefaultDateTimePickerItems.repairedDateTimeAfter(
            currentDateTime = current,
            column = DateTimePickerColumn.MONTH,
            value = 3
        )

        assertEquals(LocalDateTime(2026, 3, 1, 0, 0), repaired)
        assertTrue(DefaultDateTimePickerItems.contains(repaired))
    }

    @Test
    fun hourChange_repairsMinuteFromTheNewActiveSource() {
        val current = LocalDateTime(2026, 3, 1, 0, 30)

        assertEquals(
            LocalDateTime(2026, 3, 1, 1, 30),
            DefaultDateTimePickerItems.repairedDateTimeAfter(
                currentDateTime = current,
                column = DateTimePickerColumn.HOUR,
                value = 1
            )
        )
    }

    @Test
    fun logicalStateAndCoercion_rejectSubMinuteValuesWithoutSilentTruncation() {
        val subMinute = LocalDateTime(2026, 2, 28, 23, 30, 1)

        assertFailsWith<IllegalArgumentException> {
            DateTimePickerState(initialDateTime = subMinute)
        }
        assertFailsWith<IllegalArgumentException> {
            DefaultDateTimePickerItems.coerceDateTime(subMinute)
        }
    }

    @Test
    fun lateInvalidAndNoOpChanges_doNotCreateAnInvalidSelection() {
        val current = LocalDateTime(2026, 3, 1, 0, 0)

        assertEquals(
            current,
            DefaultDateTimePickerItems.repairedDateTimeAfter(
                currentDateTime = current,
                column = DateTimePickerColumn.DAY,
                value = 28
            )
        )
        assertEquals(
            current,
            DefaultDateTimePickerItems.repairedDateTimeAfter(
                currentDateTime = current,
                column = DateTimePickerColumn.MINUTE,
                value = 0
            )
        )
    }

    @Test
    fun everyActiveColumnValue_repairsToAConfiguredCandidate() {
        DefaultDateTimePickerItems.candidates.forEach { current ->
            DateTimePickerColumn.entries.forEach { column ->
                DefaultDateTimePickerItems
                    .selectableItemsFor(column, current)
                    .forEach { value ->
                        val repaired = DefaultDateTimePickerItems.repairedDateTimeAfter(
                            currentDateTime = current,
                            column = column,
                            value = value
                        )
                        assertTrue(DefaultDateTimePickerItems.contains(repaired))
                    }
            }
        }
    }

    @Test
    fun directEdits_preserveTheRequestedValueAndRepairOnlyUnavailableSuffixes() {
        val variedItems = DateTimePickerItems(
            candidates = listOf(
                LocalDateTime(2025, 12, 31, 23, 15),
                LocalDateTime(2025, 12, 31, 23, 45),
                LocalDateTime(2026, 1, 1, 0, 15),
                LocalDateTime(2026, 1, 1, 0, 45),
                LocalDateTime(2026, 1, 2, 0, 15)
            )
        )

        assertEquals(
            LocalDateTime(2026, 1, 1, 0, 15),
            variedItems.repairedDateTimeAfter(
                currentDateTime = LocalDateTime(2025, 12, 31, 23, 45),
                column = DateTimePickerColumn.YEAR,
                value = 2026
            )
        )
        assertEquals(
            LocalDateTime(2026, 1, 2, 0, 15),
            variedItems.repairedDateTimeAfter(
                currentDateTime = LocalDateTime(2026, 1, 1, 0, 45),
                column = DateTimePickerColumn.DAY,
                value = 2
            )
        )
        assertEquals(
            LocalDateTime(2026, 1, 1, 0, 45),
            variedItems.repairedDateTimeAfter(
                currentDateTime = LocalDateTime(2026, 1, 1, 0, 15),
                column = DateTimePickerColumn.MINUTE,
                value = 45
            )
        )
    }

    @Test
    fun commit_updatesStateBeforeOneCallbackAndIgnoresNoOp() {
        val initial = LocalDateTime(2026, 2, 28, 23, 30)
        val next = LocalDateTime(2026, 3, 1, 0, 0)
        val state = DateTimePickerState(initialDateTime = initial)
        val callbacks = mutableListOf<LocalDateTime>()

        val changed = commitDateTimeSelection(
            state = state,
            nextDateTime = next,
            onSelectionCommitted = { committed ->
                assertEquals(committed, state.selectedDateTime)
                callbacks += committed
            }
        )
        val changedAgain = commitDateTimeSelection(
            state = state,
            nextDateTime = next,
            onSelectionCommitted = callbacks::add
        )

        assertTrue(changed)
        assertFalse(changedAgain)
        assertEquals(listOf(next), callbacks)
    }

    @Test
    fun programmaticSelectionAndSavers_keepAConfiguredLogicalValue() {
        val state = DateTimePickerState(
            initialDateTime = LocalDateTime(2026, 2, 28, 23, 30)
        )
        state.selectDateTime(
            dateTime = LocalDateTime(2026, 3, 1, 0, 45),
            items = DefaultDateTimePickerItems
        )

        assertEquals(LocalDateTime(2026, 3, 1, 0, 30), state.selectedDateTime)
        assertEquals(
            state.selectedDateTime,
            state.saveAndRestore(DateTimePickerState.Saver).selectedDateTime
        )

        val marchOnlyItems = DateTimePickerItems(
            candidates = listOf(
                LocalDateTime(2026, 3, 1, 0, 0),
                LocalDateTime(2026, 3, 1, 0, 30)
            )
        )
        val februaryState = DateTimePickerState(
            initialDateTime = LocalDateTime(2026, 2, 28, 23, 30)
        )
        assertEquals(
            LocalDateTime(2026, 3, 1, 0, 0),
            februaryState.saveAndRestore(
                dateTimePickerStateSaver(marchOnlyItems)
            ).selectedDateTime
        )
    }

    @Test
    fun layout_requiresEveryColumnExactlyOnce() {
        assertFailsWith<IllegalArgumentException> {
            DateTimePickerLayout(
                columnOrder = listOf(
                    DateTimePickerColumn.YEAR,
                    DateTimePickerColumn.MONTH,
                    DateTimePickerColumn.DAY,
                    DateTimePickerColumn.HOUR,
                    DateTimePickerColumn.HOUR
                )
            )
        }

        val reversed = DateTimePickerColumn.entries.reversed()
        assertEquals(
            reversed,
            DateTimePickerLayout(columnOrder = reversed).columnOrder
        )
    }

    private fun DateTimePickerState.saveAndRestore(
        saver: Saver<DateTimePickerState, Any>
    ): DateTimePickerState {
        val saved = with(saver) {
            SaverScope { true }.save(this@saveAndRestore)
        }
        return saver.restore(saved!!)!!
    }
}
