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
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
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
    val ItemPadding: PaddingValues = PaddingValues(horizontal = 8.dp, vertical = 14.dp)

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
     * Default semantics action label for selecting the previous picker item.
     */
    const val PreviousItemActionLabel: String = "Select previous item"

    /**
     * Default semantics action label for selecting the next picker item.
     */
    const val NextItemActionLabel: String = "Select next item"

    /**
     * Default shape for the dividers.
     */
    val DividerShape: Shape = RoundedCornerShape(10.dp)

    /**
     * Default width of the selection dividers. Defaults to filling the picker column width.
     */
    val DividerWidth: PickerDividerWidth = PickerDividerWidth.Fill

    /**
     * Default horizontal inset applied to each side of a composite picker's selection indicator band.
     */
    val SelectionIndicatorHorizontalInset: Dp = 0.dp

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
        dividerColor: Color = LocalContentColor.current.copy(alpha = 0.2f),
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
     * @param dividerThickness The thickness of the selection dividers.
     * @param dividerShape The shape of the selection dividers.
     * @param dividerWidth The width of the selection dividers. Use [PickerDividerWidth.Fill] for the
     * full column width, [PickerDividerWidth.Fraction] for a fraction of it, or
     * [PickerDividerWidth.Fixed] for an absolute width.
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
        dividerThickness: Dp = DividerThickness,
        dividerShape: Shape = DividerShape,
        dividerWidth: PickerDividerWidth = DividerWidth,
        isDividerVisible: Boolean = true
    ): PickerStyle = PickerStyle(
        visibleItemsCount = visibleItemsCount,
        colors = colors,
        textStyles = textStyles,
        selectedItemBackgroundShape = selectedItemBackgroundShape,
        itemPadding = itemPadding,
        fadingEdgeGradient = fadingEdgeGradient,
        horizontalAlignment = horizontalAlignment,
        dividerThickness = dividerThickness,
        dividerShape = dividerShape,
        dividerWidth = dividerWidth,
        isDividerVisible = isDividerVisible
    )

    /**
     * Creates a [PickerSelectionIndicator] for composite pickers (e.g.
     * [com.kez.picker.time.TimePicker], [com.kez.picker.date.DatePicker]).
     *
     * Composite pickers render a single selection band spanning the whole picker instead of one
     * divider per column, so per-column [PickerStyle] divider settings do not apply to them. The
     * defaults derive from [style] so existing divider customizations carry over.
     *
     * @param style The picker style used to derive default indicator values.
     * @param color The color of the selection band lines.
     * @param thickness The thickness of each selection band line.
     * @param shape The shape of each selection band line.
     * @param horizontalInset The inset applied to each side of the band so it can be narrower than
     * the full picker width while staying centered.
     * @param isVisible Whether the selection band is drawn.
     * @return A [PickerSelectionIndicator] instance with the specified values.
     */
    fun selectionIndicator(
        style: PickerStyle,
        color: Color = style.colors.dividerColor,
        thickness: Dp = style.dividerThickness,
        shape: Shape = style.dividerShape,
        horizontalInset: Dp = SelectionIndicatorHorizontalInset,
        isVisible: Boolean = style.isDividerVisible
    ): PickerSelectionIndicator = PickerSelectionIndicator(
        color = color,
        thickness = thickness,
        shape = shape,
        horizontalInset = horizontalInset,
        isVisible = isVisible
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
     * @param columnOrder The visual column order. Must contain every [TimePickerColumn] exactly
     * once. [TimePickerColumn.PERIOD] is still required in 24-hour mode, but its position is ignored.
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
        columnOrder = columnOrder.toImmutableList()
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
     * @param columnOrder The visual column order. Must contain every [DatePickerColumn] exactly once.
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
        columnOrder = columnOrder.toImmutableList()
    )

    /**
     * Creates layout options for [com.kez.picker.date.YearMonthPicker] columns.
     *
     * Pass `null` for a column weight when [com.kez.picker.date.YearMonthPicker] should use
     * `pickerModifier` to define that column width instead of filling weighted row space.
     *
     * @param yearWeight The year column weight.
     * @param monthWeight The month column weight.
     * @param columnOrder The visual column order. Must contain every [YearMonthPickerColumn] exactly once.
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
        columnOrder = columnOrder.toImmutableList()
    )

    /**
     * Creates semantics configuration for one picker column.
     *
     * Value descriptions are configured with [itemFormat].
     *
     * @param pickerLabel Optional label used as the accessibility prefix for the picker column.
     * @param previousItemActionLabel Accessibility action label for selecting the previous item. Pass null or blank to omit the action.
     * @param nextItemActionLabel Accessibility action label for selecting the next item. Pass null or blank to omit the action.
     * @return A [PickerSemantics] instance with the specified semantics behavior.
     */
    fun semantics(
        pickerLabel: String? = null,
        previousItemActionLabel: String? = PreviousItemActionLabel,
        nextItemActionLabel: String? = NextItemActionLabel
    ): PickerSemantics = PickerSemantics(
        pickerLabel = pickerLabel,
        previousItemActionLabel = previousItemActionLabel,
        nextItemActionLabel = nextItemActionLabel
    )

    /**
     * Creates value formatting for one picker column.
     *
     * @param itemText Text displayed for each item value.
     * @param itemContentDescription Optional accessibility description for each item value. When null,
     * [itemText] is used as the default value description.
     * @return A [PickerItemFormat] instance with the specified value formatters.
     */
    fun <T : Any> itemFormat(
        itemText: (T) -> String = { it.toString() },
        itemContentDescription: ((T) -> String)? = null
    ): PickerItemFormat<T> = PickerItemFormat(
        itemText = itemText,
        itemContentDescription = itemContentDescription
    )

    /**
     * Creates value formatting for a time picker.
     *
     * @param hourItemText Text displayed for each hour value.
     * @param minuteItemText Text displayed for each minute value.
     * @param periodItemText Text displayed for each AM/PM value.
     * @param hourItemContentDescription Optional accessibility description for each hour value.
     * @param minuteItemContentDescription Optional accessibility description for each minute value.
     * @param periodItemContentDescription Optional accessibility description for each AM/PM value.
     * @return A [TimePickerFormat] instance with the specified value formatters.
     */
    fun timePickerFormat(
        hourItemText: (Int) -> String = { it.toString() },
        minuteItemText: (Int) -> String = { it.toString() },
        periodItemText: (TimePeriod) -> String = { it.name },
        hourItemContentDescription: ((Int) -> String)? = null,
        minuteItemContentDescription: ((Int) -> String)? = null,
        periodItemContentDescription: ((TimePeriod) -> String)? = null
    ): TimePickerFormat = TimePickerFormat(
        hour = itemFormat(
            itemText = hourItemText,
            itemContentDescription = hourItemContentDescription
        ),
        minute = itemFormat(
            itemText = minuteItemText,
            itemContentDescription = minuteItemContentDescription
        ),
        period = itemFormat(
            itemText = periodItemText,
            itemContentDescription = periodItemContentDescription
        )
    )

    /**
     * Creates value formatting for a date picker.
     *
     * @param yearItemText Text displayed for each year value.
     * @param monthItemText Text displayed for each month value.
     * @param dayItemText Text displayed for each day value.
     * @param yearItemContentDescription Optional accessibility description for each year value.
     * @param monthItemContentDescription Optional accessibility description for each month value.
     * @param dayItemContentDescription Optional accessibility description for each day value.
     * @return A [DatePickerFormat] instance with the specified value formatters.
     */
    fun datePickerFormat(
        yearItemText: (Int) -> String = { it.toString() },
        monthItemText: (Int) -> String = { it.toString() },
        dayItemText: (Int) -> String = { it.toString() },
        yearItemContentDescription: ((Int) -> String)? = null,
        monthItemContentDescription: ((Int) -> String)? = null,
        dayItemContentDescription: ((Int) -> String)? = null
    ): DatePickerFormat = DatePickerFormat(
        year = itemFormat(
            itemText = yearItemText,
            itemContentDescription = yearItemContentDescription
        ),
        month = itemFormat(
            itemText = monthItemText,
            itemContentDescription = monthItemContentDescription
        ),
        day = itemFormat(
            itemText = dayItemText,
            itemContentDescription = dayItemContentDescription
        )
    )

    /**
     * Creates value formatting for a year-month picker.
     *
     * @param yearItemText Text displayed for each year value.
     * @param monthItemText Text displayed for each month value.
     * @param yearItemContentDescription Optional accessibility description for each year value.
     * @param monthItemContentDescription Optional accessibility description for each month value.
     * @return A [YearMonthPickerFormat] instance with the specified value formatters.
     */
    fun yearMonthPickerFormat(
        yearItemText: (Int) -> String = { it.toString() },
        monthItemText: (Int) -> String = { it.toString() },
        yearItemContentDescription: ((Int) -> String)? = null,
        monthItemContentDescription: ((Int) -> String)? = null
    ): YearMonthPickerFormat = YearMonthPickerFormat(
        year = itemFormat(
            itemText = yearItemText,
            itemContentDescription = yearItemContentDescription
        ),
        month = itemFormat(
            itemText = monthItemText,
            itemContentDescription = monthItemContentDescription
        )
    )

    /**
     * Creates semantics configuration for a time picker.
     *
     * Shared previous/next action labels are applied to all child picker columns. Use
     * [TimePickerSemantics.copy] with [semantics] when one column needs a custom label.
     *
     * @param hourPickerLabel Accessibility label for the hour picker. Pass null to omit the picker label prefix.
     * @param minutePickerLabel Accessibility label for the minute picker. Pass null to omit the picker label prefix.
     * @param periodPickerLabel Accessibility label for the AM/PM picker in 12-hour time. Pass null to omit the picker label prefix.
     * @param previousItemActionLabel Accessibility action label used by child pickers to select the previous item.
     * @param nextItemActionLabel Accessibility action label used by child pickers to select the next item.
     * @return A [TimePickerSemantics] instance with the specified semantics behavior.
     */
    fun timePickerSemantics(
        hourPickerLabel: String? = "Hour",
        minutePickerLabel: String? = "Minute",
        periodPickerLabel: String? = "AM/PM",
        previousItemActionLabel: String? = PreviousItemActionLabel,
        nextItemActionLabel: String? = NextItemActionLabel
    ): TimePickerSemantics = TimePickerSemantics(
        hour = semantics(
            pickerLabel = hourPickerLabel,
            previousItemActionLabel = previousItemActionLabel,
            nextItemActionLabel = nextItemActionLabel
        ),
        minute = semantics(
            pickerLabel = minutePickerLabel,
            previousItemActionLabel = previousItemActionLabel,
            nextItemActionLabel = nextItemActionLabel
        ),
        period = semantics(
            pickerLabel = periodPickerLabel,
            previousItemActionLabel = previousItemActionLabel,
            nextItemActionLabel = nextItemActionLabel
        )
    )

    /**
     * Creates semantics configuration for a date picker.
     *
     * @param yearPickerLabel Accessibility label for the year picker. Pass null to omit the picker label prefix.
     * @param monthPickerLabel Accessibility label for the month picker. Pass null to omit the picker label prefix.
     * @param dayPickerLabel Accessibility label for the day picker. Pass null to omit the picker label prefix.
     * @param previousItemActionLabel Accessibility action label used by child pickers to select the previous item.
     * @param nextItemActionLabel Accessibility action label used by child pickers to select the next item.
     * @return A [DatePickerSemantics] instance with the specified semantics behavior.
     */
    fun datePickerSemantics(
        yearPickerLabel: String? = "Year",
        monthPickerLabel: String? = "Month",
        dayPickerLabel: String? = "Day",
        previousItemActionLabel: String? = PreviousItemActionLabel,
        nextItemActionLabel: String? = NextItemActionLabel
    ): DatePickerSemantics = DatePickerSemantics(
        year = semantics(
            pickerLabel = yearPickerLabel,
            previousItemActionLabel = previousItemActionLabel,
            nextItemActionLabel = nextItemActionLabel
        ),
        month = semantics(
            pickerLabel = monthPickerLabel,
            previousItemActionLabel = previousItemActionLabel,
            nextItemActionLabel = nextItemActionLabel
        ),
        day = semantics(
            pickerLabel = dayPickerLabel,
            previousItemActionLabel = previousItemActionLabel,
            nextItemActionLabel = nextItemActionLabel
        )
    )

    /**
     * Creates semantics configuration for a date range picker.
     *
     * @param start Accessibility configuration for the start date picker.
     * @param end Accessibility configuration for the end date picker.
     * @return A [DateRangePickerSemantics] instance with the specified semantics behavior.
     */
    fun dateRangePickerSemantics(
        start: DatePickerSemantics = datePickerSemantics(
            yearPickerLabel = "Start year",
            monthPickerLabel = "Start month",
            dayPickerLabel = "Start day"
        ),
        end: DatePickerSemantics = datePickerSemantics(
            yearPickerLabel = "End year",
            monthPickerLabel = "End month",
            dayPickerLabel = "End day"
        )
    ): DateRangePickerSemantics = DateRangePickerSemantics(
        start = start,
        end = end
    )

    /**
     * Creates semantics configuration for a year-month picker.
     *
     * @param yearPickerLabel Accessibility label for the year picker. Pass null to omit the picker label prefix.
     * @param monthPickerLabel Accessibility label for the month picker. Pass null to omit the picker label prefix.
     * @param previousItemActionLabel Accessibility action label used by child pickers to select the previous item.
     * @param nextItemActionLabel Accessibility action label used by child pickers to select the next item.
     * @return A [YearMonthPickerSemantics] instance with the specified semantics behavior.
     */
    fun yearMonthPickerSemantics(
        yearPickerLabel: String? = "Year",
        monthPickerLabel: String? = "Month",
        previousItemActionLabel: String? = PreviousItemActionLabel,
        nextItemActionLabel: String? = NextItemActionLabel
    ): YearMonthPickerSemantics = YearMonthPickerSemantics(
        year = semantics(
            pickerLabel = yearPickerLabel,
            previousItemActionLabel = previousItemActionLabel,
            nextItemActionLabel = nextItemActionLabel
        ),
        month = semantics(
            pickerLabel = monthPickerLabel,
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
 * @param dividerThickness The thickness of the selection dividers.
 * @param dividerShape The shape of the selection dividers.
 * @param dividerWidth The width of the selection dividers.
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
    val dividerThickness: Dp,
    val dividerShape: Shape,
    val dividerWidth: PickerDividerWidth,
    val isDividerVisible: Boolean
)

/**
 * Describes how wide the selection dividers of a [Picker] should be rendered.
 *
 * @see PickerDefaults.style
 */
@Immutable
sealed interface PickerDividerWidth {
    /**
     * The divider fills the full width of the picker column.
     */
    data object Fill : PickerDividerWidth

    /**
     * The divider spans a [fraction] of the picker column width, centered horizontally.
     *
     * @param fraction The portion of the column width to occupy, in `0f..1f`.
     */
    @Immutable
    data class Fraction(val fraction: Float) : PickerDividerWidth {
        init {
            require(fraction in 0f..1f) {
                "PickerDividerWidth.Fraction.fraction must be in 0f..1f, but was $fraction."
            }
        }
    }

    /**
     * The divider has a fixed [width], centered horizontally.
     *
     * @param width The absolute divider width. Must not be negative.
     */
    @Immutable
    data class Fixed(val width: Dp) : PickerDividerWidth {
        init {
            require(width.value >= 0f) {
                "PickerDividerWidth.Fixed.width must not be negative, but was $width."
            }
        }
    }
}

/**
 * Describes the single selection band drawn by composite pickers (e.g.
 * [com.kez.picker.time.TimePicker], [com.kez.picker.date.DatePicker]).
 *
 * The band spans the whole picker width (minus [horizontalInset] on each side) and stays centered,
 * so the selection lines stay aligned regardless of per-column widths and column spacing.
 *
 * @param color The color of the band lines.
 * @param thickness The thickness of each band line. Must not be negative.
 * @param shape The shape of each band line.
 * @param horizontalInset The inset applied to each side of the band. Must not be negative.
 * @param isVisible Whether the band is drawn.
 * @see PickerDefaults.selectionIndicator
 */
@Immutable
data class PickerSelectionIndicator(
    val color: Color,
    val thickness: Dp,
    val shape: Shape,
    val horizontalInset: Dp,
    val isVisible: Boolean
) {
    init {
        require(thickness.value >= 0f) {
            "PickerSelectionIndicator.thickness must not be negative, but was $thickness."
        }
        require(horizontalInset.value >= 0f) {
            "PickerSelectionIndicator.horizontalInset must not be negative, but was $horizontalInset."
        }
    }
}

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
 * @param columnOrder The visual column order. Must contain every [TimePickerColumn] exactly once.
 * [TimePickerColumn.PERIOD] is still required in 24-hour mode, but its position is ignored.
 * @see PickerDefaults.timePickerLayout
 */
@Immutable
data class TimePickerLayout(
    val periodWeight: Float?,
    val hourWeight: Float?,
    val minuteWeight: Float?,
    val columnOrder: ImmutableList<TimePickerColumn> = persistentListOf(
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
 * @param columnOrder The visual column order. Must contain every [DatePickerColumn] exactly once.
 * @see PickerDefaults.datePickerLayout
 */
@Immutable
data class DatePickerLayout(
    val yearWeight: Float?,
    val monthWeight: Float?,
    val dayWeight: Float?,
    val columnOrder: ImmutableList<DatePickerColumn> = persistentListOf(
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
 * @param columnOrder The visual column order. Must contain every [YearMonthPickerColumn] exactly once.
 * @see PickerDefaults.yearMonthPickerLayout
 */
@Immutable
data class YearMonthPickerLayout(
    val yearWeight: Float?,
    val monthWeight: Float?,
    val columnOrder: ImmutableList<YearMonthPickerColumn> = persistentListOf(
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
