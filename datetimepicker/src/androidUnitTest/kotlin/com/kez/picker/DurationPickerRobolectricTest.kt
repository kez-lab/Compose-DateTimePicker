package com.kez.picker

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.isSelected
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import com.kez.picker.duration.DurationPicker
import com.kez.picker.duration.DurationPickerState
import com.kez.picker.duration.rememberDurationPickerState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class DurationPickerRobolectricTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun nonDefaultOrder_commitsOneRepairedScalarDurationAfterState() {
        lateinit var state: DurationPickerState
        val committedDurations = mutableListOf<Duration>()
        val items = PickerDefaults.durationPickerItems(
            hourItems = listOf(0, 1),
            minuteItems = (0..55 step 5).toList(),
            maxDuration = 90.minutes
        )

        composeRule.setContent {
            state = rememberDurationPickerState(
                items = items,
                initialDuration = 45.minutes
            )
            DurationPicker(
                state = state,
                items = items,
                onSelectedDurationChange = { duration ->
                    assertEquals(duration, state.selectedDuration)
                    committedDurations += duration
                },
                layout = PickerDefaults.durationPickerLayout(
                    columnOrder = listOf(
                        DurationPickerColumn.MINUTE,
                        DurationPickerColumn.HOUR
                    )
                ),
                style = PickerDefaults.style(visibleItemsCount = 3),
                format = testDurationFormat(),
                semantics = testDurationSemantics()
            )
        }

        val minuteLeft = composeRule
            .onNode(hasContentDescription("Minutes: 45 minutes") and isSelected())
            .fetchSemanticsNode()
            .boundsInRoot
            .left
        val hourLeft = composeRule
            .onNode(hasContentDescription("Hours: 0 hours") and isSelected())
            .fetchSemanticsNode()
            .boundsInRoot
            .left
        assertTrue("MINUTE must be rendered before HOUR.", minuteLeft < hourLeft)

        composeRule.onAllNodes(hasContentDescription("Hours: 1 hours"))[0].performClick()
        waitUntilSelected("Hours: 1 hours")
        waitUntilSelected("Minutes: 30 minutes")

        composeRule.runOnIdle {
            assertEquals(90.minutes, state.selectedDuration)
            assertEquals(listOf(90.minutes), committedDurations)
            assertTrue(items.contains(state.selectedDuration))
        }
    }

    @Test
    fun programmaticSelectionCancelsInFlightUserSettleWithoutCallback() {
        lateinit var state: DurationPickerState
        val committedDurations = mutableListOf<Duration>()
        val items = PickerDefaults.durationPickerItems(
            hourItems = listOf(0, 1),
            minuteItems = (0..55 step 5).toList(),
            maxDuration = 90.minutes
        )

        composeRule.setContent {
            state = rememberDurationPickerState(items = items, initialDuration = 45.minutes)
            DurationPicker(
                state = state,
                items = items,
                onSelectedDurationChange = committedDurations::add,
                style = PickerDefaults.style(visibleItemsCount = 3),
                format = testDurationFormat(),
                semantics = testDurationSemantics()
            )
        }

        composeRule.mainClock.autoAdvance = false
        val targetTopsBefore = nodeTops("Hours: 1 hours")
        composeRule.onAllNodes(hasContentDescription("Hours: 1 hours"))[0].performClick()
        composeRule.mainClock.advanceTimeBy(100)
        val targetTopsDuring = nodeTops("Hours: 1 hours")
        assertTrue(
            "The user animation must start before programmatic selection replaces it.",
            targetTopsDuring != targetTopsBefore
        )

        composeRule.runOnIdle {
            state.selectDuration(30.minutes, items)
        }
        composeRule.mainClock.advanceTimeBy(5_000)
        composeRule.mainClock.autoAdvance = true
        composeRule.waitForIdle()

        waitUntilSelected("Hours: 0 hours")
        waitUntilSelected("Minutes: 30 minutes")
        composeRule.runOnIdle {
            assertEquals(30.minutes, state.selectedDuration)
            assertEquals(emptyList<Duration>(), committedDurations)
        }
    }

    @Test
    fun upstreamHourSettleCancelsInFlightMinuteInteraction() {
        lateinit var state: DurationPickerState
        val committedDurations = mutableListOf<Duration>()
        val items = PickerDefaults.durationPickerItems(
            hourItems = listOf(0, 1),
            minuteItems = (0..55 step 5).toList(),
            maxDuration = 90.minutes
        )

        composeRule.setContent {
            state = rememberDurationPickerState(items = items, initialDuration = 25.minutes)
            DurationPicker(
                state = state,
                items = items,
                onSelectedDurationChange = committedDurations::add,
                style = PickerDefaults.style(visibleItemsCount = 3),
                format = testDurationFormat(),
                semantics = testDurationSemantics()
            )
        }

        composeRule.mainClock.autoAdvance = false
        composeRule.onAllNodes(hasContentDescription("Hours: 1 hours"))[0].performClick()
        composeRule.mainClock.advanceTimeBy(16)
        val minuteTargetTopsBefore = nodeTops("Minutes: 20 minutes")
        composeRule.onAllNodes(hasContentDescription("Minutes: 20 minutes"))[0].performClick()
        composeRule.mainClock.advanceTimeBy(100)
        val minuteTargetTopsDuring = nodeTops("Minutes: 20 minutes")
        assertTrue(
            "The minute animation must start before the hour repair invalidates it.",
            minuteTargetTopsDuring != minuteTargetTopsBefore
        )
        composeRule.mainClock.advanceTimeBy(5_000)
        composeRule.mainClock.autoAdvance = true
        composeRule.waitForIdle()

        waitUntilSelected("Hours: 1 hours")
        waitUntilSelected("Minutes: 25 minutes")
        composeRule.runOnIdle {
            assertEquals(85.minutes, state.selectedDuration)
            assertEquals(listOf(85.minutes), committedDurations)
        }
    }

    @Test
    fun programmaticSelectionSynchronizesColumnsWithoutUserCallback() {
        lateinit var state: DurationPickerState
        val committedDurations = mutableListOf<Duration>()
        val items = PickerDefaults.durationPickerItems(
            hourItems = listOf(0, 1),
            minuteItems = (0..55 step 5).toList(),
            maxDuration = 90.minutes
        )

        composeRule.setContent {
            state = rememberDurationPickerState(
                items = items,
                initialDuration = Duration.ZERO
            )
            DurationPicker(
                state = state,
                items = items,
                onSelectedDurationChange = committedDurations::add,
                style = PickerDefaults.style(visibleItemsCount = 3),
                format = testDurationFormat(),
                semantics = testDurationSemantics()
            )
        }

        composeRule.runOnIdle {
            state.selectDuration(85.minutes, items)
        }
        waitUntilSelected("Hours: 1 hours")
        waitUntilSelected("Minutes: 25 minutes")

        composeRule.runOnIdle {
            assertEquals(85.minutes, state.selectedDuration)
            assertEquals(emptyList<Duration>(), committedDurations)
        }
    }

    @Test
    fun itemsAwareRestoreCoercesSavedValueWithoutUserCallback() {
        lateinit var state: DurationPickerState
        val committedDurations = mutableListOf<Duration>()
        val restorationTester = StateRestorationTester(composeRule)
        val originalItems = PickerDefaults.durationPickerItems(
            hourItems = listOf(0, 1),
            minuteItems = listOf(0, 30),
            maxDuration = 90.minutes
        )
        var recreatedItems = originalItems

        restorationTester.setContent {
            state = rememberDurationPickerState(
                items = recreatedItems,
                initialDuration = Duration.ZERO
            )
            DurationPicker(
                state = state,
                items = recreatedItems,
                onSelectedDurationChange = committedDurations::add,
                style = PickerDefaults.style(visibleItemsCount = 3),
                format = testDurationFormat(),
                semantics = testDurationSemantics()
            )
        }

        composeRule.runOnIdle {
            state.selectDuration(90.minutes, originalItems)
            recreatedItems = PickerDefaults.durationPickerItems(
                hourItems = listOf(0),
                minuteItems = listOf(0, 30),
                maxDuration = 30.minutes
            )
        }

        restorationTester.emulateSavedInstanceStateRestore()
        waitUntilSelected("Hours: 0 hours")
        waitUntilSelected("Minutes: 30 minutes")

        composeRule.runOnIdle {
            assertEquals(30.minutes, state.selectedDuration)
            assertEquals(emptyList<Duration>(), committedDurations)
        }
    }

    @Test
    fun disabledColumnsKeepSelectedSemanticsAndExposeDisabledState() {
        lateinit var state: DurationPickerState
        val committedDurations = mutableListOf<Duration>()
        val items = PickerDefaults.durationPickerItems(
            hourItems = listOf(0, 1),
            minuteItems = listOf(0, 30)
        )

        composeRule.setContent {
            state = rememberDurationPickerState(
                items = items,
                initialDuration = 30.minutes
            )
            DurationPicker(
                state = state,
                items = items,
                enabled = false,
                onSelectedDurationChange = committedDurations::add,
                style = PickerDefaults.style(visibleItemsCount = 3),
                format = testDurationFormat(),
                semantics = testDurationSemantics()
            )
        }

        composeRule
            .onNode(hasContentDescription("Hours: 0 hours") and isSelected())
            .assertIsNotEnabled()
        composeRule
            .onNode(hasContentDescription("Minutes: 30 minutes") and isSelected())
            .assertIsNotEnabled()
        assertNoCustomAccessibilityActions("Hours: 0 hours")
        assertNoCustomAccessibilityActions("Minutes: 30 minutes")
        composeRule.runOnIdle {
            assertEquals(30.minutes, state.selectedDuration)
            assertEquals(emptyList<Duration>(), committedDurations)
        }
    }

    private fun waitUntilSelected(contentDescription: String) {
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule
                .onAllNodes(hasContentDescription(contentDescription) and isSelected())
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
    }

    private fun nodeTops(contentDescription: String): List<Float> =
        composeRule
            .onAllNodes(hasContentDescription(contentDescription))
            .fetchSemanticsNodes()
            .map { it.boundsInRoot.top }

    private fun assertNoCustomAccessibilityActions(contentDescription: String) {
        val actions = composeRule
            .onAllNodes(hasContentDescription(contentDescription))
            .fetchSemanticsNodes()
            .flatMap { node ->
                node.config
                    .getOrNull(SemanticsActions.CustomActions)
                    .orEmpty()
            }

        assertTrue(actions.isEmpty())
    }

    private fun testDurationFormat(): DurationPickerFormat =
        PickerDefaults.durationPickerFormat(
            hourItemContentDescription = { "$it hours" },
            minuteItemContentDescription = { "$it minutes" }
        )

    private fun testDurationSemantics(): DurationPickerSemantics =
        PickerDefaults.durationPickerSemantics(
            hourPickerLabel = "Hours",
            minutePickerLabel = "Minutes"
        )
}
