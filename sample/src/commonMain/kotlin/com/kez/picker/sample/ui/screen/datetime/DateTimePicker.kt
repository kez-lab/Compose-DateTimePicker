package com.kez.picker.sample.ui.screen.datetime

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kez.picker.Picker
import com.kez.picker.PickerDefaults
import com.kez.picker.PickerItemFormat
import com.kez.picker.PickerSemantics
import com.kez.picker.PickerStyle
import kotlinx.datetime.number

@Composable
internal fun DateTimePicker(
    state: DateTimePickerState,
    items: DateTimePickerItems,
    onSelectedDateTimeChange: (kotlinx.datetime.LocalDateTime) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    layout: DateTimePickerLayout = DateTimePickerLayout(),
    spacingBetweenPickers: Dp = 2.dp,
    style: PickerStyle = PickerDefaults.style()
) {
    val selectedDateTime = state.selectedDateTime
    remember(items, state, selectedDateTime) {
        require(items.contains(selectedDateTime)) {
            "DateTimePicker selectedDateTime=$selectedDateTime must be present in items. " +
                    "Create state with rememberDateTimePickerState(items, ...) or call " +
                    "state.selectDateTime(value, items) before composing."
        }
    }

    fun commit(nextDateTime: kotlinx.datetime.LocalDateTime) {
        commitDateTimeSelection(
            state = state,
            nextDateTime = nextDateTime,
            onSelectionCommitted = onSelectedDateTimeChange
        )
    }

    val fontScale = LocalDensity.current.fontScale.coerceAtLeast(1f)
    val horizontalScrollState = rememberScrollState()
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .horizontalScroll(
                    state = horizontalScrollState,
                    enabled = enabled
                )
                .widthIn(min = maxWidth),
            horizontalArrangement = Arrangement.spacedBy(
                space = spacingBetweenPickers,
                alignment = Alignment.CenterHorizontally
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            layout.columnOrder.forEach { column ->
                val columnItems = remember(items, selectedDateTime, column) {
                    items.selectableItemsFor(
                        column = column,
                        currentDateTime = selectedDateTime
                    )
                }
                val format = remember(column) { dateTimeColumnFormat(column) }
                val semantics = remember(column) { dateTimeColumnSemantics(column) }

                key(column, state, items, selectedDateTime, enabled) {
                    Column(
                        modifier = Modifier.width(column.minimumWidth(fontScale)),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = column.header,
                            modifier = Modifier.clearAndSetSemantics { },
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Picker(
                            items = columnItems,
                            selectedItem = selectedDateTime.valueForPicker(column),
                            onSelectedItemChange = { value ->
                                commit(
                                    items.repairedDateTimeAfter(
                                        currentDateTime = state.selectedDateTime,
                                        column = column,
                                        value = value
                                    )
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = enabled,
                            format = format,
                            style = style,
                            semantics = semantics,
                            isInfinity = false
                        )
                    }
                }
            }
        }
    }
}

private fun dateTimeColumnFormat(
    column: DateTimePickerColumn
): PickerItemFormat<Int> = PickerDefaults.itemFormat(
    itemText = { value ->
        if (column == DateTimePickerColumn.YEAR) {
            value.toString()
        } else {
            value.toString().padStart(length = 2, padChar = '0')
        }
    },
    itemContentDescription = Int::toString
)

private fun dateTimeColumnSemantics(
    column: DateTimePickerColumn
): PickerSemantics = PickerDefaults.semantics(
    pickerLabel = "Date-time ${column.name.lowercase()}",
    previousItemActionLabel = "Select previous ${column.name.lowercase()}",
    nextItemActionLabel = "Select next ${column.name.lowercase()}"
)

private fun kotlinx.datetime.LocalDateTime.valueForPicker(
    column: DateTimePickerColumn
): Int = when (column) {
    DateTimePickerColumn.YEAR -> year
    DateTimePickerColumn.MONTH -> month.number
    DateTimePickerColumn.DAY -> day
    DateTimePickerColumn.HOUR -> hour
    DateTimePickerColumn.MINUTE -> minute
}

private fun DateTimePickerColumn.minimumWidth(fontScale: Float): Dp {
    val baseWidth = if (this == DateTimePickerColumn.YEAR) 72f else 56f
    return (baseWidth * fontScale).dp
}

private val DateTimePickerColumn.header: String
    get() = when (this) {
        DateTimePickerColumn.YEAR -> "YYYY"
        DateTimePickerColumn.MONTH -> "MM"
        DateTimePickerColumn.DAY -> "DD"
        DateTimePickerColumn.HOUR -> "HH"
        DateTimePickerColumn.MINUTE -> "mm"
    }
