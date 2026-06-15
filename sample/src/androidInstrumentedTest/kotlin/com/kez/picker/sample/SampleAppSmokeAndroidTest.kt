package com.kez.picker.sample

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.kez.picker.util.currentDate
import org.junit.Rule
import org.junit.Test

class SampleAppSmokeAndroidTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun homeScreen_displaysPrimarySampleMenuActions() {
        composeRule
            .onNodeWithText("Compose DateTimePicker")
            .assertIsDisplayed()

        sampleMenuTags.forEach { tag ->
            composeRule
                .onNodeWithTag(tag)
                .performScrollTo()
                .assertIsDisplayed()
                .assertHasClickAction()
        }
    }

    @Test
    fun datePickerMenuAction_opensDatePickerSampleAndReturnsHome() {
        composeRule
            .onNodeWithTag("sample-menu-date-picker")
            .performScrollTo()
            .performClick()

        composeRule
            .onNodeWithText("DatePicker Sample")
            .assertIsDisplayed()

        composeRule
            .onNodeWithContentDescription("Back")
            .performClick()

        composeRule
            .onNodeWithText("Compose DateTimePicker")
            .assertIsDisplayed()
    }

    @Test
    fun timePickerMenuAction_opensTimePickerSampleAndRendersDefaultTab() {
        composeRule
            .onNodeWithTag("sample-menu-time-picker")
            .performScrollTo()
            .performClick()

        composeRule
            .onNodeWithText("TimePicker Sample")
            .assertIsDisplayed()
        composeRule
            .onNodeWithText("12-Hour")
            .assertIsDisplayed()
        composeRule
            .onNodeWithText("24-Hour")
            .assertIsDisplayed()
    }

    @Test
    fun dateRangePickerMenuAction_updatesSelectedRangeFromButtons() {
        val today = currentDate()
        val expectedSummary = "Selected range, $today..$today, Days selected: 1. " +
                "Last user change: No user change. Selectable year: ${today.year}"

        composeRule
            .onNodeWithTag("sample-menu-date-range-picker")
            .performScrollTo()
            .performClick()

        composeRule
            .onNodeWithText("DateRangePicker Sample")
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("Today only")
            .performClick()

        composeRule
            .onNode(hasContentDescriptionStartingWith(expectedSummary))
            .assertIsDisplayed()
    }

    @Test
    fun integratedMenuAction_opensAccessibleSelectionSummaryAndReturnsHome() {
        composeRule
            .onNodeWithTag("sample-menu-integrated")
            .performScrollTo()
            .performClick()

        composeRule
            .onNodeWithText("Integrated Sample")
            .assertIsDisplayed()
        composeRule
            .onNode(hasContentDescriptionStartingWith("Selected date,"))
            .assertIsDisplayed()

        composeRule
            .onNodeWithContentDescription("Back")
            .performClick()

        composeRule
            .onNodeWithText("Compose DateTimePicker")
            .assertIsDisplayed()
    }
}

private val sampleMenuTags = listOf(
    "sample-menu-integrated",
    "sample-menu-time-picker",
    "sample-menu-year-month-picker",
    "sample-menu-date-picker",
    "sample-menu-date-range-picker",
    "sample-menu-bottom-sheet",
    "sample-menu-background-style"
)

private fun hasContentDescriptionStartingWith(prefix: String): SemanticsMatcher {
    return SemanticsMatcher("content description starts with $prefix") { node ->
        node.config
            .getOrNull(SemanticsProperties.ContentDescription)
            ?.any { it.startsWith(prefix) } == true
    }
}
