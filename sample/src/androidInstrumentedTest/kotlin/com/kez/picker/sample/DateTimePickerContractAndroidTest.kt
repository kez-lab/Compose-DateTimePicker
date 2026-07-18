package com.kez.picker.sample

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.isSelected
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import com.kez.picker.PickerDefaults
import com.kez.picker.sample.ui.screen.datetime.DateTimePicker
import com.kez.picker.sample.ui.screen.datetime.DateTimePickerColumn
import com.kez.picker.sample.ui.screen.datetime.DateTimePickerLayout
import com.kez.picker.sample.ui.screen.datetime.DateTimePickerState
import com.kez.picker.sample.ui.screen.datetime.DefaultDateTimePickerItems
import com.kez.picker.sample.ui.screen.datetime.rememberDateTimePickerState
import kotlinx.datetime.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class DateTimePickerContractAndroidTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun programmaticSelection_cancelsInFlightStillSelectableMinuteTarget() {
        lateinit var state: DateTimePickerState
        val committedDateTimes = mutableListOf<LocalDateTime>()
        val items = DefaultDateTimePickerItems

        composeRule.setContent {
            MaterialTheme {
                state = rememberDateTimePickerState(
                    items = items,
                    initialDateTime = LocalDateTime(2026, 3, 1, 0, 0)
                )
                DateTimePicker(
                    state = state,
                    items = items,
                    onSelectedDateTimeChange = committedDateTimes::add,
                    style = PickerDefaults.style(visibleItemsCount = 3)
                )
            }
        }

        composeRule.mainClock.autoAdvance = false
        val targetTopBefore = uniqueNodeTop("Date-time minute: 30")
        clickUniqueNode("Date-time minute: 30")
        composeRule.mainClock.advanceTimeBy(100)
        val targetTopDuring = uniqueNodeTop("Date-time minute: 30")
        assertTrue(
            "The minute animation must start before programmatic selection replaces it.",
            targetTopDuring < targetTopBefore
        )
        composeRule.runOnIdle {
            assertEquals(LocalDateTime(2026, 3, 1, 0, 0), state.selectedDateTime)
            assertEquals(emptyList<LocalDateTime>(), committedDateTimes)
        }

        composeRule.runOnIdle {
            state.selectDateTime(
                dateTime = LocalDateTime(2026, 3, 1, 1, 0),
                items = items
            )
        }
        composeRule.mainClock.advanceTimeBy(5_000)
        composeRule.mainClock.autoAdvance = true
        composeRule.waitForIdle()

        waitUntilSelected("Date-time hour: 1")
        waitUntilSelected("Date-time minute: 0")
        composeRule.runOnIdle {
            assertEquals(LocalDateTime(2026, 3, 1, 1, 0), state.selectedDateTime)
            assertEquals(emptyList<LocalDateTime>(), committedDateTimes)
        }
    }

    @Test
    fun reversedLayout_keepsVisualOrderAndLogicalRepair() {
        lateinit var state: DateTimePickerState
        val committedDateTimes = mutableListOf<LocalDateTime>()
        val items = DefaultDateTimePickerItems

        composeRule.setContent {
            MaterialTheme {
                state = rememberDateTimePickerState(
                    items = items,
                    initialDateTime = LocalDateTime(2026, 2, 28, 23, 30)
                )
                DateTimePicker(
                    state = state,
                    items = items,
                    onSelectedDateTimeChange = committedDateTimes::add,
                    layout = DateTimePickerLayout(
                        columnOrder = DateTimePickerColumn.entries.reversed()
                    ),
                    style = PickerDefaults.style(visibleItemsCount = 3)
                )
            }
        }

        val selectedNodeLefts = listOf(
            "Date-time minute: 30",
            "Date-time hour: 23",
            "Date-time day: 28",
            "Date-time month: 2",
            "Date-time year: 2026"
        ).map(::selectedNodeLeft)
        assertTrue(
            "The reversed layout must place minute before year without changing dependencies.",
            selectedNodeLefts.zipWithNext().all { (left, right) -> left < right }
        )

        clickUniqueNode("Date-time month: 3")
        waitUntilSelected("Date-time month: 3")
        waitUntilSelected("Date-time day: 1")
        waitUntilSelected("Date-time hour: 0")
        waitUntilSelected("Date-time minute: 0")

        composeRule.runOnIdle {
            val expected = LocalDateTime(2026, 3, 1, 0, 0)
            assertEquals(expected, state.selectedDateTime)
            assertEquals(listOf(expected), committedDateTimes)
        }
    }

    @Test
    fun rememberState_restoresProgrammaticSelectionWithoutUserCallback() {
        lateinit var state: DateTimePickerState
        val committedDateTimes = mutableListOf<LocalDateTime>()
        val items = DefaultDateTimePickerItems
        val restorationTester = StateRestorationTester(composeRule)

        restorationTester.setContent {
            MaterialTheme {
                state = rememberDateTimePickerState(
                    items = items,
                    initialDateTime = LocalDateTime(2026, 2, 28, 23, 30)
                )
                DateTimePicker(
                    state = state,
                    items = items,
                    onSelectedDateTimeChange = committedDateTimes::add,
                    style = PickerDefaults.style(visibleItemsCount = 3)
                )
            }
        }

        composeRule.runOnIdle {
            state.selectDateTime(
                dateTime = LocalDateTime(2026, 3, 1, 1, 30),
                items = items
            )
        }
        waitUntilSelected("Date-time hour: 1")
        waitUntilSelected("Date-time minute: 30")

        restorationTester.emulateSavedInstanceStateRestore()
        waitUntilSelected("Date-time month: 3")
        waitUntilSelected("Date-time day: 1")
        waitUntilSelected("Date-time hour: 1")
        waitUntilSelected("Date-time minute: 30")

        composeRule.runOnIdle {
            assertEquals(LocalDateTime(2026, 3, 1, 1, 30), state.selectedDateTime)
            assertEquals(emptyList<LocalDateTime>(), committedDateTimes)
        }
    }

    private fun waitUntilSelected(contentDescription: String) {
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule
                .onAllNodes(hasContentDescription(contentDescription) and isSelected())
                .fetchSemanticsNodes()
                .size == 1
        }
    }

    private fun clickUniqueNode(contentDescription: String) {
        val nodes = composeRule.onAllNodes(hasContentDescription(contentDescription))
        assertEquals(1, nodes.fetchSemanticsNodes().size)
        nodes[0].performClick()
    }

    private fun uniqueNodeTop(contentDescription: String): Float = composeRule
        .onAllNodes(hasContentDescription(contentDescription))
        .fetchSemanticsNodes()
        .single()
        .boundsInRoot
        .top

    private fun selectedNodeLeft(contentDescription: String): Float = composeRule
        .onAllNodes(hasContentDescription(contentDescription) and isSelected())
        .fetchSemanticsNodes()
        .single()
        .boundsInRoot
        .left
}
