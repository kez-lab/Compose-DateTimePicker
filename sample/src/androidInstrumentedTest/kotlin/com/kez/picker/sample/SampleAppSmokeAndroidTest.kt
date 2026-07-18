package com.kez.picker.sample

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.isSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import com.kez.picker.date.DateRange
import com.kez.picker.util.currentDate
import kotlinx.datetime.LocalDate
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
            scrollToSampleMenuItem(tag)
            composeRule
                .onNodeWithTag(tag)
                .assertIsDisplayed()
                .assertHasClickAction()
        }
    }

    @Test
    fun datePickerMenuAction_opensDatePickerSampleAndReturnsHome() {
        scrollToSampleMenuItem("sample-menu-date-picker")
        composeRule
            .onNodeWithTag("sample-menu-date-picker")
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
    fun wheelPickerMenuAction_opensLiveAndSettledSelectionSample() {
        scrollToSampleMenuItem("sample-menu-wheel-picker")
        composeRule
            .onNodeWithTag("sample-menu-wheel-picker")
            .performClick()

        composeRule
            .onNodeWithText("WheelPicker Sample")
            .assertIsDisplayed()
        composeRule
            .onNodeWithText("Set 8 seats programmatically")
            .assertIsDisplayed()
    }

    @Test
    fun dateTimePickerMenuAction_opensFiveColumnExactCandidateSample() {
        scrollToSampleMenuItem("sample-menu-date-time-picker")
        composeRule
            .onNodeWithTag("sample-menu-date-time-picker")
            .performClick()

        composeRule
            .onNodeWithText("Exact Date-Time Slots")
            .assertIsDisplayed()
        composeRule
            .onNodeWithText("Set Mar 1, 00:30 programmatically")
            .assertIsDisplayed()
        composeRule
            .onNodeWithText("Reset Feb 28, 23:30")
            .assertIsDisplayed()
    }

    @Test
    fun dateTimePicker_monthRepairCommitsOnceAndProgrammaticResetIsCallbackFree() {
        scrollToSampleMenuItem("sample-menu-date-time-picker")
        composeRule
            .onNodeWithTag("sample-menu-date-time-picker")
            .performClick()

        composeRule
            .onNodeWithContentDescription("Date-time month: 3")
            .performClick()

        composeRule
            .onNode(
                SemanticsMatcher.expectValue(
                    SemanticsProperties.ContentDescription,
                    listOf(
                        "Selected date-time, 2026-03-01 00:00, " +
                                "User callbacks: 1 · Last: 2026-03-01 00:00"
                    )
                )
            )
            .assertIsDisplayed()
        composeRule
            .onNode(hasContentDescription("Date-time month: 3") and isSelected())
            .assertIsDisplayed()
        composeRule
            .onNode(hasContentDescription("Date-time day: 1") and isSelected())
            .assertIsDisplayed()
        composeRule
            .onNode(hasContentDescription("Date-time hour: 0") and isSelected())
            .assertIsDisplayed()
        composeRule
            .onNode(hasContentDescription("Date-time minute: 0") and isSelected())
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("Reset Feb 28, 23:30")
            .performClick()

        composeRule
            .onNode(
                SemanticsMatcher.expectValue(
                    SemanticsProperties.ContentDescription,
                    listOf(
                        "Selected date-time, 2026-02-28 23:30, " +
                                "User callbacks: 1 · Last: 2026-03-01 00:00"
                    )
                )
            )
            .assertIsDisplayed()
        composeRule
            .onNode(hasContentDescription("Date-time month: 2") and isSelected())
            .assertIsDisplayed()
        composeRule
            .onNode(hasContentDescription("Date-time day: 28") and isSelected())
            .assertIsDisplayed()
        composeRule
            .onNode(hasContentDescription("Date-time hour: 23") and isSelected())
            .assertIsDisplayed()
        composeRule
            .onNode(hasContentDescription("Date-time minute: 30") and isSelected())
            .assertIsDisplayed()
    }

    @Test
    fun timePickerMenuAction_opensTimePickerSampleAndRendersDefaultTab() {
        scrollToSampleMenuItem("sample-menu-time-picker")
        composeRule
            .onNodeWithTag("sample-menu-time-picker")
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
    fun durationPickerMenuAction_opensBoundedScalarSample() {
        scrollToSampleMenuItem("sample-menu-duration-picker")
        composeRule
            .onNodeWithTag("sample-menu-duration-picker")
            .performClick()

        composeRule
            .onNodeWithText("DurationPicker Sample")
            .assertIsDisplayed()
        composeRule
            .onNodeWithText("Set 90 min")
            .assertIsDisplayed()
        composeRule
            .onNodeWithText("Reset 45 min")
            .assertIsDisplayed()
    }

    @Test
    fun quantityUnitMenuAction_opensDependentSourceSample() {
        scrollToSampleMenuItem("sample-menu-quantity-unit-picker")
        composeRule
            .onNodeWithTag("sample-menu-quantity-unit-picker")
            .performClick()

        composeRule
            .onNodeWithText("Quantity + Unit Sample")
            .assertIsDisplayed()
        composeRule
            .onNodeWithText("Set 2 kg programmatically")
            .assertIsDisplayed()
        composeRule
            .onNodeWithText("Reset 2,500 g")
            .assertIsDisplayed()
    }

    @Test
    fun quantityUnitPicker_unitRepairCommitsOnceAndProgrammaticResetIsCallbackFree() {
        scrollToSampleMenuItem("sample-menu-quantity-unit-picker")
        composeRule
            .onNodeWithTag("sample-menu-quantity-unit-picker")
            .performClick()

        composeRule
            .onNodeWithContentDescription("Mass unit: Kilogram")
            .performClick()

        composeRule
            .onNode(
                SemanticsMatcher.expectValue(
                    SemanticsProperties.ContentDescription,
                    listOf(
                        "Selected mass, 2 kg, Normalized: 2000 g · " +
                                "User callbacks: 1 · Last: 2 kg"
                    )
                )
            )
            .assertIsDisplayed()
        composeRule
            .onNodeWithText("2 kg")
            .assertIsDisplayed()
        composeRule
            .onNode(
                hasContentDescription("Quantity in kilograms: 2 kilograms") and isSelected()
            )
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("Reset 2,500 g")
            .performClick()

        composeRule
            .onNode(
                SemanticsMatcher.expectValue(
                    SemanticsProperties.ContentDescription,
                    listOf(
                        "Selected mass, 2500 g, Normalized: 2500 g · " +
                                "User callbacks: 1 · Last: 2 kg"
                    )
                )
            )
            .assertIsDisplayed()
        composeRule
            .onNodeWithText("2500 g")
            .assertIsDisplayed()
        composeRule
            .onNode(hasContentDescription("Mass unit: Gram") and isSelected())
            .assertIsDisplayed()
        composeRule
            .onNode(
                hasContentDescription("Quantity in grams: 2500 grams") and isSelected()
            )
            .assertIsDisplayed()
    }

    @Test
    fun dateRangePickerMenuAction_updatesSelectedRangeFromButtons() {
        val today = currentDate()
        val expectedTodaySummary = "Selected range, $today..$today, Single day. Days selected: 1. " +
                "Last user change: No user change. Selectable year: ${today.year}"
        val yearStart = LocalDate(today.year, 1, 1)
        val yearEnd = LocalDate(today.year, 12, 31)
        val expectedYearRange = DateRange(startDate = yearStart, endDate = yearEnd)
        val expectedYearSummary = "Selected range, $yearStart..$yearEnd, Date range. " +
                "Days selected: ${expectedYearRange.dayCount}. Last user change: No user change. " +
                "Selectable year: ${today.year}"

        scrollToSampleMenuItem("sample-menu-date-range-picker")
        composeRule
            .onNodeWithTag("sample-menu-date-range-picker")
            .performClick()

        composeRule
            .onNodeWithText("DateRangePicker Sample")
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("Today only")
            .performClick()

        composeRule
            .onNode(hasContentDescriptionStartingWith(expectedTodaySummary))
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("Year bounds")
            .performClick()

        composeRule
            .onNode(hasContentDescriptionStartingWith(expectedYearSummary))
            .assertIsDisplayed()
    }

    @Test
    fun integratedMenuAction_opensAccessibleSelectionSummaryAndReturnsHome() {
        scrollToSampleMenuItem("sample-menu-integrated")
        composeRule
            .onNodeWithTag("sample-menu-integrated")
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

    private fun scrollToSampleMenuItem(tag: String) {
        composeRule
            .onNodeWithTag("sample-menu-list")
            .performScrollToNode(hasTestTag(tag))
    }
}

private val sampleMenuTags = listOf(
    "sample-menu-wheel-picker",
    "sample-menu-date-time-picker",
    "sample-menu-integrated",
    "sample-menu-time-picker",
    "sample-menu-duration-picker",
    "sample-menu-quantity-unit-picker",
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
