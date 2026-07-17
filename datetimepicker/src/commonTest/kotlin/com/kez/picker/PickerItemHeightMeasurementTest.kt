package com.kez.picker

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class PickerItemHeightMeasurementTest {

    @Test
    fun pickerItemHeightTexts_withoutProbeFormatsEveryItemExactly() {
        listOf(10, 100, 10_000).forEach { itemCount ->
            var formatCallCount = 0
            val format = PickerDefaults.itemFormat<Int>(
                itemText = { item ->
                    formatCallCount += 1
                    "value-$item"
                }
            )

            val texts = pickerItemHeightTexts(
                items = (1..itemCount).toList(),
                format = format
            )

            assertEquals(itemCount, formatCallCount, "itemCount=$itemCount")
            assertEquals(itemCount, texts.size, "itemCount=$itemCount")
            assertEquals("value-1", texts.first())
            assertEquals("value-$itemCount", texts.last())
        }
    }

    @Test
    fun pickerItemHeightTexts_withProbeSkipsPerItemFormatting() {
        listOf(10, 100, 10_000).forEach { itemCount ->
            var formatCallCount = 0
            val format = PickerDefaults.itemFormat<Int>(
                itemText = { item ->
                    formatCallCount += 1
                    "value-$item"
                },
                itemHeightProbeText = "value-0123456789"
            )

            val texts = pickerItemHeightTexts(
                items = (1..itemCount).toList(),
                format = format
            )

            assertEquals(0, formatCallCount, "itemCount=$itemCount")
            assertEquals(listOf("value-0123456789"), texts)
        }
    }

    @Test
    fun temporalDefaultFormatsUseBoundedNumericHeightProbes() {
        val time = PickerDefaults.timePickerFormat()
        val date = PickerDefaults.datePickerFormat()
        val yearMonth = PickerDefaults.yearMonthPickerFormat()

        assertEquals("0123456789", time.hour.itemHeightProbeText)
        assertEquals("0123456789", time.minute.itemHeightProbeText)
        assertEquals("AMPM", time.period.itemHeightProbeText)
        assertEquals("0123456789", date.year.itemHeightProbeText)
        assertEquals("0123456789", date.month.itemHeightProbeText)
        assertEquals("0123456789", date.day.itemHeightProbeText)
        assertEquals("0123456789", yearMonth.year.itemHeightProbeText)
        assertEquals("0123456789", yearMonth.month.itemHeightProbeText)
    }

    @Test
    fun temporalCustomFormatterDefaultsBackToExactHeightMeasurement() {
        val time = PickerDefaults.timePickerFormat(
            hourItemText = { "$it hour" }
        )
        val date = PickerDefaults.datePickerFormat(
            yearItemText = { "$it year" }
        )
        val yearMonth = PickerDefaults.yearMonthPickerFormat(
            monthItemText = { "$it month" }
        )

        assertNull(time.hour.itemHeightProbeText)
        assertNull(date.year.itemHeightProbeText)
        assertNull(yearMonth.month.itemHeightProbeText)
    }

    @Test
    fun temporalCustomFormatterAcceptsExplicitHeightProbe() {
        val format = PickerDefaults.datePickerFormat(
            yearItemText = { "${it}년" },
            yearItemHeightProbeText = "0123456789년"
        )

        assertEquals("0123456789년", format.year.itemHeightProbeText)
    }

    @Test
    fun pickerItemFormat_rejectsBlankHeightProbe() {
        assertFailsWith<IllegalArgumentException> {
            PickerDefaults.itemFormat<Int>(itemHeightProbeText = "   ")
        }
    }
}
