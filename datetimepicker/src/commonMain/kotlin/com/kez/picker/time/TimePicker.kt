package com.kez.picker.time

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.kez.picker.Picker
import com.kez.picker.PickerDefaults
import com.kez.picker.PickerStyle
import com.kez.picker.util.HOUR12_RANGE
import com.kez.picker.util.HOUR24_RANGE
import com.kez.picker.util.MINUTE_RANGE
import com.kez.picker.util.TimeFormat
import com.kez.picker.util.TimePeriod

/**
 * A time picker component that allows the user to select hours, minutes, and—when using the 12-hour format—the AM/PM period.
 *
 * @param modifier The modifier to be applied to the component.
 * @param pickerModifier The modifier to be applied to each picker.
 * @param state The state object to control the picker.
 * @param minuteItems The list of minute values to display. Must be non-empty, distinct, contain values in 0..59, and contain [TimePickerState.selectedMinute].
 * @param hourItems The list of hour values to display. Must be non-empty, distinct, contain display-hour values in 1..12 for [TimeFormat.HOUR_12] or 0..23 for [TimeFormat.HOUR_24], and contain [TimePickerState.selectedHour].
 * @param periodItems The list of period values to display in [TimeFormat.HOUR_12]. Must be non-empty, distinct, and contain [TimePickerState.selectedPeriod] when the picker uses 12-hour time.
 * @param style Visual and layout styling for each picker column.
 * @param spacingBetweenPickers The spacing between the pickers.
 * @param hourPickerLabel Accessibility label for the hour picker. Pass null to omit the picker label prefix.
 * @param minutePickerLabel Accessibility label for the minute picker. Pass null to omit the picker label prefix.
 * @param periodPickerLabel Accessibility label for the AM/PM picker in [TimeFormat.HOUR_12]. Pass null to omit the picker label prefix.
 * @param hourItemContentDescription Accessibility description for each hour value.
 * @param minuteItemContentDescription Accessibility description for each minute value.
 * @param periodItemContentDescription Accessibility description for each AM/PM value in [TimeFormat.HOUR_12].
 * @param previousItemActionLabel Accessibility action label used by child pickers to select the previous item. Pass null or blank to omit the action.
 * @param nextItemActionLabel Accessibility action label used by child pickers to select the next item. Pass null or blank to omit the action.
 * @throws IllegalArgumentException if custom item lists are empty where required, contain duplicates, contain values outside the supported ranges, or omit the current selected value.
 */
@Composable
fun TimePicker(
    modifier: Modifier = Modifier,
    pickerModifier: Modifier = Modifier,
    state: TimePickerState = rememberTimePickerState(),
    minuteItems: List<Int> = MINUTE_RANGE,
    hourItems: List<Int> = when (state.timeFormat) {
        TimeFormat.HOUR_12 -> HOUR12_RANGE
        TimeFormat.HOUR_24 -> HOUR24_RANGE
    },
    periodItems: List<TimePeriod> = TimePeriod.entries,
    style: PickerStyle = PickerDefaults.style(),
    spacingBetweenPickers: Dp = PickerDefaults.SpacingBetweenPickers,
    hourPickerLabel: String? = "Hour",
    minutePickerLabel: String? = "Minute",
    periodPickerLabel: String? = "AM/PM",
    hourItemContentDescription: (Int) -> String = { it.toString() },
    minuteItemContentDescription: (Int) -> String = { it.toString() },
    periodItemContentDescription: (TimePeriod) -> String = { it.name },
    previousItemActionLabel: String? = PickerDefaults.PreviousItemActionLabel,
    nextItemActionLabel: String? = PickerDefaults.NextItemActionLabel
) {
    validateTimePickerItems(
        state = state,
        minuteItems = minuteItems,
        hourItems = hourItems,
        periodItems = periodItems
    )

    Box(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (state.timeFormat == TimeFormat.HOUR_12) {
                    Picker(
                        items = periodItems,
                        selectedItem = state.selectedPeriod,
                        onSelectedItemChange = state::selectPeriod,
                        modifier = pickerModifier.weight(1f),
                        style = style,
                        isInfinity = false,
                        pickerLabel = periodPickerLabel,
                        itemContentDescription = periodItemContentDescription,
                        previousItemActionLabel = previousItemActionLabel,
                        nextItemActionLabel = nextItemActionLabel
                    )
                    Spacer(modifier = Modifier.width(spacingBetweenPickers))
                }
                Picker(
                    items = hourItems,
                    selectedItem = state.selectedHour,
                    onSelectedItemChange = state::selectHour,
                    modifier = pickerModifier.weight(1f),
                    style = style,
                    pickerLabel = hourPickerLabel,
                    itemContentDescription = hourItemContentDescription,
                    previousItemActionLabel = previousItemActionLabel,
                    nextItemActionLabel = nextItemActionLabel
                )
                Spacer(modifier = Modifier.width(spacingBetweenPickers))
                Picker(
                    items = minuteItems,
                    selectedItem = state.selectedMinute,
                    onSelectedItemChange = state::selectMinute,
                    modifier = pickerModifier.weight(1f),
                    style = style,
                    pickerLabel = minutePickerLabel,
                    itemContentDescription = minuteItemContentDescription,
                    previousItemActionLabel = previousItemActionLabel,
                    nextItemActionLabel = nextItemActionLabel
                )
            }
        }
    }
}

