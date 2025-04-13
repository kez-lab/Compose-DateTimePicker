package com.kez.picker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * Remember a [PickerState] with the given initial item.
 * 
 * @param initialItem The initial selected item.
 * @return A [PickerState] with the given initial item.
 */
@Composable
fun <T> rememberPickerState(initialItem: T) = remember { PickerState(initialItem) }

/**
 * State holder for the picker component.
 *
 * @param initialItem The initial selected item.
 */
class PickerState<T>(
    initialItem: T
) {
    /**
     * The currently selected item.
     */
    var selectedItem by mutableStateOf(initialItem)
} 