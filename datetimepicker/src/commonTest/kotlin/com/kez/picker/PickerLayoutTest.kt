package com.kez.picker

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
}
