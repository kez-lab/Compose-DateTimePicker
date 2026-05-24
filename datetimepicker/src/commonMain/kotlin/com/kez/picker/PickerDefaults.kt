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
import com.kez.picker.util.DAY_RANGE
import com.kez.picker.util.HOUR12_RANGE
import com.kez.picker.util.HOUR24_RANGE
import com.kez.picker.util.MINUTE_RANGE
import com.kez.picker.util.MONTH_RANGE
import com.kez.picker.util.TimePeriod
import com.kez.picker.util.YEAR_RANGE
import com.kez.picker.date.YearMonth
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

private const val DISABLED_CONTENT_ALPHA: Float = 0.38f
private const val DISABLED_CONTAINER_ALPHA: Float = 0.12f

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
     * @param disabledDividerColor The divider color used when the picker is disabled.
     * @param disabledSelectedItemBackgroundColor The selected item background color used when the picker is disabled.
     * @param disabledTextColor The unselected item text color used when the picker is disabled.
     * @param disabledSelectedTextColor The selected item text color used when the picker is disabled.
     * @return A [PickerColors] instance with the specified colors.
     */
    @Composable
    fun colors(
        dividerColor: Color = LocalContentColor.current,
        selectedItemBackgroundColor: Color = Color.Transparent,
        textColor: Color = LocalContentColor.current.copy(alpha = 0.7f),
        selectedTextColor: Color = LocalContentColor.current,
        disabledDividerColor: Color = dividerColor.copy(alpha = dividerColor.alpha * DISABLED_CONTAINER_ALPHA),
        disabledSelectedItemBackgroundColor: Color = selectedItemBackgroundColor.copy(
            alpha = selectedItemBackgroundColor.alpha * DISABLED_CONTAINER_ALPHA
        ),
        disabledTextColor: Color = textColor.copy(alpha = textColor.alpha * DISABLED_CONTENT_ALPHA),
        disabledSelectedTextColor: Color = selectedTextColor.copy(
            alpha = selectedTextColor.alpha * DISABLED_CONTENT_ALPHA
        )
    ): PickerColors = PickerColors(
        dividerColor = dividerColor,
        selectedItemBackgroundColor = selectedItemBackgroundColor,
        textColor = textColor,
        selectedTextColor = selectedTextColor,
        disabledDividerColor = disabledDividerColor,
        disabledSelectedItemBackgroundColor = disabledSelectedItemBackgroundColor,
        disabledTextColor = disabledTextColor,
        disabledSelectedTextColor = disabledSelectedTextColor
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
     * Creates layout options for [com.kez.picker.time.TimePicker] columns.
     *
     * Pass `null` for a column weight when [com.kez.picker.time.TimePicker] should use
     * `pickerModifier` to define that column width instead of filling weighted row space.
     *
     * @param periodWeight The AM/PM column weight in 12-hour mode.
     * @param hourWeight The hour column weight.
     * @param minuteWeight The minute column weight.
     * @param columnOrder The visual column order. [TimePickerColumn.PERIOD] is ignored in 24-hour mode.
     * @return A [TimePickerLayout] instance with the specified column weights.
     */
    fun timePickerLayout(
        periodWeight: Float? = 1f,
        hourWeight: Float? = 1f,
        minuteWeight: Float? = 1f,
        columnOrder: List<TimePickerColumn> = listOf(
            TimePickerColumn.PERIOD,
            TimePickerColumn.HOUR,
            TimePickerColumn.MINUTE
        )
    ): TimePickerLayout = TimePickerLayout(
        periodWeight = periodWeight,
        hourWeight = hourWeight,
        minuteWeight = minuteWeight,
        columnOrder = columnOrder.toList()
    )

    /**
     * Creates layout options for [com.kez.picker.date.DatePicker] columns.
     *
     * Pass `null` for a column weight when [com.kez.picker.date.DatePicker] should use
     * `pickerModifier` to define that column width instead of filling weighted row space.
     *
     * @param yearWeight The year column weight.
     * @param monthWeight The month column weight.
     * @param dayWeight The day column weight.
     * @param columnOrder The visual column order.
     * @return A [DatePickerLayout] instance with the specified column weights.
     */
    fun datePickerLayout(
        yearWeight: Float? = 1.2f,
        monthWeight: Float? = 0.8f,
        dayWeight: Float? = 0.8f,
        columnOrder: List<DatePickerColumn> = listOf(
            DatePickerColumn.YEAR,
            DatePickerColumn.MONTH,
            DatePickerColumn.DAY
        )
    ): DatePickerLayout = DatePickerLayout(
        yearWeight = yearWeight,
        monthWeight = monthWeight,
        dayWeight = dayWeight,
        columnOrder = columnOrder.toList()
    )

    /**
     * Creates layout options for [com.kez.picker.date.YearMonthPicker] columns.
     *
     * Pass `null` for a column weight when [com.kez.picker.date.YearMonthPicker] should use
     * `pickerModifier` to define that column width instead of filling weighted row space.
     *
     * @param yearWeight The year column weight.
     * @param monthWeight The month column weight.
     * @param columnOrder The visual column order.
     * @return A [YearMonthPickerLayout] instance with the specified column weights.
     */
    fun yearMonthPickerLayout(
        yearWeight: Float? = 1f,
        monthWeight: Float? = 1f,
        columnOrder: List<YearMonthPickerColumn> = listOf(
            YearMonthPickerColumn.YEAR,
            YearMonthPickerColumn.MONTH
        )
    ): YearMonthPickerLayout = YearMonthPickerLayout(
        yearWeight = yearWeight,
        monthWeight = monthWeight,
        columnOrder = columnOrder.toList()
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
     * Creates visible item text configuration for one picker column.
     *
     * @param itemText Text displayed for each item value.
     * @return A [PickerItemText] instance with the specified visible text formatter.
     */
    fun <T : Any> itemText(
        itemText: (T) -> String = { it.toString() }
    ): PickerItemText<T> = PickerItemText(itemText = itemText)

    /**
     * Creates visible item text configuration for a time picker.
     *
     * @param hourItemText Text displayed for each hour value.
     * @param minuteItemText Text displayed for each minute value.
     * @param periodItemText Text displayed for each AM/PM value.
     * @return A [TimePickerDisplay] instance with the specified visible text formatters.
     */
    fun timePickerDisplay(
        hourItemText: (Int) -> String = { it.toString() },
        minuteItemText: (Int) -> String = { it.toString() },
        periodItemText: (TimePeriod) -> String = { it.name }
    ): TimePickerDisplay = TimePickerDisplay(
        hour = itemText(itemText = hourItemText),
        minute = itemText(itemText = minuteItemText),
        period = itemText(itemText = periodItemText)
    )

    /**
     * Creates visible item text configuration for a date picker.
     *
     * @param yearItemText Text displayed for each year value.
     * @param monthItemText Text displayed for each month value.
     * @param dayItemText Text displayed for each day value.
     * @return A [DatePickerDisplay] instance with the specified visible text formatters.
     */
    fun datePickerDisplay(
        yearItemText: (Int) -> String = { it.toString() },
        monthItemText: (Int) -> String = { it.toString() },
        dayItemText: (Int) -> String = { it.toString() }
    ): DatePickerDisplay = DatePickerDisplay(
        year = itemText(itemText = yearItemText),
        month = itemText(itemText = monthItemText),
        day = itemText(itemText = dayItemText)
    )

    /**
     * Creates visible item text configuration for a year-month picker.
     *
     * @param yearItemText Text displayed for each year value.
     * @param monthItemText Text displayed for each month value.
     * @return A [YearMonthPickerDisplay] instance with the specified visible text formatters.
     */
    fun yearMonthPickerDisplay(
        yearItemText: (Int) -> String = { it.toString() },
        monthItemText: (Int) -> String = { it.toString() }
    ): YearMonthPickerDisplay = YearMonthPickerDisplay(
        year = itemText(itemText = yearItemText),
        month = itemText(itemText = monthItemText)
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
     * Creates accessibility configuration for a date range picker.
     *
     * @param start Accessibility configuration for the start date picker.
     * @param end Accessibility configuration for the end date picker.
     * @return A [DateRangePickerAccessibility] instance with the specified semantics behavior.
     */
    fun dateRangePickerAccessibility(
        start: DatePickerAccessibility = datePickerAccessibility(
            yearPickerLabel = "Start year",
            monthPickerLabel = "Start month",
            dayPickerLabel = "Start day"
        ),
        end: DatePickerAccessibility = datePickerAccessibility(
            yearPickerLabel = "End year",
            monthPickerLabel = "End month",
            dayPickerLabel = "End day"
        )
    ): DateRangePickerAccessibility = DateRangePickerAccessibility(
        start = start,
        end = end
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
     * Creates inclusive time constraints for a time picker.
     *
     * @param minTime The earliest selectable time, inclusive. Pass null to omit the lower bound.
     * @param maxTime The latest selectable time, inclusive. Pass null to omit the upper bound.
     * @return A [TimePickerConstraints] instance with the specified bounds.
     */
    fun timePickerConstraints(
        minTime: LocalTime? = null,
        maxTime: LocalTime? = null
    ): TimePickerConstraints = TimePickerConstraints(
        minTime = minTime,
        maxTime = maxTime
    )

    /**
     * Creates selectable item lists for a time picker.
     *
     * @param minuteItems Minute values available for selection. Values must be in 0..59.
     * @param hour24Items Hour values available when using 24-hour time. Values must be in 0..23.
     * @param hour12Items Display-hour values available when using 12-hour time. Values must be in 1..12.
     * @param periodItems AM/PM values available when using 12-hour time.
     * @param minTime The earliest selectable time, inclusive. Pass null to omit the lower bound.
     * @param maxTime The latest selectable time, inclusive. Pass null to omit the upper bound.
     * @return A [TimePickerItems] instance with the specified item lists.
     */
    fun timePickerItems(
        minuteItems: List<Int> = MINUTE_RANGE,
        hour24Items: List<Int> = HOUR24_RANGE,
        hour12Items: List<Int> = HOUR12_RANGE,
        periodItems: List<TimePeriod> = TimePeriod.entries,
        minTime: LocalTime? = null,
        maxTime: LocalTime? = null
    ): TimePickerItems = TimePickerItems(
        minuteItems = minuteItems,
        hour24Items = hour24Items,
        hour12Items = hour12Items,
        periodItems = periodItems,
        constraints = timePickerConstraints(
            minTime = minTime,
            maxTime = maxTime
        )
    )

    /**
     * Creates inclusive date constraints for a date picker.
     *
     * @param minDate The earliest selectable date, inclusive. Pass null to omit the lower bound.
     * @param maxDate The latest selectable date, inclusive. Pass null to omit the upper bound.
     * @return A [DatePickerConstraints] instance with the specified bounds.
     */
    fun datePickerConstraints(
        minDate: LocalDate? = null,
        maxDate: LocalDate? = null
    ): DatePickerConstraints = DatePickerConstraints(
        minDate = minDate,
        maxDate = maxDate
    )

    /**
     * Creates selectable item lists for a date picker.
     *
     * @param yearItems Year values available for selection. Values must be in 1000..9999.
     * @param monthItems Month values available for selection. Values must be in 1..12.
     * @param dayItems Day values available for selection. Values must be in 1..31.
     * @param minDate The earliest selectable date, inclusive. Pass null to omit the lower bound.
     * @param maxDate The latest selectable date, inclusive. Pass null to omit the upper bound.
     * @return A [DatePickerItems] instance with the specified item lists.
     */
    fun datePickerItems(
        yearItems: List<Int> = YEAR_RANGE,
        monthItems: List<Int> = MONTH_RANGE,
        dayItems: List<Int> = DAY_RANGE,
        minDate: LocalDate? = null,
        maxDate: LocalDate? = null
    ): DatePickerItems = DatePickerItems(
        yearItems = yearItems,
        monthItems = monthItems,
        dayItems = dayItems,
        constraints = datePickerConstraints(
            minDate = minDate,
            maxDate = maxDate
        )
    )

    /**
     * Creates inclusive year/month constraints for a year-month picker.
     *
     * @param minYearMonth The earliest selectable year/month, inclusive. Pass null to omit the lower bound.
     * @param maxYearMonth The latest selectable year/month, inclusive. Pass null to omit the upper bound.
     * @return A [YearMonthPickerConstraints] instance with the specified bounds.
     */
    fun yearMonthPickerConstraints(
        minYearMonth: YearMonth? = null,
        maxYearMonth: YearMonth? = null
    ): YearMonthPickerConstraints = YearMonthPickerConstraints(
        minYearMonth = minYearMonth,
        maxYearMonth = maxYearMonth
    )

    /**
     * Creates selectable item lists for a year-month picker.
     *
     * @param yearItems Year values available for selection. Values must be in 1000..9999.
     * @param monthItems Month values available for selection. Values must be in 1..12.
     * @param minYearMonth The earliest selectable year/month, inclusive. Pass null to omit the lower bound.
     * @param maxYearMonth The latest selectable year/month, inclusive. Pass null to omit the upper bound.
     * @return A [YearMonthPickerItems] instance with the specified item lists.
     */
    fun yearMonthPickerItems(
        yearItems: List<Int> = YEAR_RANGE,
        monthItems: List<Int> = MONTH_RANGE,
        minYearMonth: YearMonth? = null,
        maxYearMonth: YearMonth? = null
    ): YearMonthPickerItems = YearMonthPickerItems(
        yearItems = yearItems,
        monthItems = monthItems,
        constraints = yearMonthPickerConstraints(
            minYearMonth = minYearMonth,
            maxYearMonth = maxYearMonth
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
 * @param disabledDividerColor The divider color used when the picker is disabled.
 * @param disabledSelectedItemBackgroundColor The selected item background color used when the picker is disabled.
 * @param disabledTextColor The unselected item text color used when the picker is disabled.
 * @param disabledSelectedTextColor The selected item text color used when the picker is disabled.
 * @see PickerDefaults.colors
 */
@Immutable
data class PickerColors(
    val dividerColor: Color,
    val selectedItemBackgroundColor: Color,
    val textColor: Color,
    val selectedTextColor: Color,
    val disabledDividerColor: Color = dividerColor.copy(alpha = dividerColor.alpha * DISABLED_CONTAINER_ALPHA),
    val disabledSelectedItemBackgroundColor: Color = selectedItemBackgroundColor.copy(
        alpha = selectedItemBackgroundColor.alpha * DISABLED_CONTAINER_ALPHA
    ),
    val disabledTextColor: Color = textColor.copy(alpha = textColor.alpha * DISABLED_CONTENT_ALPHA),
    val disabledSelectedTextColor: Color = selectedTextColor.copy(
        alpha = selectedTextColor.alpha * DISABLED_CONTENT_ALPHA
    )
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

/**
 * Identifies a [com.kez.picker.time.TimePicker] column for layout ordering.
 */
enum class TimePickerColumn {
    /**
     * AM/PM period column. This column is rendered only in 12-hour mode.
     */
    PERIOD,

    /**
     * Hour column.
     */
    HOUR,

    /**
     * Minute column.
     */
    MINUTE
}

/**
 * Identifies a [com.kez.picker.date.DatePicker] column for layout ordering.
 */
enum class DatePickerColumn {
    /**
     * Year column.
     */
    YEAR,

    /**
     * Month column.
     */
    MONTH,

    /**
     * Day column.
     */
    DAY
}

/**
 * Identifies a [com.kez.picker.date.YearMonthPicker] column for layout ordering.
 */
enum class YearMonthPickerColumn {
    /**
     * Year column.
     */
    YEAR,

    /**
     * Month column.
     */
    MONTH
}

/**
 * Represents layout options used by [com.kez.picker.time.TimePicker] columns.
 *
 * Set a column weight to `null` to leave that column unweighted so `pickerModifier` can provide an
 * explicit width.
 *
 * @param periodWeight The AM/PM column weight in 12-hour mode.
 * @param hourWeight The hour column weight.
 * @param minuteWeight The minute column weight.
 * @param columnOrder The visual column order. [TimePickerColumn.PERIOD] is ignored in 24-hour mode.
 * @see PickerDefaults.timePickerLayout
 */
@Immutable
data class TimePickerLayout(
    val periodWeight: Float?,
    val hourWeight: Float?,
    val minuteWeight: Float?,
    val columnOrder: List<TimePickerColumn> = listOf(
        TimePickerColumn.PERIOD,
        TimePickerColumn.HOUR,
        TimePickerColumn.MINUTE
    )
) {
    init {
        requirePickerWeight("periodWeight", periodWeight)
        requirePickerWeight("hourWeight", hourWeight)
        requirePickerWeight("minuteWeight", minuteWeight)
        requireColumnOrder(
            name = "TimePickerLayout.columnOrder",
            columnOrder = columnOrder,
            expectedColumns = TimePickerColumn.entries
        )
    }
}

/**
 * Represents layout options used by [com.kez.picker.date.DatePicker] columns.
 *
 * Set a column weight to `null` to leave that column unweighted so `pickerModifier` can provide an
 * explicit width.
 *
 * @param yearWeight The year column weight.
 * @param monthWeight The month column weight.
 * @param dayWeight The day column weight.
 * @param columnOrder The visual column order.
 * @see PickerDefaults.datePickerLayout
 */
@Immutable
data class DatePickerLayout(
    val yearWeight: Float?,
    val monthWeight: Float?,
    val dayWeight: Float?,
    val columnOrder: List<DatePickerColumn> = listOf(
        DatePickerColumn.YEAR,
        DatePickerColumn.MONTH,
        DatePickerColumn.DAY
    )
) {
    init {
        requirePickerWeight("yearWeight", yearWeight)
        requirePickerWeight("monthWeight", monthWeight)
        requirePickerWeight("dayWeight", dayWeight)
        requireColumnOrder(
            name = "DatePickerLayout.columnOrder",
            columnOrder = columnOrder,
            expectedColumns = DatePickerColumn.entries
        )
    }
}

/**
 * Represents layout options used by [com.kez.picker.date.YearMonthPicker] columns.
 *
 * Set a column weight to `null` to leave that column unweighted so `pickerModifier` can provide an
 * explicit width.
 *
 * @param yearWeight The year column weight.
 * @param monthWeight The month column weight.
 * @param columnOrder The visual column order.
 * @see PickerDefaults.yearMonthPickerLayout
 */
@Immutable
data class YearMonthPickerLayout(
    val yearWeight: Float?,
    val monthWeight: Float?,
    val columnOrder: List<YearMonthPickerColumn> = listOf(
        YearMonthPickerColumn.YEAR,
        YearMonthPickerColumn.MONTH
    )
) {
    init {
        requirePickerWeight("yearWeight", yearWeight)
        requirePickerWeight("monthWeight", monthWeight)
        requireColumnOrder(
            name = "YearMonthPickerLayout.columnOrder",
            columnOrder = columnOrder,
            expectedColumns = YearMonthPickerColumn.entries
        )
    }
}

private fun requirePickerWeight(name: String, weight: Float?) {
    require(weight == null || weight > 0f) {
        "$name must be positive when provided, but was $weight."
    }
}

private fun <T> requireColumnOrder(
    name: String,
    columnOrder: List<T>,
    expectedColumns: List<T>
) {
    require(columnOrder.size == expectedColumns.size && columnOrder.toSet() == expectedColumns.toSet()) {
        "$name must contain each of $expectedColumns exactly once, but was $columnOrder."
    }
}
