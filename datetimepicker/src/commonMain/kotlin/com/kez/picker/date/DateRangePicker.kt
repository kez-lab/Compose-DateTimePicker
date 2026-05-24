package com.kez.picker.date

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kez.picker.DatePickerDisplay
import com.kez.picker.DatePickerItems
import com.kez.picker.DatePickerLayout
import com.kez.picker.DateRangePickerAccessibility
import com.kez.picker.PickerDefaults
import com.kez.picker.PickerStyle
import kotlinx.datetime.LocalDate

/**
 * A date range picker composed from two ordered [DatePicker] components.
 *
 * @param modifier The modifier to be applied to the component.
 * @param pickerModifier The modifier to be applied to each child picker column.
 * @param state The state object to control the selected date range.
 * @param onSelectedDateRangeChange Called after user interaction changes the selected range.
 * @param enabled Whether user scroll, click, and accessibility selection actions are enabled.
 * @param items Selectable year/month/day item lists plus optional inclusive date bounds.
 * @param display Visible item text formatters for each picker column.
 * @param style Visual and layout styling for each picker column.
 * @param layout Column layout weights and visual order for each child [DatePicker].
 * @param spacingBetweenPickers Horizontal spacing between columns inside each child [DatePicker].
 * @param spacingBetweenDatePickers Vertical spacing between start and end pickers.
 * @param startLabel Optional visible label displayed above the start picker.
 * @param endLabel Optional visible label displayed above the end picker.
 * @param accessibility Accessibility labels for the start and end pickers.
 */
@Composable
fun DateRangePicker(
    modifier: Modifier = Modifier,
    pickerModifier: Modifier = Modifier,
    state: DateRangePickerState = rememberDateRangePickerState(),
    onSelectedDateRangeChange: (DateRange) -> Unit = {},
    enabled: Boolean = true,
    items: DatePickerItems = PickerDefaults.datePickerItems(),
    display: DatePickerDisplay = PickerDefaults.datePickerDisplay(),
    style: PickerStyle = PickerDefaults.style(),
    layout: DatePickerLayout = PickerDefaults.datePickerLayout(),
    spacingBetweenPickers: Dp = PickerDefaults.SpacingBetweenPickers,
    spacingBetweenDatePickers: Dp = 16.dp,
    startLabel: (@Composable () -> Unit)? = { DateRangePickerLabel("Start date") },
    endLabel: (@Composable () -> Unit)? = { DateRangePickerLabel("End date") },
    accessibility: DateRangePickerAccessibility = PickerDefaults.dateRangePickerAccessibility()
) {
    fun updateSelectedDateRange(update: () -> Unit) {
        val previousRange = state.selectedDateRange
        update()
        val nextRange = state.selectedDateRange
        if (nextRange != previousRange) {
            onSelectedDateRangeChange(nextRange)
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacingBetweenDatePickers)
    ) {
        DateRangePickerSection(label = startLabel) {
            DatePicker(
                pickerModifier = pickerModifier,
                state = state.startDatePickerState,
                onSelectedDateChange = { date: LocalDate ->
                    updateSelectedDateRange {
                        state.selectStartDate(date, items)
                    }
                },
                enabled = enabled,
                items = items,
                display = display,
                style = style,
                layout = layout,
                spacingBetweenPickers = spacingBetweenPickers,
                accessibility = accessibility.start
            )
        }

        DateRangePickerSection(label = endLabel) {
            DatePicker(
                pickerModifier = pickerModifier,
                state = state.endDatePickerState,
                onSelectedDateChange = { date: LocalDate ->
                    updateSelectedDateRange {
                        state.selectEndDate(date, items)
                    }
                },
                enabled = enabled,
                items = items,
                display = display,
                style = style,
                layout = layout,
                spacingBetweenPickers = spacingBetweenPickers,
                accessibility = accessibility.end
            )
        }
    }
}

@Composable
private fun DateRangePickerSection(
    label: (@Composable () -> Unit)?,
    content: @Composable () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        label?.invoke()
        content()
    }
}

@Composable
private fun DateRangePickerLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}
