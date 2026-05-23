package com.kez.picker

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.isSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.kez.picker.date.DatePicker
import com.kez.picker.date.rememberDatePickerState
import com.kez.picker.date.rememberYearMonthPickerState
import com.kez.picker.date.YearMonthPicker
import com.kez.picker.time.TimePicker
import com.kez.picker.time.rememberTimePickerState
import com.kez.picker.util.TimeFormat
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.junit.Rule
import org.junit.Test

class PickerDisplayAndroidTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun picker_usesCustomVisibleTextSeparatelyFromAccessibilityDescription() {
        composeRule.setContent {
            var selectedItem by remember { mutableStateOf(2) }

            Picker(
                items = listOf(1, 2, 3),
                selectedItem = selectedItem,
                onSelectedItemChange = { selectedItem = it },
                style = PickerDefaults.style(visibleItemsCount = 3),
                isInfinity = false,
                display = PickerDefaults.itemText(
                    itemText = { "No. $it" }
                ),
                accessibility = PickerDefaults.accessibility(
                    pickerLabel = "Number",
                    itemContentDescription = { "value $it" }
                )
            )
        }

        composeRule.onNodeWithText("No. 2").assertExists()
        composeRule
            .onNode(hasContentDescription("Number: value 2") and isSelected())
            .assertExists()
    }

    @Test
    fun timePicker_forwardsCustomDisplayTextToChildPickers() {
        composeRule.setContent {
            val items = PickerDefaults.timePickerItems(
                hour24Items = listOf(9),
                minuteItems = listOf(5)
            )
            val state = rememberTimePickerState(
                items = items,
                initialTime = LocalTime(hour = 9, minute = 5),
                timeFormat = TimeFormat.HOUR_24
            )

            TimePicker(
                state = state,
                items = items,
                style = PickerDefaults.style(visibleItemsCount = 1),
                display = PickerDefaults.timePickerDisplay(
                    hourItemText = { it.toString().padStart(length = 2, padChar = '0') },
                    minuteItemText = { it.toString().padStart(length = 2, padChar = '0') }
                )
            )
        }

        composeRule.onNodeWithText("09").assertExists()
        composeRule.onNodeWithText("05").assertExists()
    }

    @Test
    fun datePicker_forwardsCustomDisplayTextToChildPickers() {
        composeRule.setContent {
            val items = PickerDefaults.datePickerItems(
                yearItems = listOf(2026),
                monthItems = listOf(5),
                dayItems = listOf(20)
            )
            val state = rememberDatePickerState(
                items = items,
                initialDate = LocalDate(year = 2026, month = 5, day = 20)
            )

            DatePicker(
                state = state,
                items = items,
                style = PickerDefaults.style(visibleItemsCount = 1),
                display = PickerDefaults.datePickerDisplay(
                    yearItemText = { "${it}년" },
                    monthItemText = { "${it}월" },
                    dayItemText = { "${it}일" }
                )
            )
        }

        composeRule.onNodeWithText("2026년").assertExists()
        composeRule.onNodeWithText("5월").assertExists()
        composeRule.onNodeWithText("20일").assertExists()
    }

    @Test
    fun yearMonthPicker_forwardsCustomDisplayTextToChildPickers() {
        composeRule.setContent {
            val items = PickerDefaults.yearMonthPickerItems(
                yearItems = listOf(2026),
                monthItems = listOf(5)
            )
            val state = rememberYearMonthPickerState(
                items = items,
                initialDate = LocalDate(year = 2026, month = 5, day = 1)
            )

            YearMonthPicker(
                state = state,
                items = items,
                style = PickerDefaults.style(visibleItemsCount = 1),
                display = PickerDefaults.yearMonthPickerDisplay(
                    yearItemText = { "${it}년" },
                    monthItemText = { "May" }
                )
            )
        }

        composeRule.onNodeWithText("2026년").assertExists()
        composeRule.onNodeWithText("May").assertExists()
    }
}
