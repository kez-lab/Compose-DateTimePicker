package com.kez.picker.duration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.kez.picker.DurationPickerColumn
import com.kez.picker.DurationPickerFormat
import com.kez.picker.DurationPickerItems
import com.kez.picker.DurationPickerLayout
import com.kez.picker.DurationPickerSemantics
import com.kez.picker.Picker
import com.kez.picker.PickerDefaults
import com.kez.picker.PickerSelectionBand
import com.kez.picker.PickerSelectionIndicator
import com.kez.picker.PickerStyle
import com.kez.picker.commitMultiWheelSelection
import com.kez.picker.maxPickerItemHeight
import com.kez.picker.pickerColumnModifier
import com.kez.picker.rememberPickerItemHeight
import kotlin.time.Duration

/**
 * Selects a finite, non-negative, whole-minute [Duration] with elapsed-hour and minute columns.
 *
 * Each user-settled column change is repaired against the combined scalar constraints before one
 * logical value is committed to [state]. Programmatic [DurationPickerState.selectDuration] calls
 * update state without invoking [onSelectedDurationChange] and replace any in-flight child
 * interaction generation.
 *
 * @param modifier Modifier applied to the whole component.
 * @param pickerModifier Modifier applied to each child picker column.
 * @param state Saveable logical duration state.
 * @param onSelectedDurationChange Called once after a user interaction settles, dependent values
 * are repaired, and the final selectable duration has been committed to [state].
 * @param enabled Whether user scroll, click, and semantics actions are enabled.
 * @param items Selectable elapsed-hour/minute sources and inclusive scalar constraints.
 * @param format Visible text and optional value content descriptions for both columns.
 * @param style Visual and layout styling for each child column.
 * @param selectionIndicator Shared selection band drawn across the whole component.
 * @param layout Column weights and visual order.
 * @param spacingBetweenPickers Horizontal spacing between child columns.
 * @param semantics Accessibility labels and action labels for both columns.
 * @throws IllegalArgumentException if item sources are empty, duplicated, outside supported ranges,
 * have no scalar combination allowed by constraints, or do not contain the current selection.
 */
@Composable
fun DurationPicker(
    modifier: Modifier = Modifier,
    pickerModifier: Modifier = Modifier,
    state: DurationPickerState = rememberDurationPickerState(),
    onSelectedDurationChange: (Duration) -> Unit = {},
    enabled: Boolean = true,
    items: DurationPickerItems = PickerDefaults.durationPickerItems(),
    format: DurationPickerFormat = PickerDefaults.durationPickerFormat(),
    style: PickerStyle = PickerDefaults.style(),
    selectionIndicator: PickerSelectionIndicator = PickerDefaults.selectionIndicator(style),
    layout: DurationPickerLayout = PickerDefaults.durationPickerLayout(),
    spacingBetweenPickers: Dp = PickerDefaults.SpacingBetweenPickers,
    semantics: DurationPickerSemantics = PickerDefaults.durationPickerSemantics()
) {
    val columnStyle = remember(style) { style.copy(isDividerVisible = false) }

    remember(items) { items.requireValid() }
    remember(items, state, state.selectedDuration) {
        require(items.contains(state.selectedDuration)) {
            "DurationPicker selectedDuration=${state.selectedDuration} must be present in items " +
                    "and allowed by constraints. Create state with " +
                    "rememberDurationPickerState(items = items, initialDuration = ...) or call " +
                    "state.selectDuration(value, items) before composing."
        }
    }

    fun commitColumnChange(column: DurationPickerColumn, value: Int) {
        val currentDuration = state.selectedDuration
        val nextDuration = items.repairedDurationAfter(
            currentDuration = currentDuration,
            column = column,
            value = value
        )
        commitMultiWheelSelection(
            currentState = currentDuration,
            nextState = nextDuration,
            commitState = state::selectDuration,
            onSelectionCommitted = onSelectedDurationChange
        )
    }

    val hourItems by remember(items) {
        derivedStateOf { items.selectableHourItems() }
    }
    val minuteItems by remember(items, state) {
        derivedStateOf { items.selectableMinuteItemsFor(state.selectedHours) }
    }
    val hourItemHeight = rememberPickerItemHeight(
        items = hourItems,
        format = format.hour,
        style = columnStyle
    )
    val minuteItemHeight = rememberPickerItemHeight(
        items = minuteItems,
        format = format.minute,
        style = columnStyle
    )

    Box(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            PickerSelectionBand(
                indicator = selectionIndicator,
                itemHeight = maxPickerItemHeight(hourItemHeight, minuteItemHeight),
                enabled = enabled,
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
                        key(column, state, items, state.selectedDuration) {
                            when (column) {
                                DurationPickerColumn.HOUR -> Picker(
                                    items = hourItems,
                                    selectedItem = state.selectedHours,
                                    onSelectedItemChange = { hour ->
                                        commitColumnChange(DurationPickerColumn.HOUR, hour)
                                    },
                                    modifier = pickerColumnModifier(
                                        pickerModifier,
                                        layout.hourWeight
                                    ),
                                    enabled = enabled,
                                    format = format.hour,
                                    style = columnStyle,
                                    semantics = semantics.hour
                                )

                                DurationPickerColumn.MINUTE -> Picker(
                                    items = minuteItems,
                                    selectedItem = state.selectedMinutes,
                                    onSelectedItemChange = { minute ->
                                        commitColumnChange(DurationPickerColumn.MINUTE, minute)
                                    },
                                    modifier = pickerColumnModifier(
                                        pickerModifier,
                                        layout.minuteWeight
                                    ),
                                    enabled = enabled,
                                    format = format.minute,
                                    style = columnStyle,
                                    semantics = semantics.minute
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
