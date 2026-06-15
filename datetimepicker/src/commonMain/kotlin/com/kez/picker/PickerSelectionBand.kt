package com.kez.picker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints

/**
 * Alpha multiplier applied to the selection band color when the composite picker is disabled.
 * Matches the disabled container alpha used by [PickerDefaults.colors].
 */
private const val DISABLED_BAND_ALPHA: Float = 0.12f

/**
 * Wraps a composite picker's column [content] (the row of child pickers) and overlays a single
 * selection band spanning the whole picker width.
 *
 * The band height is derived from the measured content height divided by [visibleItemsCount], which
 * equals the per-row item height shared by every column. This keeps the band aligned with the
 * centered selection row without re-measuring item text, and stays correct for unequal column
 * widths and any column spacing.
 *
 * @param indicator The selection band appearance.
 * @param visibleItemsCount The number of visible rows in each child picker column.
 * @param enabled Whether the composite picker is enabled. Disabled pickers dim the band color.
 * @param modifier The modifier applied to the band container.
 * @param content The row of child picker columns to overlay.
 */
@Composable
internal fun PickerSelectionBand(
    indicator: PickerSelectionIndicator,
    visibleItemsCount: Int,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        contents = listOf(
            content,
            { SelectionBandLines(indicator = indicator, enabled = enabled) }
        )
    ) { measurables, constraints ->
        val contentMeasurables = measurables[0]
        val bandMeasurables = measurables[1]

        val contentPlaceables = contentMeasurables.map { it.measure(constraints) }
        val width = contentPlaceables.maxOfOrNull { it.width } ?: 0
        val height = contentPlaceables.maxOfOrNull { it.height } ?: 0
        val itemHeight = if (visibleItemsCount > 0) height / visibleItemsCount else height

        val bandPlaceables = bandMeasurables.map {
            it.measure(Constraints.fixed(width = width, height = itemHeight))
        }

        layout(width, height) {
            val bandY = (height - itemHeight) / 2
            contentPlaceables.forEach { it.placeRelative(0, 0) }
            // Draw the band above child picker backgrounds so custom selected backgrounds do not
            // hide it. The lines sit at the band edges, matching Picker's own divider overlay.
            bandPlaceables.forEach { it.placeRelative(0, bandY) }
        }
    }
}

/**
 * Renders the top and bottom selection band lines inside the band-height box provided by
 * [PickerSelectionBand].
 */
@Composable
private fun SelectionBandLines(
    indicator: PickerSelectionIndicator,
    enabled: Boolean
) {
    if (!indicator.isVisible) return

    val color = if (enabled) {
        indicator.color
    } else {
        indicator.color.copy(alpha = indicator.color.alpha * DISABLED_BAND_ALPHA)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = indicator.horizontalInset)
    ) {
        val lineModifier = Modifier
            .fillMaxWidth()
            .height(indicator.thickness)
            .background(color = color, shape = indicator.shape)
        Box(modifier = Modifier.then(lineModifier).align(Alignment.TopCenter))
        Box(modifier = Modifier.then(lineModifier).align(Alignment.BottomCenter))
    }
}