internal fun validateTimePickerItems(
    state: TimePickerState,
    minuteItems: List<Int>,
    hourItems: List<Int>,
    periodItems: List<TimePeriod>
) {
    val minuteRange = 0..59
    val invalidMinutes = minuteItems.invalidValuesFor(minuteRange)
    require(minuteItems.isNotEmpty()) { "TimePicker minuteItems must not be empty." }
    require(minuteItems.distinct().size == minuteItems.size) {
        "TimePicker minuteItems must not contain duplicate values."
    }
    require(invalidMinutes.isEmpty()) {
        "TimePicker minuteItems must contain only values in range [0, 59]. " +
                "Invalid values: $invalidMinutes"
    }
    require(state.selectedMinute in minuteItems) {
        "TimePicker minuteItems must contain state.selectedMinute=${state.selectedMinute}."
    }

    val hourRange = if (state.timeFormat == TimeFormat.HOUR_12) 1..12 else 0..23
    val hourRangeLabel = if (state.timeFormat == TimeFormat.HOUR_12) "1, 12" else "0, 23"
    val invalidHours = hourItems.invalidValuesFor(hourRange)
    require(hourItems.isNotEmpty()) { "TimePicker hourItems must not be empty." }
    require(hourItems.distinct().size == hourItems.size) {
        "TimePicker hourItems must not contain duplicate values."
    }
    require(invalidHours.isEmpty()) {
        "TimePicker hourItems must contain only values in range [$hourRangeLabel] " +
                "for timeFormat=${state.timeFormat}. Invalid values: $invalidHours"
    }
    require(state.selectedHour in hourItems) {
        "TimePicker hourItems must contain state.selectedHour=${state.selectedHour} for timeFormat=${state.timeFormat}."
    }

    if (state.timeFormat == TimeFormat.HOUR_12) {
        require(periodItems.isNotEmpty()) {
            "TimePicker periodItems must not be empty for timeFormat=${TimeFormat.HOUR_12}."
        }
        require(periodItems.distinct().size == periodItems.size) {
            "TimePicker periodItems must not contain duplicate values."
        }
        require(state.selectedPeriod in periodItems) {
            "TimePicker periodItems must contain state.selectedPeriod=${state.selectedPeriod}."
        }
    }
}

private fun List<Int>.invalidValuesFor(range: IntRange): List<Int> =
    filterNot { it in range }.distinct()

@Preview(name = "24-Hour Format", group = "TimePicker - Formats", showBackground = true)
@Composable
private fun TimePickerPreview24Hour() {
    TimePicker(
        state = rememberTimePickerState(timeFormat = TimeFormat.HOUR_24)
    )
}

@Preview(name = "12-Hour Format with AM/PM", group = "TimePicker - Formats", showBackground = true)
@Composable
private fun TimePickerPreview12Hour() {
    TimePicker(
        state = rememberTimePickerState(timeFormat = TimeFormat.HOUR_12)
    )
}

@Preview(name = "No Divider", group = "TimePicker - Styles", showBackground = true)
@Composable
private fun TimePickerNoDividerPreview() {
    TimePicker(
        state = rememberTimePickerState(timeFormat = TimeFormat.HOUR_24),
        style = PickerDefaults.style(isDividerVisible = false)
    )
}

@Preview(name = "Custom Colors", group = "TimePicker - Styles", showBackground = true)
@Composable
private fun TimePickerCustomColorsPreview() {
    TimePicker(
        state = rememberTimePickerState(timeFormat = TimeFormat.HOUR_12),
        style = PickerDefaults.style(
            colors = PickerDefaults.colors(
                textColor = Color.Gray,
                selectedTextColor = Color(0xFF6200EE),
                dividerColor = Color(0xFF6200EE)
            )
        )
    )
}

@Preview(name = "Large Text Size", group = "TimePicker - Styles", showBackground = true)
@Composable
private fun TimePickerLargeTextPreview() {
    TimePicker(
        state = rememberTimePickerState(timeFormat = TimeFormat.HOUR_24),
        style = PickerDefaults.style(
            textStyles = PickerDefaults.textStyles(
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 20.sp),
                selectedTextStyle = androidx.compose.ui.text.TextStyle(fontSize = 28.sp)
            ),
            visibleItemsCount = 5
        )
    )
}
