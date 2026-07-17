package com.kez.picker

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.isSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class WheelPickerSelectionRobolectricTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun wheelPicker_clickDispatchesLiveChangeBeforeSettledCallback() {
        val events = mutableListOf<String>()

        composeRule.setContent {
            var selectedItem by remember { mutableStateOf(2) }
            WheelPicker(
                items = listOf(1, 2, 3),
                selectedItem = selectedItem,
                onSelectedItemChange = { item ->
                    events += "change:$item"
                    selectedItem = item
                },
                onSelectionSettled = { item -> events += "settled:$item" },
                style = PickerDefaults.style(visibleItemsCount = 3),
                semantics = PickerDefaults.semantics(pickerLabel = "Value"),
                isInfinity = false
            )
        }

        composeRule
            .onNode(hasContentDescription("Value: 3"))
            .performClick()

        waitUntilSelectedItem("Value: 3")

        composeRule.runOnIdle {
            assertEquals(listOf("change:3", "settled:3"), events)
        }
    }

    @Test
    fun wheelPicker_programmaticSelectionSynchronizesWithoutDispatchingCallbacks() {
        lateinit var selectedItemState: MutableState<Int>
        val events = mutableListOf<String>()

        composeRule.setContent {
            selectedItemState = remember { mutableStateOf(1) }
            WheelPicker(
                items = listOf(1, 2, 3),
                selectedItem = selectedItemState.value,
                onSelectedItemChange = { item ->
                    events += "change:$item"
                    selectedItemState.value = item
                },
                onSelectionSettled = { item -> events += "settled:$item" },
                style = PickerDefaults.style(visibleItemsCount = 3),
                semantics = PickerDefaults.semantics(pickerLabel = "Value"),
                isInfinity = false
            )
        }

        composeRule.runOnIdle {
            selectedItemState.value = 3
        }

        waitUntilSelectedItem("Value: 3")

        composeRule.runOnIdle {
            assertEquals(emptyList<String>(), events)
        }
    }

    @Test
    fun wheelPicker_itemSourceAndLoopModeChangesDoNotDispatchCallbacks() {
        lateinit var itemsState: MutableState<List<Int>>
        lateinit var isInfinityState: MutableState<Boolean>
        val events = mutableListOf<String>()

        composeRule.setContent {
            itemsState = remember { mutableStateOf(listOf(1, 2, 3)) }
            isInfinityState = remember { mutableStateOf(true) }
            var selectedItem by remember { mutableStateOf(2) }
            WheelPicker(
                items = itemsState.value,
                selectedItem = selectedItem,
                onSelectedItemChange = { item ->
                    events += "change:$item"
                    selectedItem = item
                },
                onSelectionSettled = { item -> events += "settled:$item" },
                style = PickerDefaults.style(visibleItemsCount = 3),
                semantics = PickerDefaults.semantics(pickerLabel = "Value"),
                isInfinity = isInfinityState.value
            )
        }

        composeRule.runOnIdle {
            itemsState.value = listOf(2, 3, 4)
            isInfinityState.value = false
        }

        waitUntilSelectedItem("Value: 2")

        composeRule.runOnIdle {
            assertEquals(emptyList<String>(), events)
        }
    }

    @Test
    fun wheelPicker_liveSelectionCanReplaceItemsWithoutLosingSettledCallback() {
        lateinit var itemsState: MutableState<List<Int>>
        val events = mutableListOf<String>()

        composeRule.setContent {
            itemsState = remember { mutableStateOf(listOf(1, 2, 3)) }
            var selectedItem by remember { mutableStateOf(2) }
            WheelPicker(
                items = itemsState.value,
                selectedItem = selectedItem,
                onSelectedItemChange = { item ->
                    events += "change:$item"
                    selectedItem = item
                    itemsState.value = listOf(2, 3, 4)
                },
                onSelectionSettled = { item -> events += "settled:$item" },
                style = PickerDefaults.style(visibleItemsCount = 3),
                semantics = PickerDefaults.semantics(pickerLabel = "Value"),
                isInfinity = false
            )
        }

        composeRule
            .onNode(hasContentDescription("Value: 3"))
            .performClick()

        waitUntilSelectedItem("Value: 3")

        composeRule.runOnIdle {
            assertEquals(listOf("change:3", "settled:3"), events)
        }
    }

    @Test
    fun pickerCompatibilityCallbackStillDispatchesOnlyAfterSelectionSettles() {
        val selectedItems = mutableListOf<Int>()

        composeRule.setContent {
            var selectedItem by remember { mutableStateOf(2) }
            Picker(
                items = listOf(1, 2, 3),
                selectedItem = selectedItem,
                onSelectedItemChange = { item ->
                    selectedItems += item
                    selectedItem = item
                },
                style = PickerDefaults.style(visibleItemsCount = 3),
                semantics = PickerDefaults.semantics(pickerLabel = "Value"),
                isInfinity = false
            )
        }

        composeRule
            .onNode(hasContentDescription("Value: 3"))
            .performClick()

        waitUntilSelectedItem("Value: 3")

        composeRule.runOnIdle {
            assertEquals(listOf(3), selectedItems)
        }
    }

    private fun waitUntilSelectedItem(contentDescription: String) {
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule
                .onAllNodes(
                    hasContentDescription(contentDescription) and isSelected()
                )
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
    }
}
