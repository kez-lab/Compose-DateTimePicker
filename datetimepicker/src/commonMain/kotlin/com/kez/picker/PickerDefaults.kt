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
 * Follows Material3 component design patterns.
 */
object PickerDefaults {

    /**
     * Default number of visible items in the picker.
     * Must be an odd number for proper center alignment.
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
     * Default spacing between pickers in composite components (e.g., TimePicker, DatePicker).
     */
    val SpacingBetweenPickers: Dp = 0.dp

    /**
     * Default shape for the selected item background.
     */
    val SelectedItemBackgroundShape: Shape = RoundedCornerShape(12.dp)

    /**
     * Default accessibility action label for selecting the previous picker item.
     */
    const val PreviousItemActionLabel: String = "Select previous item"

    /**
     * Default accessibility action label for selecting the next picker item.
     */
    const val NextItemActionLabel: String = "Select next item"

    /**
     * Default shape for the dividers.
     */
    val DividerShape: Shape = RoundedCornerShape(10.dp)

    /**
     * Creates a [PickerColors] with the provided colors.
     *
     * @param dividerColor The color of the dividers.
     * @param selectedItemBackgroundColor The background color of the selected item area.
     * @param textColor The color of unselected item text.
     * @param selectedTextColor The color of the selected item text.
     * @return A [PickerColors] instance with the specified colors.
     */
    @Composable
    fun colors(
        dividerColor: Color = LocalContentColor.current,
        selectedItemBackgroundColor: Color = Color.Transparent,
        textColor: Color = LocalContentColor.current.copy(alpha = 0.7f),
        selectedTextColor: Color = LocalContentColor.current
    ): PickerColors = PickerColors(
        dividerColor = dividerColor,
        selectedItemBackgroundColor = selectedItemBackgroundColor,
        textColor = textColor,
        selectedTextColor = selectedTextColor
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
 * @param textColor The color of unselected item text.
 * @param selectedTextColor The color of the selected item text.
 * @see PickerDefaults.colors
 */
@Immutable
data class PickerColors(
    val dividerColor: Color,
    val selectedItemBackgroundColor: Color,
    val textColor: Color,
    val selectedTextColor: Color
)

/**
 * Represents the text styles used by [Picker] and related components.
 *
 * @param textStyle The style of the text for unselected items.
 * @param selectedTextStyle The style of the text for the selected item.
 * @see PickerDefaults.textStyles
 */
@Immutable
data class PickerTextStyles(
    val textStyle: TextStyle,
    val selectedTextStyle: TextStyle
)
