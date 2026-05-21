package com.kez.picker.sample

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
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
}

private val sampleMenuTags = listOf(
    "sample-menu-integrated",
    "sample-menu-time-picker",
    "sample-menu-year-month-picker",
    "sample-menu-date-picker",
    "sample-menu-bottom-sheet",
    "sample-menu-background-style"
)
