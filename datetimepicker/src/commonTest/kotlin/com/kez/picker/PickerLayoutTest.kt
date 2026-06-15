package com.kez.picker

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PickerLayoutTest {

    @Test
    fun timePickerLayout_defaultColumnOrder_isPeriodHourMinute() {
        val layout = PickerDefaults.timePickerLayout()

        assertEquals(
            listOf(
                TimePickerColumn.PERIOD,
                TimePickerColumn.HOUR,
                TimePickerColumn.MINUTE
            ),
            layout.columnOrder
        )
    }

    @Test
    fun datePickerLayout_acceptsCustomColumnOrder() {
        val layout = PickerDefaults.datePickerLayout(
            columnOrder = listOf(
                DatePickerColumn.MONTH,
                DatePickerColumn.DAY,
                DatePickerColumn.YEAR
            )
        )

        assertEquals(
            listOf(
                DatePickerColumn.MONTH,
                DatePickerColumn.DAY,
                DatePickerColumn.YEAR
            ),
            layout.columnOrder
        )
    }

    @Test
    fun yearMonthPickerLayout_acceptsCustomColumnOrder() {
        val layout = PickerDefaults.yearMonthPickerLayout(
            columnOrder = listOf(
                YearMonthPickerColumn.MONTH,
                YearMonthPickerColumn.YEAR
            )
        )

        assertEquals(
            listOf(
                YearMonthPickerColumn.MONTH,
                YearMonthPickerColumn.YEAR
            ),
            layout.columnOrder
        )
    }

    @Test
    fun timePickerLayout_defensivelyCopiesColumnOrder() {
        val columnOrder = mutableListOf(
            TimePickerColumn.HOUR,
            TimePickerColumn.MINUTE,
            TimePickerColumn.PERIOD
        )
        val layout = PickerDefaults.timePickerLayout(columnOrder = columnOrder)

        columnOrder[0] = TimePickerColumn.PERIOD

        assertEquals(
            listOf(
                TimePickerColumn.HOUR,
                TimePickerColumn.MINUTE,
                TimePickerColumn.PERIOD
            ),
            layout.columnOrder
        )
    }

    @Test
    fun datePickerLayout_rejectsDuplicateColumnOrder() {
        assertFailsWith<IllegalArgumentException> {
            PickerDefaults.datePickerLayout(
                columnOrder = listOf(
                    DatePickerColumn.YEAR,
                    DatePickerColumn.MONTH,
                    DatePickerColumn.MONTH
                )
            )
        }
    }

    @Test
    fun timePickerLayout_rejectsMissingColumnOrder() {
        assertFailsWith<IllegalArgumentException> {
            PickerDefaults.timePickerLayout(
                columnOrder = listOf(
                    TimePickerColumn.HOUR,
                    TimePickerColumn.MINUTE
                )
            )
        }
    }

    @Test
    fun pickerDividerWidthFraction_acceptsValuesInUnitRange() {
        assertEquals(0f, PickerDividerWidth.Fraction(0f).fraction)
        assertEquals(0.8f, PickerDividerWidth.Fraction(0.8f).fraction)
        assertEquals(1f, PickerDividerWidth.Fraction(1f).fraction)
    }

    @Test
    fun pickerDividerWidthFraction_rejectsValuesOutsideUnitRange() {
        assertFailsWith<IllegalArgumentException> { PickerDividerWidth.Fraction(-0.1f) }
        assertFailsWith<IllegalArgumentException> { PickerDividerWidth.Fraction(1.1f) }
    }

    @Test
    fun pickerDividerWidthFixed_rejectsNegativeWidth() {
        assertEquals(40.dp, PickerDividerWidth.Fixed(40.dp).width)
        assertFailsWith<IllegalArgumentException> { PickerDividerWidth.Fixed((-1).dp) }
    }

    @Test
    fun pickerSelectionIndicator_rejectsNegativeThicknessOrInset() {
        assertFailsWith<IllegalArgumentException> {
            PickerSelectionIndicator(
                color = Color.Black,
                thickness = (-1).dp,
                shape = RectangleShape,
                horizontalInset = 0.dp,
                isVisible = true
            )
        }
        assertFailsWith<IllegalArgumentException> {
            PickerSelectionIndicator(
                color = Color.Black,
                thickness = 1.dp,
                shape = RectangleShape,
                horizontalInset = (-1).dp,
                isVisible = true
            )
        }
    }

    @Test
    fun pickerSelectionIndicator_defaults_areDerivedFromStyle() {
        val style = PickerStyle(
            visibleItemsCount = 3,
            colors = PickerColors(
                dividerColor = Color.Red,
                selectedItemBackgroundColor = Color.Transparent,
                textColor = Color.Gray,
                selectedTextColor = Color.Black
            ),
            textStyles = PickerTextStyles(
                textStyle = TextStyle.Default,
                selectedTextStyle = TextStyle.Default
            ),
            selectedItemBackgroundShape = RectangleShape,
            itemPadding = PaddingValues(0.dp),
            fadingEdgeGradient = PickerDefaults.fadingEdgeGradient(),
            horizontalAlignment = Alignment.CenterHorizontally,
            dividerThickness = 3.dp,
            dividerShape = RectangleShape,
            dividerWidth = PickerDividerWidth.Fixed(40.dp),
            isDividerVisible = false
        )

        val indicator = PickerDefaults.selectionIndicator(style)

        assertEquals(Color.Red, indicator.color)
        assertEquals(3.dp, indicator.thickness)
        assertEquals(RectangleShape, indicator.shape)
        assertEquals(0.dp, indicator.horizontalInset)
        assertEquals(false, indicator.isVisible)
    }
}
