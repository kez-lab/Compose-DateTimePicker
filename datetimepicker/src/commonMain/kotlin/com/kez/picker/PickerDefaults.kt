package com.kez.picker

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kez.picker.util.TimePeriod

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
     * Creates a [PickerStyle] that groups picker visual and layout styling.
     *
     * @param visibleItemsCount The number of items visible at once. Must be a positive odd number.
     * @param colors The colors used by the picker.
     * @param textStyles The text styles used by the picker.
     * @param selectedItemBackgroundShape The shape of the selected item background.
     * @param itemPadding The padding around each item.
     * @param fadingEdgeGradient The gradient used for fading edges.
     * @param horizontalAlignment The horizontal alignment of items.
     * @param verticalAlignment The vertical alignment of item content.
     * @param dividerThickness The thickness of the selection dividers.
     * @param dividerShape The shape of the selection dividers.
     * @param isDividerVisible Whether selection dividers are visible.
     * @return A [PickerStyle] instance with the specified styling.
     */
    @Composable
    fun style(
        visibleItemsCount: Int = VisibleItemsCount,
        colors: PickerColors = PickerDefaults.colors(),
        textStyles: PickerTextStyles = PickerDefaults.textStyles(),
        selectedItemBackgroundShape: Shape = SelectedItemBackgroundShape,
        itemPadding: PaddingValues = ItemPadding,
        fadingEdgeGradient: Brush = fadingEdgeGradient(),
        horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
        verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
        dividerThickness: Dp = DividerThickness,
        dividerShape: Shape = DividerShape,
        isDividerVisible: Boolean = true
    ): PickerStyle = PickerStyle(
        visibleItemsCount = visibleItemsCount,
        colors = colors,
        textStyles = textStyles,
        selectedItemBackgroundShape = selectedItemBackgroundShape,
        itemPadding = itemPadding,
        fadingEdgeGradient = fadingEdgeGradient,
        horizontalAlignment = horizontalAlignment,
        verticalAlignment = verticalAlignment,
        dividerThickness = dividerThickness,
        dividerShape = dividerShape,
        isDividerVisible = isDividerVisible
    )

    /**
     * Creates accessibility configuration for one picker column.
     *
     * @param pickerLabel Optional label used as the accessibility prefix for the picker column.
     * @param itemContentDescription Accessibility description for each item value.
     * @param previousItemActionLabel Accessibility action label for selecting the previous item. Pass null or blank to omit the action.
     * @param nextItemActionLabel Accessibility action label for selecting the next item. Pass null or blank to omit the action.
     * @return A [PickerAccessibility] instance with the specified semantics behavior.
     */
    fun <T : Any> accessibility(
        pickerLabel: String? = null,
        itemContentDescription: (T) -> String = { it.toString() },
        previousItemActionLabel: String? = PreviousItemActionLabel,
        nextItemActionLabel: String? = NextItemActionLabel
    ): PickerAccessibility<T> = PickerAccessibility(
        pickerLabel = pickerLabel,
        itemContentDescription = itemContentDescription,
        previousItemActionLabel = previousItemActionLabel,
        nextItemActionLabel = nextItemActionLabel
    )

    /**
     * Creates accessibility configuration for a time picker.
     *
     * Shared previous/next action labels are applied to all child picker columns. Use
     * [TimePickerAccessibility.copy] with [accessibility] when one column needs a custom label or formatter.
     *
     * @param hourPickerLabel Accessibility label for the hour picker. Pass null to omit the picker label prefix.
     * @param minutePickerLabel Accessibility label for the minute picker. Pass null to omit the picker label prefix.
     * @param periodPickerLabel Accessibility label for the AM/PM picker in 12-hour time. Pass null to omit the picker label prefix.
     * @param hourItemContentDescription Accessibility description for each hour value.
     * @param minuteItemContentDescription Accessibility description for each minute value.
     * @param periodItemContentDescription Accessibility description for each AM/PM value.
     * @param previousItemActionLabel Accessibility action label used by child pickers to select the previous item.
     * @param nextItemActionLabel Accessibility action label used by child pickers to select the next item.
     * @return A [TimePickerAccessibility] instance with the specified semantics behavior.
     */
    fun timePickerAccessibility(
        hourPickerLabel: String? = "Hour",
        minutePickerLabel: String? = "Minute",
        periodPickerLabel: String? = "AM/PM",
        hourItemContentDescription: (Int) -> String = { it.toString() },
        minuteItemContentDescription: (Int) -> String = { it.toString() },
        periodItemContentDescription: (TimePeriod) -> String = { it.name },
        previousItemActionLabel: String? = PreviousItemActionLabel,
        nextItemActionLabel: String? = NextItemActionLabel
    ): TimePickerAccessibility = TimePickerAccessibility(
        hour = accessibility(
            pickerLabel = hourPickerLabel,
            itemContentDescription = hourItemContentDescription,
            previousItemActionLabel = previousItemActionLabel,
            nextItemActionLabel = nextItemActionLabel
        ),
        minute = accessibility(
            pickerLabel = minutePickerLabel,
            itemContentDescription = minuteItemContentDescription,
            previousItemActionLabel = previousItemActionLabel,
            nextItemActionLabel = nextItemActionLabel
        ),
        period = accessibility(
            pickerLabel = periodPickerLabel,
            itemContentDescription = periodItemContentDescription,
            previousItemActionLabel = previousItemActionLabel,
            nextItemActionLabel = nextItemActionLabel
        )
    )

    /**
     * Creates accessibility configuration for a date picker.
     *
     * @param yearPickerLabel Accessibility label for the year picker. Pass null to omit the picker label prefix.
     * @param monthPickerLabel Accessibility label for the month picker. Pass null to omit the picker label prefix.
     * @param dayPickerLabel Accessibility label for the day picker. Pass null to omit the picker label prefix.
     * @param yearItemContentDescription Accessibility description for each year value.
     * @param monthItemContentDescription Accessibility description for each month value.
     * @param dayItemContentDescription Accessibility description for each day value.
     * @param previousItemActionLabel Accessibility action label used by child pickers to select the previous item.
     * @param nextItemActionLabel Accessibility action label used by child pickers to select the next item.
     * @return A [DatePickerAccessibility] instance with the specified semantics behavior.
     */
    fun datePickerAccessibility(
        yearPickerLabel: String? = "Year",
        monthPickerLabel: String? = "Month",
        dayPickerLabel: String? = "Day",
        yearItemContentDescription: (Int) -> String = { it.toString() },
        monthItemContentDescription: (Int) -> String = { it.toString() },
        dayItemContentDescription: (Int) -> String = { it.toString() },
        previousItemActionLabel: String? = PreviousItemActionLabel,
        nextItemActionLabel: String? = NextItemActionLabel
    ): DatePickerAccessibility = DatePickerAccessibility(
        year = accessibility(
            pickerLabel = yearPickerLabel,
            itemContentDescription = yearItemContentDescription,
            previousItemActionLabel = previousItemActionLabel,
            nextItemActionLabel = nextItemActionLabel
        ),
        month = accessibility(
            pickerLabel = monthPickerLabel,
            itemContentDescription = monthItemContentDescription,
            previousItemActionLabel = previousItemActionLabel,
            nextItemActionLabel = nextItemActionLabel
        ),
        day = accessibility(
            pickerLabel = dayPickerLabel,
            itemContentDescription = dayItemContentDescription,
            previousItemActionLabel = previousItemActionLabel,
            nextItemActionLabel = nextItemActionLabel
        )
    )

    /**
     * Creates accessibility configuration for a year-month picker.
     *
     * @param yearPickerLabel Accessibility label for the year picker. Pass null to omit the picker label prefix.
     * @param monthPickerLabel Accessibility label for the month picker. Pass null to omit the picker label prefix.
     * @param yearItemContentDescription Accessibility description for each year value.
     * @param monthItemContentDescription Accessibility description for each month value.
     * @param previousItemActionLabel Accessibility action label used by child pickers to select the previous item.
     * @param nextItemActionLabel Accessibility action label used by child pickers to select the next item.
     * @return A [YearMonthPickerAccessibility] instance with the specified semantics behavior.
     */
    fun yearMonthPickerAccessibility(
        yearPickerLabel: String? = "Year",
        monthPickerLabel: String? = "Month",
        yearItemContentDescription: (Int) -> String = { it.toString() },
        monthItemContentDescription: (Int) -> String = { it.toString() },
        previousItemActionLabel: String? = PreviousItemActionLabel,
        nextItemActionLabel: String? = NextItemActionLabel
    ): YearMonthPickerAccessibility = YearMonthPickerAccessibility(
        year = accessibility(
            pickerLabel = yearPickerLabel,
            itemContentDescription = yearItemContentDescription,
            previousItemActionLabel = previousItemActionLabel,
            nextItemActionLabel = nextItemActionLabel
        ),
        month = accessibility(
            pickerLabel = monthPickerLabel,
            itemContentDescription = monthItemContentDescription,
            previousItemActionLabel = previousItemActionLabel,
            nextItemActionLabel = nextItemActionLabel
        )
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

/**
 * Represents visual and layout styling shared by [Picker] and related components.
 *
 * @param visibleItemsCount The number of items visible at once.
 * @param colors The colors used by the picker.
 * @param textStyles The text styles used by the picker.
 * @param selectedItemBackgroundShape The shape of the selected item background.
 * @param itemPadding The padding around each item.
 * @param fadingEdgeGradient The gradient used for fading edges.
 * @param horizontalAlignment The horizontal alignment of items.
 * @param verticalAlignment The vertical alignment of item content.
 * @param dividerThickness The thickness of the selection dividers.
 * @param dividerShape The shape of the selection dividers.
 * @param isDividerVisible Whether selection dividers are visible.
 * @see PickerDefaults.style
 */
@Immutable
data class PickerStyle(
    val visibleItemsCount: Int,
    val colors: PickerColors,
    val textStyles: PickerTextStyles,
    val selectedItemBackgroundShape: Shape,
    val itemPadding: PaddingValues,
    val fadingEdgeGradient: Brush,
    val horizontalAlignment: Alignment.Horizontal,
    val verticalAlignment: Alignment.Vertical,
    val dividerThickness: Dp,
    val dividerShape: Shape,
    val isDividerVisible: Boolean
)
