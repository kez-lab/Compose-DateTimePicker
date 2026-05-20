package com.kez.picker

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

/**
 * Unit tests for [PickerState] class.
 *
 * Tests cover:
 * - Initial value setting
 * - State updates
 * - Generic type support
 */
class PickerStateTest {

    @Test
    fun pickerState_initialValue_isCorrect() {
        val state = PickerState("Initial")
        assertEquals("Initial", state.selectedItem)
    }

    @Test
    fun pickerState_intInitialValue_isCorrect() {
        val state = PickerState(42)
        assertEquals(42, state.selectedItem)
    }

    @Test
    fun pickerState_selectItem_updatesSelectedItem() {
        val state = PickerState("Initial")

        state.selectItem("Updated")

        assertEquals("Updated", state.selectedItem)
    }

    @Test
    fun pickerState_selectItem_recordsProgrammaticSelectionRequest() {
        val state = PickerState("Initial")

        state.selectItem("Updated")

        assertEquals(1, state.selectionRequestVersion)
        assertEquals("Updated", state.activeSelectionRequest?.item)
    }

    @Test
    fun pickerState_nullableInitialValue_isCorrect() {
        val state = PickerState<String?>(null)
        assertEquals(null, state.selectedItem)
    }

    @Test
    fun pickerState_customObjectInitialValue_isCorrect() {
        data class CustomItem(val id: Int, val name: String)
        val item = CustomItem(1, "Test")
        val state = PickerState(item)
        assertEquals(item, state.selectedItem)
    }

    @Test
    fun pickerState_differentInstances_areIndependent() {
        val state1 = PickerState("State1")
        val state2 = PickerState("State2")

        assertNotEquals(state1.selectedItem, state2.selectedItem)
    }

    @Test
    fun pickerState_sameInitialValue_areEqual() {
        val state1 = PickerState(100)
        val state2 = PickerState(100)

        assertEquals(state1.selectedItem, state2.selectedItem)
    }
}
