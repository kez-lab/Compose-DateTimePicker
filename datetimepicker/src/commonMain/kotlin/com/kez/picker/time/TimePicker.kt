package com.kez.picker.time

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.kez.picker.Picker
import com.kez.picker.PickerDefaults
import com.kez.picker.PickerStyle
import com.kez.picker.TimePickerColumn
import com.kez.picker.TimePickerLayout
import com.kez.picker.TimePickerAccessibility
import com.kez.picker.TimePickerDisplay
import com.kez.picker.TimePickerItems
import com.kez.picker.pickerColumnModifier
import com.kez.picker.util.TimeFormat
import com.kez.picker.util.TimePeriod
import kotlinx.datetime.LocalTime

/**
 * A time picker component that allows the user to select hours, minutes, and—when using the 12-hour format—the AM/PM period.
 *
 * @param modifier The modifier to be applied to the component.
 * @param pickerModifier The modifier to be applied to each picker.
 * @param state The state object to control the picker.
 * @param onSelectedTimeChange Called after user interaction changes the selected time.
 * @param enabled Whether user scroll, click, and accessibility selection actions are enabled.
 * @param items Selectable minute, hour, and period item lists for the picker.
 * @param display Visible item text formatters for each picker column.
 * @param style Visual and layout styling for each picker column.
 * @param layout Column layout weights and visual order for each picker column.
 * @param spacingBetweenPickers The spacing between the pickers.
 * @param accessibility Accessibility labels, item descriptions, and custom action labels for each picker column.
 * @throws IllegalArgumentException if custom item lists are empty where required, contain duplicates, contain values outside the supported ranges, or omit the current selected value after time constraints are applied.
 */
