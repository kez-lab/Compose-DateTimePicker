package com.kez.picker.sample.ui.screen.quantity

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class QuantityUnitPickerContractTest {

    @Test
    fun items_rejectInvalidUnitAndQuantitySources() {
        val invalidItems = listOf(
            { QuantityUnitItems(unitItems = emptyList(), quantityItems = emptyMap()) },
            {
                QuantityUnitItems(
                    unitItems = listOf(QuantityUnit.GRAM, QuantityUnit.GRAM),
                    quantityItems = mapOf(QuantityUnit.GRAM to listOf(100))
                )
            },
            {
                QuantityUnitItems(
                    unitItems = listOf(QuantityUnit.GRAM, QuantityUnit.KILOGRAM),
                    quantityItems = mapOf(QuantityUnit.GRAM to listOf(100))
                )
            },
            {
                QuantityUnitItems(
                    unitItems = listOf(QuantityUnit.GRAM),
                    quantityItems = mapOf(QuantityUnit.GRAM to emptyList())
                )
            },
            {
                QuantityUnitItems(
                    unitItems = listOf(QuantityUnit.GRAM),
                    quantityItems = mapOf(QuantityUnit.GRAM to listOf(100, 100))
                )
            },
            {
                QuantityUnitItems(
                    unitItems = listOf(QuantityUnit.GRAM),
                    quantityItems = mapOf(QuantityUnit.GRAM to listOf(0, 100))
                )
            },
            {
                QuantityUnitItems(
                    unitItems = listOf(QuantityUnit.GRAM),
                    quantityItems = mapOf(
                        QuantityUnit.GRAM to listOf(100),
                        QuantityUnit.KILOGRAM to listOf(1)
                    )
                )
            },
            {
                QuantityUnitItems(
                    unitItems = listOf(QuantityUnit.GRAM),
                    quantityItems = mapOf(QuantityUnit.GRAM to listOf(-100, 100))
                )
            }
        )

        invalidItems.forEach { createItems ->
            assertFailsWith<IllegalArgumentException> { createItems() }
        }
        assertFailsWith<IllegalArgumentException> {
            QuantityUnitSelection(quantity = 0, unit = QuantityUnit.GRAM)
        }
    }

    @Test
    fun contains_requiresTheQuantityToBelongToItsUnitSource() {
        assertTrue(
            DefaultQuantityUnitItems.contains(
                QuantityUnitSelection(quantity = 2_500, unit = QuantityUnit.GRAM)
            )
        )
        assertTrue(
            DefaultQuantityUnitItems.contains(
                QuantityUnitSelection(quantity = 2, unit = QuantityUnit.KILOGRAM)
            )
        )
        assertFalse(
            DefaultQuantityUnitItems.contains(
                QuantityUnitSelection(quantity = 2_550, unit = QuantityUnit.GRAM)
            )
        )
        assertFalse(
            DefaultQuantityUnitItems.contains(
                QuantityUnitSelection(quantity = 6, unit = QuantityUnit.KILOGRAM)
            )
        )
    }

    @Test
    fun coerceSelection_preservesRequestedUnitWhenAvailable() {
        assertEquals(
            QuantityUnitSelection(quantity = 5, unit = QuantityUnit.KILOGRAM),
            DefaultQuantityUnitItems.coerceSelection(
                QuantityUnitSelection(quantity = 6, unit = QuantityUnit.KILOGRAM)
            )
        )
        assertEquals(
            QuantityUnitSelection(quantity = 2_500, unit = QuantityUnit.GRAM),
            DefaultQuantityUnitItems.coerceSelection(
                QuantityUnitSelection(quantity = 2_550, unit = QuantityUnit.GRAM)
            )
        )
    }

    @Test
    fun coerceSelection_repairsRemovedUnitByNormalizedMass() {
        val gramOnlyItems = QuantityUnitItems(
            unitItems = listOf(QuantityUnit.GRAM),
            quantityItems = mapOf(
                QuantityUnit.GRAM to (100..5_000 step 100).toList()
            )
        )

        assertEquals(
            QuantityUnitSelection(quantity = 2_000, unit = QuantityUnit.GRAM),
            gramOnlyItems.coerceSelection(
                QuantityUnitSelection(quantity = 2, unit = QuantityUnit.KILOGRAM)
            )
        )
    }

    @Test
    fun unitChange_repairsByScalarDistanceAndPrefersSmallerMassOnTie() {
        val unsortedItems = QuantityUnitItems(
            unitItems = listOf(QuantityUnit.GRAM, QuantityUnit.KILOGRAM),
            quantityItems = mapOf(
                QuantityUnit.GRAM to listOf(2_500),
                QuantityUnit.KILOGRAM to listOf(3, 2, 1)
            )
        )

        assertEquals(
            QuantityUnitSelection(quantity = 2, unit = QuantityUnit.KILOGRAM),
            unsortedItems.repairedSelectionAfterUnit(
                currentSelection = QuantityUnitSelection(
                    quantity = 2_500,
                    unit = QuantityUnit.GRAM
                ),
                unit = QuantityUnit.KILOGRAM
            )
        )
    }

    @Test
    fun quantityChange_acceptsOnlyTheActiveUnitSource() {
        val current = QuantityUnitSelection(quantity = 2, unit = QuantityUnit.KILOGRAM)

        assertEquals(
            QuantityUnitSelection(quantity = 4, unit = QuantityUnit.KILOGRAM),
            DefaultQuantityUnitItems.repairedSelectionAfterQuantity(
                currentSelection = current,
                quantity = 4
            )
        )
        assertEquals(
            current,
            DefaultQuantityUnitItems.repairedSelectionAfterQuantity(
                currentSelection = current,
                quantity = 2_500
            )
        )

        val gramOnlyItems = QuantityUnitItems(
            unitItems = listOf(QuantityUnit.GRAM),
            quantityItems = mapOf(QuantityUnit.GRAM to listOf(100))
        )
        val gramSelection = QuantityUnitSelection(quantity = 100, unit = QuantityUnit.GRAM)
        assertEquals(
            gramSelection,
            gramOnlyItems.repairedSelectionAfterUnit(
                currentSelection = gramSelection,
                unit = QuantityUnit.KILOGRAM
            )
        )
    }

    @Test
    fun commit_updatesStateBeforeOneCallbackAndIgnoresNoOp() {
        val state = QuantityUnitPickerState(
            initialSelection = QuantityUnitSelection(
                quantity = 2_500,
                unit = QuantityUnit.GRAM
            )
        )
        val callbacks = mutableListOf<QuantityUnitSelection>()
        val next = QuantityUnitSelection(quantity = 2, unit = QuantityUnit.KILOGRAM)

        val changed = commitQuantityUnitSelection(
            state = state,
            nextSelection = next,
            onSelectionCommitted = { selection ->
                assertEquals(selection, state.selectedSelection)
                callbacks += selection
            }
        )
        val changedAgain = commitQuantityUnitSelection(
            state = state,
            nextSelection = next,
            onSelectionCommitted = callbacks::add
        )

        assertTrue(changed)
        assertFalse(changedAgain)
        assertEquals(listOf(next), callbacks)
    }

    @Test
    fun stateProgrammaticSelectionAndSaversKeepASelectableLogicalValue() {
        val state = QuantityUnitPickerState(
            initialSelection = QuantityUnitSelection(
                quantity = 2_500,
                unit = QuantityUnit.GRAM
            )
        )

        state.selectSelection(
            selection = QuantityUnitSelection(
                quantity = 6,
                unit = QuantityUnit.KILOGRAM
            ),
            items = DefaultQuantityUnitItems
        )

        assertEquals(
            QuantityUnitSelection(quantity = 5, unit = QuantityUnit.KILOGRAM),
            state.selectedSelection
        )
        assertEquals(
            state.selectedSelection,
            state.saveAndRestore(QuantityUnitPickerState.Saver).selectedSelection
        )

        val gramOnlyItems = QuantityUnitItems(
            unitItems = listOf(QuantityUnit.GRAM),
            quantityItems = mapOf(QuantityUnit.GRAM to listOf(1_000, 5_000))
        )
        assertEquals(
            QuantityUnitSelection(quantity = 5_000, unit = QuantityUnit.GRAM),
            state.saveAndRestore(quantityUnitPickerStateSaver(gramOnlyItems)).selectedSelection
        )

        val kilogramOnlyItems = QuantityUnitItems(
            unitItems = listOf(QuantityUnit.KILOGRAM),
            quantityItems = mapOf(QuantityUnit.KILOGRAM to listOf(2, 3))
        )
        val gramState = QuantityUnitPickerState(
            initialSelection = QuantityUnitSelection(
                quantity = 2_500,
                unit = QuantityUnit.GRAM
            )
        )
        assertEquals(
            QuantityUnitSelection(quantity = 2, unit = QuantityUnit.KILOGRAM),
            gramState.saveAndRestore(
                quantityUnitPickerStateSaver(kilogramOnlyItems)
            ).selectedSelection
        )
    }

    @Test
    fun layout_requiresEachColumnExactlyOnce() {
        assertFailsWith<IllegalArgumentException> {
            QuantityUnitPickerLayout(
                columnOrder = listOf(
                    QuantityUnitColumn.QUANTITY,
                    QuantityUnitColumn.QUANTITY
                )
            )
        }
        assertEquals(
            listOf(QuantityUnitColumn.UNIT, QuantityUnitColumn.QUANTITY),
            QuantityUnitPickerLayout(
                columnOrder = listOf(
                    QuantityUnitColumn.UNIT,
                    QuantityUnitColumn.QUANTITY
                )
            ).columnOrder
        )
    }

    private fun QuantityUnitPickerState.saveAndRestore(
        saver: Saver<QuantityUnitPickerState, Any>
    ): QuantityUnitPickerState {
        val saved = with(saver) {
            SaverScope { true }.save(this@saveAndRestore)
        }
        return saver.restore(saved!!)!!
    }
}
