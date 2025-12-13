package com.kez.picker

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Contains default values and factory methods for creating Picker styles.
 */
object PickerDefaults {

    /**
     * Default number of visible items in the picker.
     */
    const val VisibleItemsCount: Int = 3

    /**
     * Default padding around each item.
     */
    val ItemPadding: PaddingValues = PaddingValues(8.dp)

    /**
     * Default thickness of the dividers.
     */
    val DividerThickness: Dp = 1.dp

    /**
     * Default spacing between pickers in composite components (e.g., TimePicker).
     */
    val SpacingBetweenPickers: Dp = 20.dp

    /**
     * Default shape for the selected item background.
     */
    val SelectedItemBackgroundShape: Shape = RoundedCornerShape(12.dp)

    /**
     * Default shape for the dividers.
     */
    val DividerShape: Shape = RoundedCornerShape(10.dp)

    /**
     * Creates a [PickerColors] with the provided colors.
     *
     * @param dividerColor The color of the dividers.
     * @param selectedItemBackgroundColor The background color of the selected item area.
     * @return A [PickerColors] instance with the specified colors.
     */
    @Composable
    fun colors(
        dividerColor: Color = LocalContentColor.current,
        selectedItemBackgroundColor: Color = Color.Transparent
    ): PickerColors = PickerColors(
        dividerColor = dividerColor,
        selectedItemBackgroundColor = selectedItemBackgroundColor
    )

    /**
     * Creates a [PickerTextStyles] with the provided text styles.
     *
     * @param textStyle The style of the text for unselected items.
     * @param selectedTextStyle The style of the text for the selected item.
     * @return A [PickerTextStyles] instance with the specified styles.
     */
    @Composable
    fun textStyles(
        textStyle: TextStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
        selectedTextStyle: TextStyle = LocalTextStyle.current.copy(fontSize = 22.sp)
    ): PickerTextStyles = PickerTextStyles(
        textStyle = textStyle,
        selectedTextStyle = selectedTextStyle
    )

    /**
     * Creates the default fading edge gradient.
     *
     * @return A vertical [Brush] with transparent edges and opaque center.
     */
    fun fadingEdgeGradient(): Brush = Brush.verticalGradient(
        0f to Color.Transparent,
        0.5f to Color.Black,
        1f to Color.Transparent
    )
}

/**
 * Represents the colors used by [Picker] and related components.
 *
 * @param dividerColor The color of the dividers.
 * @param selectedItemBackgroundColor The background color of the selected item area.
 */
@Immutable
data class PickerColors(
    val dividerColor: Color,
    val selectedItemBackgroundColor: Color
)

/**
 * Represents the text styles used by [Picker] and related components.
 *
 * @param textStyle The style of the text for unselected items.
 * @param selectedTextStyle The style of the text for the selected item.
 */
@Immutable
data class PickerTextStyles(
    val textStyle: TextStyle,
    val selectedTextStyle: TextStyle
)