@Composable
fun TimePicker(
    modifier: Modifier = Modifier,
    pickerModifier: Modifier = Modifier,
    state: TimePickerState = rememberTimePickerState(),
    onSelectedTimeChange: (LocalTime) -> Unit = {},
    enabled: Boolean = true,
    items: TimePickerItems = PickerDefaults.timePickerItems(),
    display: TimePickerDisplay = PickerDefaults.timePickerDisplay(),
    style: PickerStyle = PickerDefaults.style(),
    layout: TimePickerLayout = PickerDefaults.timePickerLayout(),
    spacingBetweenPickers: Dp = PickerDefaults.SpacingBetweenPickers,
    accessibility: TimePickerAccessibility = PickerDefaults.timePickerAccessibility()
) {
    validateTimePickerItems(
        state = state,
        items = items
    )

    fun moveSelectionInsideAvailableItems() {
        state.selectTime(
            time = items.coerceTime(
                time = state.selectedTime,
                timeFormat = state.timeFormat
            )
        )
    }

    fun updateSelectedTime(update: () -> Unit) {
        val previousTime = state.selectedTime
        update()
        moveSelectionInsideAvailableItems()
        val nextTime = state.selectedTime
        if (nextTime != previousTime) {
            onSelectedTimeChange(nextTime)
        }
    }

    Box(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    spacingBetweenPickers,
                    Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                layout.columnOrder.forEach { column ->
                    key(column) {
                        when (column) {
                            TimePickerColumn.PERIOD -> {
                                if (state.timeFormat == TimeFormat.HOUR_12) {
                                    val periodItems = items.selectablePeriodItems()
                                    Picker(
                                        items = periodItems,
                                        selectedItem = state.selectedPeriod,
                                        onSelectedItemChange = { period ->
                                            updateSelectedTime { state.selectPeriod(period) }
                                        },
                                        modifier = pickerColumnModifier(pickerModifier, layout.periodWeight),
                                        enabled = enabled,
                                        style = style,
                                        isInfinity = false,
                                        accessibility = accessibility.period,
                                        display = display.period
                                    )
                                }
                            }

                            TimePickerColumn.HOUR -> {
                                val hourItems = items.selectableHourItemsFor(
                                    timeFormat = state.timeFormat,
                                    period = state.selectedPeriod
                                )
                                Picker(
                                    items = hourItems,
                                    selectedItem = state.selectedHour,
                                    onSelectedItemChange = { hour ->
                                        updateSelectedTime { state.selectHour(hour) }
                                    },
                                    modifier = pickerColumnModifier(pickerModifier, layout.hourWeight),
                                    enabled = enabled,
                                    style = style,
                                    accessibility = accessibility.hour,
                                    display = display.hour
                                )
                            }

                            TimePickerColumn.MINUTE -> {
                                val minuteItems = items.selectableMinuteItemsFor(
                                    hourOfDay = state.selectedHourOfDay
                                )
                                Picker(
                                    items = minuteItems,
                                    selectedItem = state.selectedMinute,
                                    onSelectedItemChange = { minute ->
                                        updateSelectedTime { state.selectMinute(minute) }
                                    },
                                    modifier = pickerColumnModifier(pickerModifier, layout.minuteWeight),
                                    enabled = enabled,
                                    style = style,
                                    accessibility = accessibility.minute,
                                    display = display.minute
                                )
                            }
                        }
                    }
                }
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
    validateTimePickerItems(
        state = state,
        items = TimePickerItems(
            minuteItems = minuteItems,
            hour24Items = hourItems,
            hour12Items = hourItems,
            periodItems = periodItems
        )
    )
}

internal fun validateTimePickerItems(
    state: TimePickerState,
    items: TimePickerItems
) {
    val minuteItems = items.minuteItems
    val hourItems = items.hourItemsFor(state.timeFormat)
    val periodItems = items.periodItems

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

    val isHour12 = state.timeFormat == TimeFormat.HOUR_12
    val hourItemsName = if (isHour12) "hour12Items" else "hour24Items"
    val hourRange = if (isHour12) 1..12 else 0..23
    val hourRangeLabel = if (isHour12) "1, 12" else "0, 23"
    val invalidHours = hourItems.invalidValuesFor(hourRange)
    require(hourItems.isNotEmpty()) { "TimePicker $hourItemsName must not be empty." }
    require(hourItems.distinct().size == hourItems.size) {
        "TimePicker $hourItemsName must not contain duplicate values."
    }
    require(invalidHours.isEmpty()) {
        "TimePicker $hourItemsName must contain only values in range [$hourRangeLabel] " +
                "for timeFormat=${state.timeFormat}. Invalid values: $invalidHours"
    }
    require(state.selectedHour in hourItems) {
        "TimePicker $hourItemsName must contain state.selectedHour=${state.selectedHour} " +
                "for timeFormat=${state.timeFormat}."
    }

    if (isHour12) {
        require(periodItems.isNotEmpty()) {
            "TimePicker periodItems must not be empty for timeFormat=${TimeFormat.HOUR_12}."
        }
        require(periodItems.distinct().size == periodItems.size) {
            "TimePicker periodItems must not contain duplicate values."
        }
        require(state.selectedPeriod in periodItems) {
            "TimePicker periodItems must contain state.selectedPeriod=${state.selectedPeriod}."
        }
        val availablePeriodItems = items.selectablePeriodItems()
        require(state.selectedPeriod in availablePeriodItems) {
            "TimePicker constraints must allow state.selectedPeriod=${state.selectedPeriod}."
        }
    }

    val availableHourItems = items.selectableHourItemsFor(
        timeFormat = state.timeFormat,
        period = state.selectedPeriod
    )
    require(state.selectedHour in availableHourItems) {
        "TimePicker constraints must allow state.selectedHour=${state.selectedHour} " +
                "for timeFormat=${state.timeFormat}."
    }
    val availableMinuteItems = items.selectableMinuteItemsFor(
        hourOfDay = state.selectedHourOfDay
    )
    require(state.selectedMinute in availableMinuteItems) {
        "TimePicker minuteItems and constraints must allow state.selectedMinute=${state.selectedMinute} " +
                "for selectedHourOfDay=${state.selectedHourOfDay}."
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
