package com.kez.picker

import androidx.compose.foundation.layout.RowScope
import androidx.compose.ui.Modifier

internal fun RowScope.pickerColumnModifier(
    baseModifier: Modifier,
    weight: Float?
): Modifier = if (weight == null) {
    baseModifier
} else {
    baseModifier.weight(weight)
}
